package pr.po.ratelimiterservice.redis;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class SlidingWindowService {

    private final ReactiveRedisTemplate<String, String> redis;

    public SlidingWindowService(ReactiveRedisTemplate<String, String> redis) {
        this.redis = redis;
    }

    public Mono<Long> increment(String key) {
        return redis.opsForValue()
                .increment(key)
                .flatMap(count ->
                        redis.expire(key, Duration.ofSeconds(60))
                                .thenReturn(count)
                );
    }
}
