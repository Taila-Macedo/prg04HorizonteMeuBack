package br.com.ifba.horizontemeu.cliente;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Log4j2
public class SpringClient {

    public static void main(String[] args) {

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080/usuarios")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        // GET findAll paginado
        String responseFindAll = webClient.get()
                .uri("/findall?page=0&size=5")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("findAll: {}", responseFindAll);
        System.out.println("=== RESPOSTA ===");
        System.out.println(responseFindAll);
    }
}