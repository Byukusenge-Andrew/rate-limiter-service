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

        // Set HTTP Fields
        fv.setPath(exchange.getRequest().getPath().value());
        fv.setMethod(exchange.getRequest().getMethod().name());
        String ua = exchange.getRequest().getHeaders().getFirst(HttpHeaders.USER_AGENT);
        fv.setUserAgent(ua != null ? ua : "unknown");

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
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Take the first IP in the comma-separated list
            return xForwardedFor.split(",")[0].trim();
        }

        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        return "unknown";
    }
}