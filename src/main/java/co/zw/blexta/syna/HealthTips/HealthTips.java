package co.zw.blexta.syna.HealthTips;



import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;



import java.util.ArrayList;

import java.util.List;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;



@Component

public class HealthTips {



    private final WebClient webClient;



    private final Map<String, List<Map<String, Object>>> conversationHistory = new ConcurrentHashMap<>();



    public HealthTips(@Value("${groq-api-key}") String apiKey) {

        this.webClient = WebClient.builder()

                .baseUrl("https://api.groq.com/openai/v1")

                .defaultHeader("Authorization", "Bearer " + apiKey)

                .build();

    }



    public Mono<String> getTips(String location, String season, String weather, String userMessage, String conversationId) {

        return getTips(location, season, weather, userMessage, conversationId, 10);

    }



    public Mono<String> getTips(String location, String season, String weather, String userMessage, String conversationId, int maxHistory) {



        List<Map<String, Object>> history = conversationHistory.computeIfAbsent(conversationId, k -> new ArrayList<>());



        List<Map<String, Object>> messages = buildMessages(location, season, weather, userMessage, history, maxHistory);



        Map<String, Object> body = Map.of(

                "model", "openai/gpt-oss-20b",

                "input", messages

        );



        return webClient.post()

                .uri("/responses")

                .bodyValue(body)

                .retrieve()

                .bodyToMono(Map.class)

                .map(res -> {

                    String assistantResponse = extractResponse(res);



                    if (assistantResponse != null && !assistantResponse.equals("No response from Groq API")) {

                        updateHistory(history, userMessage, assistantResponse, maxHistory);

                    }



                    return assistantResponse;

                });

    }



    private List<Map<String, Object>> buildMessages(String location, String season, String weather, String userMessage,

                                                    List<Map<String, Object>> history, int maxHistory) {

        List<Map<String, Object>> messages = new ArrayList<>();



        StringBuilder userContext = new StringBuilder("User context:");

        if (location != null && !location.isEmpty()) userContext.append(" Location = ").append(location).append(",");

        if (season != null && !season.isEmpty()) userContext.append(" Season = ").append(season).append(",");

        if (weather != null && !weather.isEmpty()) userContext.append(" Weather = ").append(weather);

        String contextString = userContext.toString().replaceAll(",$", "");



        String systemPrompt = """

                You are Syna AI — a smart, people-first health assistant built by Blexta.

                - Your goal is to give 5 practical and relatable health or wellness tips.

                - Tips must match the user's provided context.

                - %s

                - Speak like a real person, not a medical textbook. Be warm, helpful, and locally aware.

                - You can include simple daily routines, food advice, hydration reminders, fitness ideas, or mental health balance tips — anything genuinely useful.

                - Use modern, human language with a natural, friendly tone (avoid sounding robotic or overly formal) sometimes sarcastic but helpful, roast when neccessary.

                - Each tip should be clear, short, and suited to the region's climate, season, or current weather.

                - Output must be in JSON format using this structure:

                    {
                        "tips": [
                          {"id": "1", "tip": "<health_tip_1>", "icon": "<ionicon_name>", "color": "<hex_color>"},
                          {"id": "2", "tip": "<health_tip_2>", "icon": "<ionicon_name>", "color": "<hex_color>"},
                          {"id": "3", "tip": "<health_tip_3>", "icon": "<ionicon_name>", "color": "<hex_color>"},
                          {"id": "4", "tip": "<health_tip_4>", "icon": "<ionicon_name>", "color": "<hex_color>"},
                          {"id": "5", "tip": "<health_tip_5>", "icon": "<ionicon_name>", "color": "<hex_color>"}
                        ]
                      }
                

                - Choose the most fitting Ionicon name for each tip (e.g., "sunny", "leaf-outline", "water", "fitness-outline", "heart-outline").
                - - Choose a hex color for each tip that matches the tip’s theme. For example:
                    - Water tips: "#18b79a"
                    - Fitness tips: "#10b981"
                    - Sleep tips: "#6366f1"
                    - Food/nutrition tips: "#f59e0b"
                    - Mental health tips: "#ef4444"
                  - Return only the JSON body — no extra text or explanation.
               

                - Avoid using em dashes

                """.formatted(contextString);



        messages.add(Map.of("role", "system", "content", systemPrompt));



        int startIndex = Math.max(0, history.size() - maxHistory);

        for (int i = startIndex; i < history.size(); i++) {

            messages.add(history.get(i));

        }



        messages.add(Map.of("role", "user", "content", userMessage));



        return messages;

    }



    private void updateHistory(List<Map<String, Object>> history, String userMessage,

                               String assistantResponse, int maxHistory) {

        history.add(Map.of("role", "user", "content", userMessage));

        history.add(Map.of("role", "assistant", "content", assistantResponse));



        while (history.size() > maxHistory * 2) {

            history.remove(0);

            history.remove(0);

        }

    }



    private String extractResponse(Map<?, ?> res) {

        Object outputObj = res.get("output");

        if (outputObj instanceof List<?> outputs && !outputs.isEmpty()) {

            for (Object o : outputs) {

                if (o instanceof Map<?, ?> m && "message".equals(m.get("type"))) {

                    Object contentObj = m.get("content");

                    if (contentObj instanceof List<?> contentList && !contentList.isEmpty()) {

                        Map<?, ?> firstContent = (Map<?, ?>) contentList.get(0);

                        Object text = firstContent.get("text");

                        if (text != null) return text.toString();

                    }

                }

            }

        }

        System.out.println("Groq raw response: " + res);

        return "No response from Groq API";

    }



    public void clearHistory(String conversationId) {

        conversationHistory.remove(conversationId);

    }



    public int getHistorySize(String conversationId) {

        List<Map<String, Object>> history = conversationHistory.get(conversationId);

        return history != null ? history.size() / 2 : 0;

    }

}