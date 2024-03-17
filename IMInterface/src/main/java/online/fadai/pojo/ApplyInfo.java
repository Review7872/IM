package online.fadai.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ApplyInfo {
    private Long applyKey;
    private Long applyA;
    private Long applyB;
    private int applyType;
    private Date applyTime;
    private Integer applyResult;

}
