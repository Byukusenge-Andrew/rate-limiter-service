package pr.po.ratelimiterservice.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class SlidingWindowService {

    private static final Duration WINDOW = Duration.ofSeconds(60);

    private final ReactiveRedisTemplate<String, String> redis;

    public SlidingWindowService(
            @Qualifier("rateLimiterRedisTemplate")
            ReactiveRedisTemplate<String, String> redis) {
        this.redis = redis;
    }

    public Mono<Long> increment(String key) {
        return redis.opsForValue()
                .increment(key)
                .flatMap(count ->
                        redis.expire(key, WINDOW)
                                .thenReturn(count)
                );
    }
}
