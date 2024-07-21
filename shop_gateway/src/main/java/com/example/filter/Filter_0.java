package com.example.filter;
import com.example.common.CommonConstants;
import com.example.common.redis.CommonRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class Filter_0 implements GlobalFilter {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest().mutate().//获得请求对象并开始修改
        header(CommonConstants.REAL_IP,exchange.getRequest().getRemoteAddress().getHostString()).//往请求头添加真实ip地址
        header(CommonConstants.FEIGN_REQUEST_KEY,CommonConstants.FEIGN_REQUEST_FALSE).  //向请求头中加入"FEIGN_REQUEST":0
        build();
        return chain.filter(exchange.mutate().request(request).build()).then(Mono.fromRunnable(()->{
            String token,redisKey;
            if(!StringUtils.isEmpty(token =exchange.getRequest().getHeaders().getFirst(CommonConstants.TOKEN_NAME)) && redisTemplate.hasKey(redisKey = CommonRedisKey.USER_TOKEN.getRealKey(token))){
                redisTemplate.expire(redisKey, CommonRedisKey.USER_TOKEN.getExpireTime(), CommonRedisKey.USER_TOKEN.getUnit());
            }
        }));
    }
}
