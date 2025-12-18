package pr.po.ratelimiterservice.gateway;


import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pr.po.ratelimiterservice.decision.Decision;
import pr.po.ratelimiterservice.decision.RateLimitDecisionService;
import pr.po.ratelimiterservice.features.FeatureExtractor;
import pr.po.ratelimiterservice.features.FeatureVector;
import reactor.core.publisher.Mono;

@Component
public class RateLimitFilter implements GlobalFilter {

    private final FeatureExtractor extractor;
    private final RateLimitDecisionService decisionService;

    public RateLimitFilter(
            FeatureExtractor extractor,
            RateLimitDecisionService decisionService
    ) {
        this.extractor = extractor;
        this.decisionService = decisionService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        FeatureVector features = extractor.extract(exchange);
        Decision decision = decisionService.decide(features);

        if (decision == Decision.BLOCK) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
