package online.fadai.userinfo.controller;

import jakarta.annotation.Resource;
import online.fadai.pojo.ApplyInfo;
import online.fadai.service.ApplyInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/info/applyInfo")
public class ApplyInfoController {
    @Resource
    private ApplyInfoService applyInfoService;
    @GetMapping("/selectApplyInfo")
    public ApplyInfo selectApplyInfo(long applyKey){
        return applyInfoService.selectApplyInfo(applyKey);
    }
    @GetMapping("/selectUserApplyInfo")
    public List<ApplyInfo> selectUserApplyInfo(long userKey, int num){
        return applyInfoService.selectUserApplyInfo(userKey,num);
    }
    @GetMapping("/selectGroupApplyInfo")
    public List<ApplyInfo> selectGroupApplyInfo(long groupKey,int num){
        return applyInfoService.selectGroupApplyInfo(groupKey,num);
    }
    @PostMapping("/insertApply")
    public int insertApply(@RequestBody ApplyInfo applyInfo){
        return applyInfoService.insertApply(applyInfo.getApplyA(),applyInfo.getApplyB(),applyInfo.getApplyType());
    }
    @PostMapping("/updateResult")
    public int updateResult(@RequestBody ApplyInfo applyInfo){
        return applyInfoService.updateResult(applyInfo.getApplyKey(),applyInfo.getApplyResult());
    }
}
