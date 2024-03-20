package online.fadai.imgateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

@Configuration
public class Result {
    @Bean
    public byte[] result401Byte(){
        HashMap<String, Object> map = new HashMap<>(4);
        //返回401
        map.put("code", HttpStatus.UNAUTHORIZED.value());
        map.put("msg", "未授权");

        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = new byte[0]; //以字节形式 写到objectmapper
        try {
            bytes = objectMapper.writeValueAsBytes(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }
}
