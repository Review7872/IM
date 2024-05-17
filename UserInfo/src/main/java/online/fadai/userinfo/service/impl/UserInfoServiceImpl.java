package online.fadai.userinfo.service.impl;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import online.fadai.pojo.UserInfo;
import online.fadai.service.UserInfoService;
import online.fadai.userinfo.dao.UserInfoDao;
import online.fadai.userinfo.util.Jwt;
import online.fadai.userinfo.util.SnowflakeIdGenerator;
import org.apache.dubbo.config.annotation.DubboService;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@DubboService
@Transactional
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private UserInfoDao userInfoDao;
    @Resource
    private StringEncryptor encryptor;
    @Resource
    private Jwt jwt;
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    private final Lock lock = new ReentrantLock();

    @Override
    public int insertUser(UserInfo userInfo) {
        return userInfoDao.insertUser(snowflakeIdGenerator.nextId(),
                userInfo.getUsername(), userInfo.getPhone(), encryptor.encrypt(userInfo.getPassword()));
    }

    /**
     * 用户登录时会颁发JWT，并且存储于Redis中，断开连接时，netty集群会负责将jwt移除
     * 后续用户发起请求会解析JWT拿到用户Key，去Redis中判断是否存在
     *
     * @param userInfo 用户信息
     * @return JWT
     */
    @Override
    public String userLogin(UserInfo userInfo) {
        UserInfo info = userInfoDao.selectPasswordByPhone(userInfo.getPhone());
        if (info == null || !encryptor.decrypt(info.getPassword()).equals(userInfo.getPassword())) {
            throw new RuntimeException();
        }
        String jwtInfo = jwt.generateJwtToken(String.valueOf(info.getUserKey()));
        redisTemplate.opsForValue().set(jwtInfo,"");
        redisTemplate.expire(jwtInfo,1,TimeUnit.DAYS);
        return jwtInfo;
    }

    /**
     * 判断JWT是否有效的方法，解析后拿去redis判断是否存在
     * 使用HASH对存储是因为要在netty中断连后根据key删除
     */
    @Override
    public int onlineStatus(String userJwt) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(userJwt)) ?1:0;
    }

    @Override
    public UserInfo selectUserInfo(long userKey) {
        return userInfoDao.selectUserInfo(userKey);
    }

    @Override
    public UserInfo selectUserSimpleInfo(long userKey) {
        String userInfoCache = redisTemplate.opsForValue().get(userKey);
        if (userInfoCache != null && !userInfoCache.isEmpty()) {
            return JSON.parseObject(userInfoCache, UserInfo.class);
        }
        lock.lock();
        try {
            userInfoCache = redisTemplate.opsForValue().get(userKey);
            if (userInfoCache != null && !userInfoCache.isEmpty()) {
                return JSON.parseObject(userInfoCache, UserInfo.class);
            }else {
                String key = String.valueOf(userKey);
                UserInfo userInfo = userInfoDao.selectUserSimpleInfo(userKey);
                redisTemplate.opsForValue().set(key,JSON.toJSONString(userInfo));
                redisTemplate.expire(key,7, TimeUnit.DAYS);
                return userInfo;
            }
        }finally {
            lock.unlock();
        }
    }

    @Override
    public int delFriend(long userKey, long friendKey) {
        UserInfo userInfo = userInfoDao.selectUserInfo(userKey);
        Set<Long> userFriendsSet = userInfo.getFriendsSet();
        userFriendsSet.remove(friendKey);
        int x = userInfoDao.updateFriend(userKey, JSON.toJSONString(userFriendsSet));
        UserInfo friendInfo = userInfoDao.selectUserInfo(friendKey);
        Set<Long> friendsSet = friendInfo.getFriendsSet();
        friendsSet.remove(userKey);
        int y = userInfoDao.updateFriend(friendKey, JSON.toJSONString(friendsSet));
        if (x + y != 2) {
            throw new RuntimeException();
        }
        return 1;
    }

    @Override
    public int updatePassword(long userKey, String password) {
        return userInfoDao.updatePassword(userKey, encryptor.encrypt(password));
    }

    @Override
    public int updateUsername(long userKey, String username) {
        redisTemplate.delete(String.valueOf(userKey));
        int i = userInfoDao.updateUsername(userKey, username);
        redisTemplate.delete(String.valueOf(userKey));
        return i;
    }

    @Override
    public int updatePhoto(long userKey, String photo) {
        redisTemplate.delete(String.valueOf(userKey));
        int i = userInfoDao.updatePhoto(userKey, photo);
        redisTemplate.delete(String.valueOf(userKey));
        return i;
    }
}
