package br.com.poc.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class Api {
    private final ObjectMapper objectMapper = new ObjectMapper(); // Instância do ObjectMapper

    @GetMapping("ip")
    public ResponseEntity<String> getApi(HttpServletRequest request) throws JsonProcessingException {
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.info("Recebendo chamada de api do IP: {}", clientIp);
        log.info("User-Agent: {}", userAgent);
        log.info("Método: {}", method);
        log.info("URL Requisitada: {}", url);

        // Montando um Map com os detalhes da requisição
        Map<String, Object> details = new HashMap<>();
        details.put("ip", clientIp);
        details.put("userAgent", userAgent);
        details.put("method", method);
        details.put("url", url);

        // Convertendo o Map para JSON usando o ObjectMapper
        String jsonResponse = objectMapper.writeValueAsString(details);
        return ResponseEntity.ok(jsonResponse);
    }
    @GetMapping("details")
    public ResponseEntity<String> getRequestDetails(HttpServletRequest request) {
        Map<String, Object> details = new HashMap<>();
        details.put("ip", request.getRemoteAddr());
        details.put("userAgent", request.getHeader("User-Agent"));
        details.put("headers", Collections.list(request.getHeaderNames()));

        try {
            String jsonResponse = objectMapper.writeValueAsString(details);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            log.error("Erro ao converter os detalhes para JSON", e);
            return ResponseEntity.status(500).body("Erro interno ao processar a requisição");
        }
    }
    @GetMapping("error")
    public ResponseEntity<String> getError() {
        throw new RuntimeException("Erro simulado para teste");
    }
}
