package pr.po.ratelimiterservice.decision;

import org.springframework.stereotype.Service;
import pr.po.ratelimiterservice.features.FeatureVector;
import pr.po.ratelimiterservice.ml.MlClient;

@Service
public class RateLimitDecisionService {

    private static final double BLOCK_THRESHOLD = 0.85;
    private static final double THROTTLE_THRESHOLD = 0.60;

    private final MlClient mlClient;

    public RateLimitDecisionService(MlClient mlClient) {
        this.mlClient = mlClient;
    }

    public Decision decide(FeatureVector features) {

        // 1️⃣ Hard safety rules (fast exit)
        if (features.getRequestsPerMinute() > 600) {
            return Decision.BLOCK;
        }

        if (features.getHttp4xxRate() > 0.8) {
            return Decision.BLOCK;
        }

        // 2️⃣ ML scoring
        double riskScore = mlClient.score(features);

        // 3️⃣ Decision thresholds
        if (riskScore >= BLOCK_THRESHOLD) {
            return Decision.BLOCK;
        }

        if (riskScore >= THROTTLE_THRESHOLD) {
            return Decision.THROTTLE;
        }

        // 4️⃣ Normal traffic
        return Decision.ALLOW;
    }
}

