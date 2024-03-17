package online.fadai.userinfo.config;

import online.fadai.pojo.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatusConfig {
    @Bean("successStatus")
    public Status getSuccessStatus(){
        return new Status(200,"success");
    }
    @Bean("exceptionStatus")
    public Status getExceptionStatus(){
        return new Status(500,"failed");
    }
}
