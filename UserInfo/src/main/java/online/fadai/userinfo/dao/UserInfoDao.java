package online.fadai.userinfo.dao;

import online.fadai.pojo.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserInfoDao {
    @Select("""
            select user_key,user_username,user_phone,user_password,user_photo,user_friend,user_group,user_blacklist from user_info
            """)
    @Results(id = "userInfoRes", value = {
            @Result(id = true, column = "user_key", property = "userKey"),
            @Result(column = "user_username", property = "username"),
            @Result(column = "user_phone", property = "phone"),
            @Result(column = "user_password", property = "password"),
            @Result(column = "user_photo", property = "userPhoto"),
            @Result(column = "user_friend", property = "friends"),
            @Result(column = "user_group", property = "groups"),
            @Result(column = "user_blacklist", property = "blacklist")
    })
    List<UserInfo> selectAllInfo();

    @Select("""
            select user_key,user_phone,user_password from user_info where user_phone = #{phone}
            """)
    @ResultMap("userInfoRes")
    UserInfo selectPasswordByPhone(int phone);

    @Select("""
            select user_key,user_username,user_phone,user_password,user_photo,user_friend,user_group,user_blacklist from user_info where user_key = #{key}
            """)
    @ResultMap("userInfoRes")
    UserInfo selectUserInfo(long key);

    @Select("""
            select user_key,user_username,user_photo from user_info where user_key = #{key}
            """)
    @ResultMap("userInfoRes")
    UserInfo selectUserSimpleInfo(long key);


    @Insert("""
            insert into user_info(user_key,user_username,user_phone,user_password) value (#{key},#{username},#{phone},#{password})
            """)
    int insertUser(long key, String username, int phone, String password);

    @Update("""
            update user_info set user_username = #{username} where user_key = #{key}
            """)
    int updateUsername(long key, String username);

    @Update("""
            update user_info set user_password = #{password} where user_key = #{key}
            """)
    int updatePassword(long key, String password);

    @Update("""
            update user_info set user_photo = #{photo} where user_key = #{key}
            """)
    int updatePhoto(long key, String photo);

    @Update("""
            update user_info set user_friend = #{friend} where user_key = #{key};
            """)
    int updateFriend(long key, String friend);

    @Update("""
            update user_info set user_group = #{group} where user_key = #{key};
            """)
    int updateGroup(long key, String group);

    @Update("""
            update user_info set user_blacklist = #{blackList} where user_key = #{key}
            """)
    int updateBlackList(long key,String blackList);

}
