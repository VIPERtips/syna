package co.zw.blexta.syna.HealthTips;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/health-tips")
@RequiredArgsConstructor
@Tag(name = "Health Tips API", description = "Provides 5 personalized health tips based on location, season, and weather.")
public class HealthTipsController {

    private final HealthTips tips;

    @PostMapping
    @Operation(summary = "Get 5 personalized health tips")
    public Mono<Map<String, Object>> getHealthTips(@RequestBody HealthTipsRequest request,
                                                   @RequestParam(defaultValue = "default-convo") String conversationId) {
        return tips.getTips(
                        request.getLocation(),
                        request.getSeason(),
                        request.getWeather(),
                        "",
                        conversationId
                )
                .map(response -> {
                    try {
                        // parse the JSON string from Groq
                        return new ObjectMapper().readValue(response, new TypeReference<Map<String, Object>>() {});
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Map.of("error", "Invalid JSON from Groq");
                    }
                });
    }

}