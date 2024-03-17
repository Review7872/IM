package online.fadai.userinfo.dao;

import online.fadai.pojo.ApplyInfo;
import org.apache.ibatis.annotations.*;

import java.sql.Date;
import java.util.List;

@Mapper
public interface ApplyDao {
    @Results(id = "applyInfoMap", value = {
            @Result(id = true, column = "apply_key", property = "applyKey"),
            @Result(column = "apply_a", property = "applyA"),
            @Result(column = "apply_b", property = "applyB"),
            @Result(column = "apply_time", property = "applyTime"),
            @Result(column = "apply_result", property = "applyResult"),
            @Result(column = "apply_type", property = "applyType")
    })
    @Select("""
            select apply_key,apply_a,apply_b,apply_time,apply_result,apply_type from apply_info
            """)
    List<ApplyInfo> selectAll();

    @Select("""
            select apply_key,apply_a,apply_b,apply_time,apply_result,apply_type from apply_info where apply_a = #{userKey} or apply_b = #{userKey} order by apply_time desc limit #{begin},10
            """)
    @ResultMap("applyInfoMap")
    List<ApplyInfo> selectUserApplyInfo(long userKey, int begin);

    @Select("""
            select apply_key,apply_a,apply_b,apply_time,apply_result,apply_type from apply_info where apply_b = #{groupKey} order by apply_time desc limit #{begin},10
            """)
    @ResultMap("applyInfoMap")
    List<ApplyInfo> selectGroupApplyInfo(long groupKey, int begin);

    @Select("""
            select apply_key,apply_a,apply_b,apply_time,apply_result,apply_type from apply_info where apply_key = #{applyKey}
            """)
    @ResultMap("applyInfoMap")
    ApplyInfo selectApplyInfo(long applyKey);

    @Insert("""
            insert into apply_info(apply_key,apply_a,apply_b,apply_time,apply_result,apply_type) value (#{applyKet},#{applyA},#{applyB},#{applyTime},#{applyResult},#{applyType})
            """)
    int insertApply(long applyKet, long applyA, long applyB, Date applyTime, int applyResult, int applyType);

    @Update("""
            update apply_info set apply_result = #{applyResult} where apply_key = #{applyKey}
            """)
    int updateResult(long applyKey, int applyResult);

}
