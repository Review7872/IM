package online.fadai.service;

import online.fadai.pojo.GroupInfo;

public interface GroupInfoService {
    GroupInfo selectGroupInfo(long groupKey);

    GroupInfo selectGroupSimpleInfo(long groupKey);

    int insertGroup(GroupInfo groupInfo);

    int delGroup(long userKey, long groupKey);

    int updateGroupMaster(GroupInfo groupInfo);

    int updateGroupName(GroupInfo groupInfo);

    int updateGroupPhoto(GroupInfo groupInfo);

    int updateGroupFriend(GroupInfo groupInfo);
}
