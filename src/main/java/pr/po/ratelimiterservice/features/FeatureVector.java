package pr.po.ratelimiterservice.features;

import java.util.Objects;

/**
 * FeatureVector carries normalized/request-scoped features used by the
 * rate-limit decision pipeline.
 *
 * Notes:
 * - Keep primitive wrappers where nullability may be meaningful during extraction.
 * - Add fields conservatively and prefer double for normalized rates/scores.
 */
public class FeatureVector {

    // Identity / context
    private String clientIp;
    private Boolean authenticated;

    // Volume/traffic shape
    private Integer requestsPerMinute;
    private Integer burstCount10s;
    private Integer uniqueEndpoints1m;

    // Quality/error signals
    private Double http4xxRate;

    // Content/behavioral signals
    private Double payloadEntropy;
    private Double userReputationScore;

    // HTTP specific metadata (for Python API)
    private String path;
    private String method;
    private String userAgent;

    // Constructors
    public FeatureVector() {}

    public FeatureVector(String clientIp,
                         Boolean authenticated,
                         Integer requestsPerMinute,
                         Integer burstCount10s,
                         Integer uniqueEndpoints1m,
                         Double http4xxRate,
                         Double payloadEntropy,
                         Double userReputationScore) {
        this.clientIp = clientIp;
        this.authenticated = authenticated;
        this.requestsPerMinute = requestsPerMinute;
        this.burstCount10s = burstCount10s;
        this.uniqueEndpoints1m = uniqueEndpoints1m;
        this.http4xxRate = http4xxRate;
        this.payloadEntropy = payloadEntropy;
        this.userReputationScore = userReputationScore;
    }

    // Getters and setters

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Integer getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(Integer requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public Integer getBurstCount10s() {
        return burstCount10s;
    }

    public void setBurstCount10s(Integer burstCount10s) {
        this.burstCount10s = burstCount10s;
    }

    public Integer getUniqueEndpoints1m() {
        return uniqueEndpoints1m;
    }

    public void setUniqueEndpoints1m(Integer uniqueEndpoints1m) {
        this.uniqueEndpoints1m = uniqueEndpoints1m;
    }

    public Double getHttp4xxRate() {
        return http4xxRate;
    }

    public void setHttp4xxRate(Double http4xxRate) {
        this.http4xxRate = http4xxRate;
    }

    public Double getPayloadEntropy() {
        return payloadEntropy;
    }

    public void setPayloadEntropy(Double payloadEntropy) {
        this.payloadEntropy = payloadEntropy;
    }

    public Double getUserReputationScore() {
        return userReputationScore;
    }

    public void setUserReputationScore(Double userReputationScore) {
        this.userReputationScore = userReputationScore;
    }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    // Builder-style helpers (optional fluency)
    public FeatureVector withClientIp(String clientIp) {
        setClientIp(clientIp);
        return this;
    }

    public FeatureVector withAuthenticated(Boolean authenticated) {
        setAuthenticated(authenticated);
        return this;
    }

    public FeatureVector withRequestsPerMinute(Integer rpm) {
        setRequestsPerMinute(rpm);
        return this;
    }

    public FeatureVector withBurstCount10s(Integer burst) {
        setBurstCount10s(burst);
        return this;
    }

    public FeatureVector withUniqueEndpoints1m(Integer unique) {
        setUniqueEndpoints1m(unique);
        return this;
    }

    public FeatureVector withHttp4xxRate(Double rate) {
        setHttp4xxRate(rate);
        return this;
    }

    public FeatureVector withPayloadEntropy(Double entropy) {
        setPayloadEntropy(entropy);
        return this;
    }

    public FeatureVector withUserReputationScore(Double score) {
        setUserReputationScore(score);
        return this;
    }

    // Equality and hashing (based on all fields)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureVector)) return false;
        FeatureVector that = (FeatureVector) o;
        return Objects.equals(clientIp, that.clientIp)
                && Objects.equals(authenticated, that.authenticated)
                && Objects.equals(requestsPerMinute, that.requestsPerMinute)
                && Objects.equals(burstCount10s, that.burstCount10s)
                && Objects.equals(uniqueEndpoints1m, that.uniqueEndpoints1m)
                && Objects.equals(http4xxRate, that.http4xxRate)
                && Objects.equals(payloadEntropy, that.payloadEntropy)
                && Objects.equals(userReputationScore, that.userReputationScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientIp, authenticated, requestsPerMinute, burstCount10s,
                uniqueEndpoints1m, http4xxRate, payloadEntropy, userReputationScore);
    }

    @Override
    public String toString() {
        return "FeatureVector{" +
                "clientIp='" + clientIp + '\'' +
                ", authenticated=" + authenticated +
                ", requestsPerMinute=" + requestsPerMinute +
                ", burstCount10s=" + burstCount10s +
                ", uniqueEndpoints1m=" + uniqueEndpoints1m +
                ", http4xxRate=" + http4xxRate +
                ", payloadEntropy=" + payloadEntropy +
                ", userReputationScore=" + userReputationScore +
                '}';
    }
}