package br.com.poc.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiTest {

    private Api api;
    private ObjectMapper objectMapper;
    @Mock
    private HttpServletRequest request;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        api = new Api();
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetApi() throws Exception {
        // Simulando a requisição
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("TestAgent");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/api/ip"));

        // Chamando o método
        ResponseEntity<String> response = api.getApi(request);

        // Verificando o status da resposta
        assertEquals(200, response.getStatusCodeValue());

        // Convertendo a resposta JSON para um Map
        Map<String, Object> actualResponseMap = objectMapper.readValue(response.getBody(), Map.class);

        // Criando o Map esperado
        Map<String, Object> expectedResponseMap = Map.of(
                "ip", "127.0.0.1",
                "userAgent", "TestAgent",
                "method", "GET",
                "url", "http://localhost:8080/api/ip"
        );

        // Comparando os dois Maps
        assertEquals(expectedResponseMap, actualResponseMap);
    }
    @Test
    public void testGetRequestDetails() throws Exception {
        // Simulando a requisição
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("TestAgent");

        // Criando uma Enumeration para os headers
        List<String> headerNames = List.of("User-Agent", "Accept");
        Enumeration<String> headerNamesEnum = Collections.enumeration(headerNames);
        when(request.getHeaderNames()).thenReturn(headerNamesEnum);

        // Chamando o método
        ResponseEntity<String> response = api.getRequestDetails(request);

        // Verificando o status da resposta
        assertEquals(200, response.getStatusCodeValue());

        // Convertendo a resposta JSON para um Map
        Map<String, Object> actualResponseMap = objectMapper.readValue(response.getBody(), Map.class);

        // Criando o Map esperado
        Map<String, Object> expectedResponseMap = Map.of(
                "ip", "127.0.0.1",
                "userAgent", "TestAgent",
                "headers", List.of("User-Agent", "Accept")
        );

        // Comparando os dois Maps
        assertEquals(expectedResponseMap, actualResponseMap);
    }

    @Test
    public void testGetError() {
        // Verificando se o método lança a exceção correta
        assertThrows(RuntimeException.class, () -> api.getError(), "Erro simulado para teste");
    }
    @Test
    public void testGetRequestDetailsWithUserAgent() throws Exception {
        // Simulando a requisição com User-Agent
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        // Criando uma Enumeration para os headers
        List<String> headerNames = List.of("Content-Type", "Accept");
        Enumeration<String> headerNamesEnum = Collections.enumeration(headerNames);
        when(request.getHeaderNames()).thenReturn(headerNamesEnum);

        // Chamando o método
        ResponseEntity<String> response = api.getRequestDetails(request);

        // Verificando o status da resposta
        assertEquals(200, response.getStatusCodeValue());

        // Convertendo a resposta JSON para um Map
        Map<String, Object> actualResponseMap = objectMapper.readValue(response.getBody(), Map.class);

        // Criando o Map esperado
        Map<String, Object> expectedResponseMap = Map.of(
                "ip", "192.168.1.1",
                "userAgent", "Mozilla/5.0",
                "headers", List.of("Content-Type", "Accept")
        );

        // Comparando os dois Maps
        assertEquals(expectedResponseMap, actualResponseMap);
    }

    @Test
    public void testGetRequestDetailsWithNoHeaders() throws Exception {
        // Simulando a requisição sem cabeçalhos
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("SomeUserAgent");

        // Criando uma Enumeration vazia para os headers
        Enumeration<String> emptyHeaderNamesEnum = Collections.enumeration(Collections.emptyList());
        when(request.getHeaderNames()).thenReturn(emptyHeaderNamesEnum);

        // Chamando o método
        ResponseEntity<String> response = api.getRequestDetails(request);

        // Verificando o status da resposta
        assertEquals(200, response.getStatusCodeValue());

        // Convertendo a resposta JSON para um Map
        Map<String, Object> actualResponseMap = objectMapper.readValue(response.getBody(), Map.class);

        // Criando o Map esperado
        Map<String, Object> expectedResponseMap = Map.of(
                "ip", "127.0.0.1",
                "userAgent", "SomeUserAgent",
                "headers", Collections.emptyList()
        );

        // Comparando os dois Maps
        assertEquals(expectedResponseMap, actualResponseMap);
    }

    @Test
    public void testGlobalExceptionHandler() throws Exception {
        // Criando uma instância do GlobalExceptionHandler
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        // Criando uma exceção para simular um erro
        Exception exception = new RuntimeException("Teste de exceção");

        // Chamando o manipulador de exceções
        ResponseEntity<String> response = handler.handleException(exception);

        // Verificando se a resposta é a esperada
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Erro interno ao processar a requisição", response.getBody());
    }

    @Test
    public void testGetRequestDetailsJsonProcessingException() throws Exception {
        // Simulando a requisição
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("TestAgent");

        // Criando uma Enumeration para os headers
        List<String> headerNames = List.of("User-Agent", "Accept");
        Enumeration<String> headerNamesEnum = Collections.enumeration(headerNames);
        when(request.getHeaderNames()).thenReturn(headerNamesEnum);

        // Criando um mock do ObjectMapper
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        when(mockObjectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Erro de JSON") {});

        // Criando uma instância da classe Api
        Api api = new Api();

        // Usando reflexão para injetar o mockObjectMapper no campo privado da classe Api
        Field objectMapperField = Api.class.getDeclaredField("objectMapper"); // Nome do campo onde o ObjectMapper é definido
        objectMapperField.setAccessible(true); // Permite acesso ao campo privado
        objectMapperField.set(api, mockObjectMapper); // Define o campo com o mock

        // Chamando o método
        ResponseEntity<String> response = api.getRequestDetails(request);

        // Verificando se a resposta é a esperada para erro interno
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Erro interno ao processar a requisição", response.getBody());
    }




}