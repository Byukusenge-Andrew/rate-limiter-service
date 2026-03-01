package pr.po.ratelimiterservice.ml;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pr.po.ratelimiterservice.features.FeatureVector;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class MlClient {

    private final WebClient webClient;

    public MlClient(WebClient.Builder webClientBuilder) {
        // Pointing to the Python Microservice
        this.webClient = webClientBuilder.baseUrl("http://ml-scoring-service:8000").build();
    }

    /**
     * Calls Python ML Service to get a risk score.
     */
    public double score(FeatureVector features) {
        // In a reactive stack, we generally shouldn't block, but decisionService 
        // logic is currently synchronous in the Filter flow.
        // For this portfolio MVP, block() is acceptable here.
        // For pure non-blocking, we'd refactor decisionService to return Mono<Decision>.

        try {
            Double risk = webClient.post()
                    .uri("/score")
                    .bodyValue(features)
                    .retrieve()
                    .bodyToMono(ScoreResponse.class)
                    .timeout(Duration.ofMillis(500)) // fast timeout
                    .map(ScoreResponse::getRiskScore)
                    .onErrorReturn(0.1) // If Python is down, assume safe (0.1)
                    .block();

            return risk != null ? risk : 0.1;

        } catch (Exception e) {
            // Log error in production
            // System.err.println("ML Service Error: " + e.getMessage());
            return 0.1; // Fallback: Allow traffic
        }
    }
}