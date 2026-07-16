package com.ordershopx.backend.shared.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiAIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public String generarRespuesta(String problema) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        // El Prompt que le da el rol a la IA
        String prompt = "Eres el asistente de soporte técnico oficial de la plataforma OrderShopX. " +
                "Redacta una respuesta amable, empática, profesional y directa para el siguiente problema reportado por un usuario: " + problema;

        Map<String, Object> request = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{Map.of("text", prompt)})
                }
        );

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            // Navegar por el JSON de respuesta de Gemini
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            return parts.get(0).get("text").toString();
        } catch (Exception e) {
            return "Lo siento, ocurrió un error al generar la respuesta con IA. Por favor, redacta tu respuesta manualmente.";
        }
    }
}