package online.fadai.service;

import online.fadai.pojo.UserInfo;

public interface UserInfoService {
    int insertUser(UserInfo userInfo);

    String userLogin(UserInfo userInfo);

    int onlineStatus(String userJwt);

    UserInfo selectUserInfo(long userKey);

    UserInfo selectUserSimpleInfo(long userKey);

    int delFriend(long userKey, long friendKey);

    int updatePassword(long userKey, String password);

    int updateUsername(long userKey, String username);

    int updatePhoto(long userKey, String photo);

}
