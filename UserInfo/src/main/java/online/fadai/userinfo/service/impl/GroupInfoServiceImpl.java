package online.fadai.userinfo.service.impl;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import online.fadai.pojo.GroupInfo;
import online.fadai.pojo.UserInfo;
import online.fadai.service.GroupInfoService;
import online.fadai.userinfo.dao.GroupDao;
import online.fadai.userinfo.dao.UserInfoDao;
import online.fadai.userinfo.util.SnowflakeIdGenerator;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@DubboService
public class GroupInfoServiceImpl implements GroupInfoService {
    @Resource
    private UserInfoDao userInfoDao;
    @Resource
    private GroupDao groupDao;
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public GroupInfo selectGroupInfo(long groupKey) {
        String key = String.valueOf(groupKey);
        // 从redis中获取缓存
        String cacheObject = redisTemplate.opsForValue().get(key);
        // 判断缓存有效
        if (cacheObject != null && !cacheObject.isEmpty()) {
            return JSON.parseObject(cacheObject, GroupInfo.class);
        }
        Boolean lock = null;
        String uuid = null;
        // 添加分布式锁防止mysql压力过大
        try {
            // 锁标识
            uuid = UUID.randomUUID().toString();
            // 每个群有一把锁，过期时间为10s
            lock = redisTemplate.opsForValue().setIfAbsent(key+"INFO", uuid, 10, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(lock)) {
                // 拿到锁后才有资格查询数据库
                GroupInfo groupInfo = groupDao.selectGroupInfo(groupKey);
                // 缓存
                redisTemplate.opsForValue().set(key, JSON.toJSONString(groupInfo));
                // 设置过期时间
                redisTemplate.expire(key, 1, TimeUnit.DAYS);
                // 返回值
                return groupInfo;
            } else {
                // 没拿到锁
                throw new RuntimeException();
            }
        } finally {
            if (Boolean.TRUE.equals(lock)) {
                // 如果有拿到锁就释放锁
                String script = " if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end ";
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
                redisScript.setScriptSource(new StaticScriptSource(script));
                redisTemplate.execute(redisScript, Collections.singletonList(key+"INFO"), uuid);
            }
        }
    }
    @Override
    public GroupInfo selectGroupSimpleInfo(long groupKey){
        String key = groupKey + "Simple";
        // 从redis中获取缓存
        String cacheObject = redisTemplate.opsForValue().get(key);
        // 判断缓存有效
        if (cacheObject != null && !cacheObject.isEmpty()) {
            return JSON.parseObject(cacheObject, GroupInfo.class);
        }
        Boolean lock = null;
        String uuid = null;
        // 添加分布式锁防止mysql压力过大
        try {
            // 锁标识
            uuid = UUID.randomUUID().toString();
            // 每个群有一把锁，过期时间为10s
            lock = redisTemplate.opsForValue().setIfAbsent(key+"SIMPLE", uuid, 10, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(lock)) {
                // 拿到锁后才有资格查询数据库
                GroupInfo groupInfo = groupDao.selectGroupSimpleInfo(groupKey);
                // 缓存
                redisTemplate.opsForValue().set(key, JSON.toJSONString(groupInfo));
                // 设置过期时间
                redisTemplate.expire(key, 1, TimeUnit.DAYS);
                // 返回值
                return groupInfo;
            } else {
                // 没拿到锁
                throw new RuntimeException();
            }
        } finally {
            if (Boolean.TRUE.equals(lock)) {
                // 如果有拿到锁就释放锁
                String script = " if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end ";
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
                redisScript.setScriptSource(new StaticScriptSource(script));
                redisTemplate.execute(redisScript, Collections.singletonList(key+"SIMPLE"), uuid);
            }
        }
    }

    @Override
    public int insertGroup(GroupInfo groupInfo) {
        long snowId = snowflakeIdGenerator.nextId();
        UserInfo userInfo = userInfoDao.selectUserInfo(groupInfo.getGroupMaster());
        Set<Long> groupsSet = userInfo.getGroupsSet();
        groupsSet.add(groupInfo.getGroupMaster());
        userInfoDao.updateGroup(groupInfo.getGroupMaster(), JSON.toJSONString(groupsSet));
        return groupDao.insertGroup(snowId, groupInfo.getGroupName(), groupInfo.getGroupMaster(),JSON.toJSONString(groupsSet));
    }

    @Override
    public int delGroup(long userKey, long groupKey) {
        UserInfo userInfo = userInfoDao.selectUserInfo(userKey);
        Set<Long> groupsSet = userInfo.getGroupsSet();
        groupsSet.remove(groupKey);
        int x = userInfoDao.updateGroup(userKey, JSON.toJSONString(groupsSet));
        GroupInfo groupInfo = groupDao.selectGroupInfo(groupKey);
        Set<Long> groupFriendSet = groupInfo.getGroupFriendSet();
        groupFriendSet.remove(userKey);
        redisTemplate.delete(String.valueOf(groupInfo.getGroupKey()));
        int y = groupDao.updateGroupFriend(groupKey, JSON.toJSONString(groupFriendSet));
        if (x + y != 2) {
            throw new RuntimeException();
        }
        redisTemplate.delete(String.valueOf(groupInfo.getGroupKey()));
        return 1;
    }

    @Override
    public int updateGroupMaster(GroupInfo groupInfo) {
        redisTemplate.delete(String.valueOf(groupInfo.getGroupKey()));
        int i = groupDao.updateGroupMaster(groupInfo.getGroupKey(), groupInfo.getGroupMaster());
        redisTemplate.delete(String.valueOf(groupInfo.getGroupKey()));
        return i;
    }

    @Override
    public int updateGroupName(GroupInfo groupInfo) {
        redisTemplate.delete(String.valueOf(groupInfo.getGroupKey()));
        redisTemplate.delete(groupInfo.getGroupKey() + "Simple");
        int i = groupDao.updateGroupName(groupInfo.getGroupKey(), groupInfo.getGroupName());
        redisTemplate.delete(String.valueOf(groupInfo.getGroupKey()));
        redisTemplate.delete(groupInfo.getGroupKey() + "Simple");
        return i;
    }

    @Override
    public int updateGroupPhoto(GroupInfo groupInfo) {
        redisTemplate.delete(String.valueOf(groupInfo.getGroupKey()));
        redisTemplate.delete(groupInfo.getGroupKey() + "Simple");
        int i = groupDao.updateGroupPhoto(groupInfo.getGroupKey(), groupInfo.getGroupPhoto());
        redisTemplate.delete(String.valueOf(groupInfo.getGroupKey()));
        redisTemplate.delete(groupInfo.getGroupKey() + "Simple");
        return i;
    }

    @Override
    public int updateGroupFriend(GroupInfo groupInfo) {
        redisTemplate.delete(String.valueOf(groupInfo.getGroupKey()));
        int i = groupDao.updateGroupFriend(groupInfo.getGroupKey(), groupInfo.getGroupFriend());
        redisTemplate.delete(String.valueOf(groupInfo.getGroupKey()));
        return i;
    }
}
