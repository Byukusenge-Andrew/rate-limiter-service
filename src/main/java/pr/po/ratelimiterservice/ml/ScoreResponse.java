package pr.po.ratelimiterservice.ml;

public class ScoreResponse {
    
    private double riskScore;

    // Default no-args constructor (needed for JSON deserialization)
    public ScoreResponse() {
    }

    public ScoreResponse(double riskScore) {
        this.riskScore = riskScore;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    @Override
    public String toString() {
        return "ScoreResponse{riskScore=" + riskScore + "}";
    }
}