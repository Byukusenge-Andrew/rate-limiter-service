package pr.po.ratelimiterservice.metrics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    @GetMapping("/rate-limit")
    public Map<String, Object> metrics() {
        return Map.of(
                "blockedRequests", 12,
                "throttledRequests", 34,
                "allowedRequests", 450
        );
    }
}
