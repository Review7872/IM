package online.fadai.userinfo.dao;

import online.fadai.pojo.GroupInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface GroupDao {
    @Select("""
            select group_key,group_name,group_master,group_friend,group_photo from group_info where group_key = #{groupKey}
            """)
    @Results(id = "groupInfoMap", value = {
            @Result(id = true, column = "group_key", property = "groupKey"),
            @Result(column = "group_name", property = "groupName"),
            @Result(column = "group_master", property = "groupMaster"),
            @Result(column = "group_friend", property = "groupFriend"),
            @Result(column = "group_photo", property = "groupPhoto")
    })
    GroupInfo selectGroupInfo(long groupKey);

    @Select("""
            select group_key,group_name,group_photo from group_info where group_key = #{groupKey}
            """)
    GroupInfo selectGroupSimpleInfo(long groupKey);

    @Insert("""
            insert into group_info(group_key,group_name,group_master,group_friend) value (#{groupKey},#{groupName},#{groupMaster},#{friends})
            """)
    int insertGroup(long groupKey, String groupName, long groupMaster, String friends);

    @Update("""
            update group_info set group_master = #{masterKey} where group_key = #{groupKey}
            """)
    int updateGroupMaster(long groupKey, long masterKey);

    @Update("""
            update group_info set group_name = #{name} where group_key = #{groupKey}
            """)
    int updateGroupName(long groupKey, String name);

    @Update("""
            update group_info set group_photo = #{photo} where group_key = #{groupKey}
            """)
    int updateGroupPhoto(long groupKey, String photo);

    @Update("""
            update group_info set group_friend = #{friend} where group_key = #{groupKey}
            """)
    int updateGroupFriend(long groupKey, String friend);
}
