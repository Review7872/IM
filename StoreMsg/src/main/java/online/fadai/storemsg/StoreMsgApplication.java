package online.fadai.storemsg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class StoreMsgApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoreMsgApplication.class,args);
    }
}
