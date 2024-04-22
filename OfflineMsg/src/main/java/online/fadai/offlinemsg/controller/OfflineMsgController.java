package online.fadai.offlinemsg.controller;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller("/offlineMsg")
public class OfflineMsgController {
    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @GetMapping("/get")
    public List<String> get(String id){
        Set<String> keys = redisTemplate.keys(id + "*");
        List<String> list ;
        if (keys != null) {
            list = new ArrayList<>(keys);
            keys.forEach(i->{
                list.add(redisTemplate.opsForValue().get(i));
                redisTemplate.delete(i);
            });
            return list;
        }
        return null;
    }
}
