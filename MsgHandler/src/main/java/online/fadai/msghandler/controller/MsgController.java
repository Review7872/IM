package online.fadai.msghandler.controller;

import jakarta.annotation.Resource;
import online.fadai.pojo.Msg;
import online.fadai.pojo.Status;
import online.fadai.service.MsgService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/msg")
public class MsgController {
    @Resource
    private RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    @Resource
    private MsgService msgService;
    @Value("${server.port}")
    private int port;
    @GetMapping("/port")
    public int port(){
        return port;
    }
    @Resource(name = "successStatus")
    private Status successStatus;
    @PostMapping("/send")
    public Status send(@RequestBody Msg msg){
        msgService.sendMsg(msg);
        return successStatus;
    }
}
