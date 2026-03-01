package pr.po.ratelimiterservice.decision;

import org.springframework.stereotype.Service;
import pr.po.ratelimiterservice.features.FeatureVector;
import pr.po.ratelimiterservice.ml.MlClient;

@Service
public class RateLimitDecisionService {

    private static final double BLOCK_THRESHOLD = 0.85;
    private final MlClient mlClient;

    public RateLimitDecisionService(MlClient mlClient) {
        this.mlClient = mlClient;
    }

    public Decision decide(FeatureVector features) {
        // 1. Hard Rule (Safety Net)
        if (features.getRequestsPerMinute() > 600) return Decision.BLOCK;

        // 2. AI Scoring (Call Python)
        double riskScore = mlClient.score(features);
        
        // Optional: Print score to console to SEE it working
        System.out.println("🤖 AI Risk Score: " + riskScore + " for IP: " + features.getClientIp());

        if (riskScore >= BLOCK_THRESHOLD) {
            return Decision.BLOCK;
        }

        return Decision.ALLOW;
    }
}