package online.fadai.pojo;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import online.fadai.type.SetTypeReference;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GroupInfo {
    private Long groupKey;
    private String groupName;
    private Long groupMaster;
    private String groupFriend;
    private String groupPhoto;
    public Set<Long> getGroupFriendSet(){
        Set<Long> set = JSON.parseObject(groupFriend, SetTypeReference.getSetTypeReference().getType());
        if (set == null){
            return new HashSet<>();
        }
        return set;
    }
}
