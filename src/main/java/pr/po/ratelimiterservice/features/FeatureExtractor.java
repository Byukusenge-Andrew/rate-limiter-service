package pr.po.ratelimiterservice.features;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class FeatureExtractor {

    public FeatureVector extract(ServerWebExchange exchange) {

        FeatureVector fv = new FeatureVector();

        String ip = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Forwarded-For");

        fv.setAuthenticated(
                exchange.getRequest().getHeaders().containsHeader("Authorization")
        );

        fv.setRequestsPerMinute(50); // placeholder
        fv.setBurstCount10s(3);
        fv.setUniqueEndpoints1m(2);
        fv.setHttp4xxRate(0.05);
        fv.setPayloadEntropy(0.2);
        fv.setUserReputationScore(0.9);

        return fv;
    }
}
