package pr.po.ratelimiterservice.throttle;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ThrottleService {

    public Mono<Void> throttle(ServerWebExchange exchange) {
        return Mono.delay(Duration.ofMillis(300))
                .then(exchange.getResponse().setComplete());
    }
}
