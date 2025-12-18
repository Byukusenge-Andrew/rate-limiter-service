package pr.po.ratelimiterservice.ml;

import org.springframework.stereotype.Component;
import pr.po.ratelimiterservice.features.FeatureVector;

/**
 * MlClient provides a risk score used by the decision service.
 *
 * Scoring contract:
 * - Returns a value in [0.0, 1.0], where higher implies higher risk.
 * - Must be fast and side effect free.
 *
 * Implementation notes:
 * - This default implementation uses a deterministic heuristic so the service
 *   can operate without a deployed model. Replace the internals with a real
 *   model or remote inference call when available.
 */
@Component
public class MlClient {

    /**
     * Compute a risk score for the given features.
     * @param features FeatureVector with request/user signals
     * @return risk score in [0,1]
     */
    public double score(FeatureVector features) {
        if (features == null) {
            return 0.0;
        }

        // Start from a baseline
        double score = 0.0;

        // Volume: requests per minute (rpm)
        Integer rpm = features.getRequestsPerMinute();
        if (rpm != null) {
            // Normalize: assume 0..600 typical; cap contribution
            double rpmNorm = clamp(rpm / 600.0, 0.0, 1.0);
            score += 0.45 * rpmNorm;
        }

        // Error rate: 4xx rate
        Double r4xx = features.getHttp4xxRate();
        if (r4xx != null) {
            double r4xxNorm = clamp(r4xx, 0.0, 1.0);
            score += 0.35 * r4xxNorm;
        }

        // Authentication: unauthenticated slightly riskier
        Boolean authed = features.getAuthenticated();
        if (authed != null && !authed) {
            score += 0.08;
        }

        // Burstiness in last 10s
        Integer burst10s = features.getBurstCount10s();
        if (burst10s != null) {
            double burstNorm = clamp(burst10s / 50.0, 0.0, 1.0);
            score += 0.07 * burstNorm;
        }

        // User reputation: invert good reputation
        Double rep = features.getUserReputationScore();
        if (rep != null) {
            double repNorm = clamp(rep, 0.0, 1.0);
            score += 0.10 * (1.0 - repNorm);
        }

        // Entropy: high payload entropy can be suspicious but low weight
        Double entropy = features.getPayloadEntropy();
        if (entropy != null) {
            // Assume normalized [0,1] upstream
            score += 0.03 * clamp(entropy, 0.0, 1.0);
        }

        // Unique endpoints churn in last minute
        Integer uniq = features.getUniqueEndpoints1m();
        if (uniq != null) {
            double uniqNorm = clamp(uniq / 40.0, 0.0, 1.0);
            score += 0.05 * uniqNorm;
        }

        // Bound to [0,1]
        return clamp(score, 0.0, 1.0);
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}