package online.fadai.userinfo.config;

import online.fadai.userinfo.util.Jwt;
import online.fadai.userinfo.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Value("${snow.beginTime}")
    private long beginTime;
    @Value("${snow.serverId}")
    private long serverId;
    @Value("${snow.hostId}")
    private long hostId;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.jwt_iss}")
    private String jwt_iss;
    @Value("${jwt.subject}")
    private String subject;

    @Bean
    public SnowflakeIdGenerator getSnow() {
        return new SnowflakeIdGenerator(serverId, hostId, beginTime);
    }
    @Bean
    public Jwt getJwt(){
        return new Jwt(secret,jwt_iss,subject);
    }

}
