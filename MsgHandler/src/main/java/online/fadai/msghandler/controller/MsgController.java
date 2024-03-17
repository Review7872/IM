package online.fadai.msghandler.controller;

import jakarta.annotation.Resource;
import online.fadai.pojo.Status;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MsgController {
    @Resource
    private RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    @Resource(name = "successStatus")
    private Status successStatus;
}
