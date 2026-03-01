package pr.po.ratelimiterservice.features;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pr.po.ratelimiterservice.redis.SlidingWindowService; // Import this!
import reactor.core.publisher.Mono;

@Component
public class FeatureExtractor {

    private final SlidingWindowService slidingWindowService;

    // Inject the Redis Service
    public FeatureExtractor(SlidingWindowService slidingWindowService) {
        this.slidingWindowService = slidingWindowService;
    }

    public Mono<FeatureVector> extract(ServerWebExchange exchange) {
        String ip = getClientIp(exchange);

        // ✅ CALL REDIS HERE
        return slidingWindowService.increment(ip)
                .map(currentCount -> buildVector(exchange, currentCount));
    }

    private FeatureVector buildVector(ServerWebExchange exchange, Long currentCount) {
        FeatureVector fv = new FeatureVector();

        // Set the REAL count from Redis
        fv.setRequestsPerMinute(currentCount.intValue());

        // Set IP
        fv.setClientIp(getClientIp(exchange));

        // Auth check
        fv.setAuthenticated(
                exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)
        );

        // Defaults for other ML features (keep these static for now)
        fv.setBurstCount10s(Math.min(currentCount.intValue(), 10));
        fv.setUniqueEndpoints1m(2);
        fv.setHttp4xxRate(0.05);
        fv.setPayloadEntropy(0.2);
        fv.setUserReputationScore(0.9);

        return fv;
    }

    private String getClientIp(ServerWebExchange exchange) {
        String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (ip == null) {
            if (exchange.getRequest().getRemoteAddress() != null) {
                ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            } else {
                ip = "unknown";
            }
        }
        return ip;
    }
}