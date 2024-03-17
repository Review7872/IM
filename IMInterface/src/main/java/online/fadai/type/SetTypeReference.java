package online.fadai.type;

import com.alibaba.fastjson2.TypeReference;

import java.util.Set;

public class SetTypeReference {
    private static TypeReference<Set<Long>> setTypeReference = null;
    private SetTypeReference(){}
    public static TypeReference<Set<Long>> getSetTypeReference(){
        if (setTypeReference != null){
            return setTypeReference;
        }else {
            synchronized (SetTypeReference.class){
                if (setTypeReference != null){
                    return setTypeReference;
                }else {
                    setTypeReference = new TypeReference<>() {
                    };
                    return setTypeReference;
                }
            }
        }
    }
}
