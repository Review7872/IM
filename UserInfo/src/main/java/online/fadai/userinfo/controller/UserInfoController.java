package online.fadai.userinfo.controller;

import jakarta.annotation.Resource;
import online.fadai.pojo.UserInfo;
import online.fadai.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/info/userInfo")
public class UserInfoController {
    @Resource
    private UserInfoService userInfoService;
    @PostMapping("/insertUser")
    public int insertUser(@RequestBody UserInfo userInfo){
        return userInfoService.insertUser(userInfo);
    }
    @PostMapping("/userLogin")
    public String userLogin(@RequestBody UserInfo userInfo){
        return userInfoService.userLogin(userInfo);
    }
    @PostMapping("/userForget")
    public int userForget(){
        return 1;
    }
    @GetMapping("/onlineStatus")
    public int onlineStatus(String userJwt){
        return userInfoService.onlineStatus(userJwt);
    }
    @GetMapping("/selectUserInfo")
    public UserInfo selectUserInfo(long userKey){
        return userInfoService.selectUserInfo(userKey);
    }
    @GetMapping("/selectUserSimpleInfo")
    public UserInfo selectUserSimpleInfo(long userKey){
        return userInfoService.selectUserSimpleInfo(userKey);
    }
    @PostMapping("/delFriend")
    public int delFriend(@RequestBody UserInfo userInfo){
        return userInfoService.delFriend(userInfo.getUserKey(),userInfo.getFriendKey());
    }
    @PostMapping("/updatePassword")
    public int updatePassword(@RequestBody UserInfo userInfo){
        return userInfoService.updatePassword(userInfo.getUserKey(),userInfo.getPassword());
    }
    @PostMapping("/updateUsername")
    public int updateUsername(@RequestBody UserInfo userInfo){
        return userInfoService.updateUsername(userInfo.getUserKey(),userInfo.getUsername());
    }
    @PostMapping("/updatePhoto")
    public int updatePhoto(@RequestBody UserInfo userInfo){
        return userInfoService.updatePhoto(userInfo.getUserKey(),userInfo.getUserPhoto());
    }
}
