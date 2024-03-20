package online.fadai.userinfo.service.impl;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import online.fadai.pojo.ApplyInfo;
import online.fadai.pojo.GroupInfo;
import online.fadai.pojo.Msg;
import online.fadai.pojo.UserInfo;
import online.fadai.service.ApplyInfoService;
import online.fadai.service.MsgService;
import online.fadai.userinfo.dao.ApplyDao;
import online.fadai.userinfo.dao.GroupDao;
import online.fadai.userinfo.dao.UserInfoDao;
import online.fadai.userinfo.util.SnowflakeIdGenerator;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@DubboService
public class ApplyInfoServiceImpl implements ApplyInfoService {
    @Resource
    private ApplyDao applyDao;
    @Resource
    private UserInfoDao userInfoDao;
    @Resource
    private GroupDao groupDao;
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @DubboReference
    private MsgService msgService;
    @Override
    public ApplyInfo selectApplyInfo(long userKey){
        return applyDao.selectApplyInfo(userKey);
    }
    @Override
    public List<ApplyInfo> selectUserApplyInfo(long userKey, int num) {
        return applyDao.selectUserApplyInfo(userKey, (num-1) * 10);
    }

    @Override
    public List<ApplyInfo> selectGroupApplyInfo(long groupKey, int num) {
        return applyDao.selectGroupApplyInfo(groupKey, (num-1) * 10);
    }

    /**
     * 添加好友：
     * 发起申请->收到申请->同意/拒绝->添加进双方列表/忽略
     */
    @Override
    public int insertApply(long applyA, long applyB, int applyType) {
        if (applyType == 0) {
            int i = applyDao.insertApply(snowflakeIdGenerator.nextId(), applyA, applyB, new Date(System.currentTimeMillis()),
                    0, applyType);
            msgService.sendMsg(new Msg(203,String.valueOf(applyA),String.valueOf(applyB),""));
            return i;
        } else if (applyType == 1) {
            Long masterKey = groupDao.selectGroupInfo(applyB).getGroupMaster();
            int i = applyDao.insertApply(snowflakeIdGenerator.nextId(), applyA, applyB, new Date(System.currentTimeMillis()),
                    0, applyType);
            msgService.sendMsg(new Msg(204,String.valueOf(applyA),String.valueOf(masterKey),""));
            return i;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public int updateResult(long applyKey, int applyResult) {
        ApplyInfo applyInfo = applyDao.selectApplyInfo(applyKey);
        int i = applyDao.updateResult(applyKey, applyResult);
        if (applyResult == 2) {
            // 被拒绝
            msgService.sendMsg(new Msg(207,String.valueOf(applyKey),String.valueOf(applyInfo.getApplyA()),""));
            return i;
        } else if (applyResult == 1) {
            // 同意了申请
            if (applyInfo.getApplyType() == 0) {
                // 申请好友
                UserInfo userInfo = userInfoDao.selectUserInfo(applyInfo.getApplyA());
                Set<Long> friendsSet = userInfo.getFriendsSet();
                friendsSet.add(applyInfo.getApplyB());
                userInfoDao.updateFriend(applyInfo.getApplyA(), JSON.toJSONString(friendsSet));
                UserInfo userInfo1 = userInfoDao.selectUserInfo(applyInfo.getApplyB());
                Set<Long> friendsSet1 = userInfo1.getFriendsSet();
                friendsSet1.add(applyInfo.getApplyA());
                userInfoDao.updateFriend(applyInfo.getApplyB(), JSON.toJSONString(friendsSet1));
                // 告诉申请者对方已经同意，这里的思想是被申请者同意后直接调用接口重新获取好友列表，所以只需要告诉申请者对方同意
                msgService.sendMsg(new Msg(205,String.valueOf(applyInfo.getApplyB()),String.valueOf(applyInfo.getApplyA()),""));
            } else if (applyInfo.getApplyType() == 1) {
                // 申请群聊
                UserInfo userInfo = userInfoDao.selectUserInfo(applyInfo.getApplyA());
                Set<Long> groupsSet = userInfo.getGroupsSet();
                groupsSet.add(applyInfo.getApplyB());
                userInfoDao.updateGroup(applyInfo.getApplyA(), JSON.toJSONString(groupsSet));
                GroupInfo groupInfo = groupDao.selectGroupInfo(applyInfo.getApplyB());
                Set<Long> groupFriendSet = groupInfo.getGroupFriendSet();
                groupFriendSet.add(applyInfo.getApplyA());
                String friendJson = JSON.toJSONString(groupFriendSet);
                groupDao.updateGroupFriend(applyInfo.getApplyB(), friendJson);
                redisTemplate.delete(String.valueOf(groupInfo.getGroupKey()));
                // 告诉所有群友群里来新人了，这里需要通知所有群友重新拉取列表，send方为新成员
                msgService.sendMsg(new Msg(206,String.valueOf(applyInfo.getApplyA()),friendJson,""));
            } else {
                throw new RuntimeException();
            }
            return i;
        } else {
            throw new RuntimeException();
        }
    }

}
