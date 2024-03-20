package online.fadai.imgateway.predicatesfactory;

import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class TokenCheckFilter implements GlobalFilter, Ordered {//GlobalFilter全局过滤器，Ordered 顺序优先级

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private byte[] bytes;
    /**
     * 白名单 (请求路径)
     */
    public static final List<String> ALLOW_URL = Arrays.asList(
            "/info/userInfo/insertUser", "/info/userInfo/userLogin", "/info/userInfo/userForget");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //拿到uri
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (ALLOW_URL.contains(path)) {
            //放行
            return chain.filter(exchange);
        }
        //校验
        HttpHeaders headers = request.getHeaders(); //拿到请求头
        List<String> authorization = headers.get("Authorization");
        if (!CollectionUtils.isEmpty(authorization)) { //key不为空，取第一个
            String token = authorization.get(0);
            if (StringUtils.hasText(token) && Boolean.TRUE.equals(redisTemplate.hasKey(token))) { //token不空
                return chain.filter(exchange);
            }
        }
        //拦截
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().set("content-type", "application/json;charset=utf-8");
        DataBuffer wrap = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(wrap));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}