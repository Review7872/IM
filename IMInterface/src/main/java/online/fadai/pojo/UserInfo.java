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
public class UserInfo {
    private Long userKey;
    private String username;
    private String password;
    private Integer phone;
    private String userPhoto;
    private String friends;
    private String groups;
    private String blackSet;
    private Long friendKey;
    private Long groupKey;

    public Set<Long> getFriendsSet() {
        Set<Long> set = JSON.parseObject(friends, SetTypeReference.getSetTypeReference().getType());
        if (set == null){
            return new HashSet<>();
        }
        return set;
    }

    public Set<Long> getGroupsSet() {
        Set<Long> set = JSON.parseObject(groups, SetTypeReference.getSetTypeReference().getType());
        if (set == null){
            return new HashSet<>();
        }
        return set;
    }

}
