package online.fadai.storemsg.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "msg")
public class MsgES {
    @Field(type = FieldType.Text)
    private String id;
    @Field(type = FieldType.Integer)
    private int type;
    @Field(type = FieldType.Text)
    private String msgSender;
    @Field(type = FieldType.Text)
    private String msgReceiver;
    @Field(type = FieldType.Text)
    private String msg;
    @Field(type = FieldType.Long)
    private Long date;
}
