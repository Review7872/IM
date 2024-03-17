package online.fadai.service;

import online.fadai.pojo.ApplyInfo;

import java.sql.Date;
import java.util.List;

public interface ApplyInfoService {
    ApplyInfo selectApplyInfo(long userKey);

    List<ApplyInfo> selectUserApplyInfo(long userKey, int num);

    List<ApplyInfo> selectGroupApplyInfo(long groupKey,int num);

    int insertApply(long applyA, long applyB, int applyType);

    int updateResult(long userKey, int applyResult);
}
