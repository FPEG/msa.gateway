package net.fpeg.msa.gateway.filter;

import net.fpeg.msa.common.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    JwtUtil jwtUtil;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String token = exchange.getRequest().getQueryParams().getFirst("token");
//        if (token == null || token.isEmpty()) {
//            log.info( "token 为空，无法进行访问." );
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
        //放通登陆
        if(exchange.getRequest().getURI().getPath().startsWith("/auth/login"))
        {
            return chain.filter(exchange);
        }
        if(exchange.getRequest().getURI().getPath().startsWith("/auth/register"))
        {
            return chain.filter(exchange);
        }

        String token;
        try
        {
            List<String> authorization = Objects.requireNonNull(exchange.getRequest().getHeaders().get("Authorization"));
            token = authorization.get(0);
        }
        catch (NullPointerException ex)
        {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//            exchange.mutate().response(exchange.getResponse().mutate().headers(httpHeader -> httpHeader.set("mytoken", "test")).build())
//            exchange.getResponse().m
            ObjectMapper mapper = new ObjectMapper();
//            return exchange.getResponse().setComplete();
//            mapper.writeValueAsString(new BaseJson("asd"))
            byte[] bytes = "{\"value\":\"没授权\"}".getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
//            return exchange.getResponse().writeWith(Flux.just(Mono.fromCallable(() -> {
//                return mapper.writeValueAsString(new BaseJson("asd"));
//            })));
        }
        try
        {
            jwtUtil.validate(token.substring(7));
        }
        catch (Exception ex)
        {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            byte[] bytes = "{\"value\":\"授权错误\"}".getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        }
//        log.info(exchange.getRequest().getURI().getPath());
//        log.info(exchange.getRequest().getURI().toASCIIString());
//        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeader -> httpHeader.set("mytoken", "test")).build();
//        exchange.mutate().request(serverHttpRequest).build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
