package online.fadai.storemsg.controller;

import jakarta.annotation.Resource;
import online.fadai.storemsg.es.MsgDao;
import online.fadai.storemsg.es.MsgES;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;


@Controller("/storeMsg")
public class StoreMsgController {
    @Resource
    private MsgDao msgDao;

    @GetMapping("/get")
    public List<MsgES> get(String sender, String receiver, long beginTime, long endTime) throws IOException {
        return msgDao.findByMsgSenderAndMsgReceiverAndDateBetween(sender, receiver, beginTime, endTime);
    }
}
