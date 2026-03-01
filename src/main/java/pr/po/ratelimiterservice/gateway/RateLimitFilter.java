package pr.po.ratelimiterservice.gateway;
import io.micrometer.core.instrument.Metrics;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import pr.po.ratelimiterservice.decision.Decision;
import pr.po.ratelimiterservice.decision.RateLimitDecisionService;
import pr.po.ratelimiterservice.features.FeatureExtractor;
import pr.po.ratelimiterservice.throttle.ThrottleService;
import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;

@Component
public class RateLimitFilter implements WebFilter { // <--- Changed from GlobalFilter to WebFilter

    private final FeatureExtractor extractor;
    private final RateLimitDecisionService decisionService;
    private final ThrottleService throttleService;

    public RateLimitFilter(
            FeatureExtractor extractor,
            RateLimitDecisionService decisionService,
            ThrottleService throttleService
    ) {
        this.extractor = extractor;
        this.decisionService = decisionService;
        this.throttleService = throttleService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 1. Skip static assets or health checks if you want (Optional)
        // if (exchange.getRequest().getPath().value().equals("/health")) {
        //    return chain.filter(exchange);
        // }

        // 2. Extract Features (Async Redis Call)
        return extractor.extract(exchange)
                .flatMap(features -> {
                    // System.out.println("Checking Limit for: " + features.getClientIp() + " Count: " + features.getRequestsPerMinute());
                    Metrics.counter("traffic_requests", "ip", features.getClientIp()).increment();
                    Decision decision = decisionService.decide(features);

                    if (decision == Decision.BLOCK) {
                        // 1. Set Status Code
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

                        // 2. Set Content Type to JSON
                        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                        // 3. Create Custom JSON Error Message
                        String errorBody = """
                            {
                                "error": "Too Many Requests",
                                "code": 429,
                                "message": "Traffic limit exceeded. Request blocked by AI Security Engine."
                            }
                        """;

                        // 4. Write Body to Response
                        byte[] bytes = errorBody.getBytes(StandardCharsets.UTF_8);
                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

                        return exchange.getResponse().writeWith(Mono.just(buffer));
                    }

                    if (decision == Decision.THROTTLE) {
                        return throttleService.throttle(exchange)
                                .then(chain.filter(exchange));
                    }

                    return chain.filter(exchange);
                });
    }
}