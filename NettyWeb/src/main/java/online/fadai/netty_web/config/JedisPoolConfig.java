package online.fadai.netty_web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class JedisPoolConfig {
    @Value("${netty.port}")
    private int port;

    @Bean("localIp")
    public String localIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress() + ":" + port;
    }
}
