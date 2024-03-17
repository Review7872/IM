package online.fadai.userinfo.controller;

import jakarta.annotation.Resource;
import online.fadai.pojo.GroupInfo;
import online.fadai.pojo.UserInfo;
import online.fadai.service.GroupInfoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/info/groupInfo")
public class GroupInfoController {
    @Resource
    private GroupInfoService groupInfoService;
    @GetMapping("/selectGroupInfo")
    public GroupInfo selectGroupInfo(long groupKey){
        return groupInfoService.selectGroupInfo(groupKey);
    }
    @GetMapping("/selectGroupSimpleInfo")
    public GroupInfo selectGroupSimpleInfo(long groupKey){
        return groupInfoService.selectGroupSimpleInfo(groupKey);
    }
    @PostMapping("/insertGroup")
    public int insertGroup(@RequestBody GroupInfo groupInfo){
        return groupInfoService.insertGroup(groupInfo);
    }
    @PostMapping("/delGroup")
    public int delGroup(@RequestBody UserInfo userInfo){
        return groupInfoService.delGroup(userInfo.getUserKey(),userInfo.getGroupKey());
    }
    @PostMapping("/updateGroupMaster")
    public int updateGroupMaster(@RequestBody GroupInfo groupInfo){
        return groupInfoService.updateGroupMaster(groupInfo);
    }
    @PostMapping("/updateGroupName")
    public int updateGroupName(@RequestBody GroupInfo groupInfo){
        return groupInfoService.updateGroupName(groupInfo);
    }
    @PostMapping("/updateGroupPhoto")
    public int updateGroupPhoto(@RequestBody GroupInfo groupInfo){
        return groupInfoService.updateGroupPhoto(groupInfo);
    }
    @PostMapping("/updateGroupFriend")
    public int updateGroupFriend(@RequestBody GroupInfo groupInfo){
        return groupInfoService.updateGroupFriend(groupInfo);
    }
}
