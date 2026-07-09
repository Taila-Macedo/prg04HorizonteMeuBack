package br.com.ifba.horizontemeu.cliente;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Cliente de testes manual da API Horizonte Meu.
 * USO EXCLUSIVO EM DESENVOLVIMENTO — não é executado em produção.
 */
@Log4j2
public class SpringClient {

    // ── Configuração base ──────────────────────────────────────────────────────
    static WebClient clienteBase = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    // Variáveis compartilhadas entre todos os módulos
    static String tokenJwt     = "";
    static Long   usuarioId    = null;
    static Long   pontoId      = null;
    static Long   fotoId       = null;
    static Long   comentarioId = null;
    static Long   favoritoId   = null;
    static String tokenAdmin   = "";
    static Long   notificacaoId = null;

    public static void main(String[] args) {
        testarModuloUsuario();
        testarModuloPontoTuristico();
        testarModuloFoto();
        testarModuloComentario();
        testarModuloFavorito();
        testarModuloNotificacao();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //   MÓDULO USUARIO
    // ══════════════════════════════════════════════════════════════════════════

    static void testarModuloUsuario() {
        cabecalho("USUARIO");
        testarCadastro();
        testarLogin();
        testarBuscarUsuarioPorId();
        testarListarUsuarios();
        testarBuscarUsuarioPorNome();
        testarAtualizarUsuario();
        testarDeleteUsuarioSemAdmin();
    }

    static void testarCadastro() {
        inicio(1, "POST /usuarios — cadastrar novo usuário");
        try {
            String body = """
                    {
                      "nome": "Taila Macedo",
                      "email": "taila@email.com",
                      "senha": "123456",
                      "fotoPerfil": "https://exemplo.com/foto.jpg"
                    }
                    """;
            String resposta = clienteBase.post()
                    .uri("http://localhost:8080/usuarios")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("201 CREATED — " + resposta);
            usuarioId = extrairId(resposta);
            System.out.println("   → ID salvo: " + usuarioId);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarLogin() {
        inicio(2, "POST /auth/login — autenticar usuário");
        try {
            String body = """
                    {
                      "email": "taila@email.com",
                      "senha": "123456"
                    }
                    """;
            String resposta = clienteBase.post()
                    .uri("http://localhost:8080/auth/login")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
            if (resposta != null && resposta.contains("\"token\":")) {
                tokenJwt = resposta.split("\"token\":\"")[1].split("\"")[0];
                System.out.println("   → Token salvo: " + tokenJwt.substring(0, 30) + "...");
            }
            if (usuarioId == null) {
                usuarioId = extrairId(resposta);
                System.out.println("   → ID recuperado do login: " + usuarioId);
            }
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarBuscarUsuarioPorId() {
        inicio(3, "GET /usuarios/{id} — buscar por ID");
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/usuarios/" + usuarioId)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarListarUsuarios() {
        inicio(4, "GET /usuarios?page=0&size=5 — listar todos paginado");
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/usuarios?page=0&size=5")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarBuscarUsuarioPorNome() {
        inicio(5, "GET /usuarios/buscar?nome=Taila — buscar por nome");
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/usuarios/buscar?nome=Taila")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarAtualizarUsuario() {
        inicio(6, "PUT /usuarios/{id} — atualizar nome e foto");
        try {
            String body = """
                    {
                      "nome": "Taila Macedo Atualizada",
                      "fotoPerfil": "https://exemplo.com/nova-foto.jpg"
                    }
                    """;
            String resposta = clienteBase.put()
                    .uri("http://localhost:8080/usuarios/" + usuarioId)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarDeleteUsuarioSemAdmin() {
        inicio(7, "DELETE /usuarios/{id} — deve retornar 403 (usuário comum)");
        try {
            clienteBase.delete()
                    .uri("http://localhost:8080/usuarios/99999")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("   ⚠️  204 NO CONTENT — deletou (você está logado como ADMIN?)");
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 403) {
                ok("403 FORBIDDEN — correto! Usuário comum não pode deletar.");
            } else if (e.getStatusCode().value() == 400) {
                ok("400 — ID inexistente confirmado. Admin tem permissão mas ID não existe.");
            } else {
                erro(e);
            }
        }
        System.out.println();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //   MÓDULO PONTO TURÍSTICO
    // ══════════════════════════════════════════════════════════════════════════

    static void testarModuloPontoTuristico() {
        cabecalho("PONTO TURÍSTICO");
        testarCadastrarPontoSemToken();
        testarCadastrarPontoComUsuarioComum();
        testarBuscarPontoPorId();
        testarListarPontos();
        testarBuscarPontoPorNome();
        testarAtualizarPontoSemAdmin();
        testarDeletarPontoSemAdmin();
    }

    static void testarCadastrarPontoSemToken() {
        inicio(8, "POST /pontos sem token — deve retornar 403");
        try {
            clienteBase.post()
                    .uri("http://localhost:8080/pontos")
                    .bodyValue(bodyPonto())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("   ⚠️  201 CREATED — cadastrou sem token? Verifique o SecurityConfig!");
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 403) {
                ok("403 FORBIDDEN — correto! Sem token não cadastra ponto.");
            } else {
                erro(e);
            }
        }
        System.out.println();
    }

    static void testarCadastrarPontoComUsuarioComum() {
        inicio(9, "POST /pontos com token de USUARIO — deve retornar 403");
        try {
            String resposta = clienteBase.post()
                    .uri("http://localhost:8080/pontos")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .bodyValue(bodyPonto())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("201 CREATED — " + resposta);
            pontoId = extrairId(resposta);
            System.out.println("   → ID do ponto salvo: " + pontoId);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 403) {
                ok("403 FORBIDDEN — correto! Token de USUARIO não pode cadastrar ponto.");
                buscarPontoExistenteParaTestes();
            } else if (e.getStatusCode().value() == 400 &&
                    e.getResponseBodyAsString().contains("Já existe")) {
                ok("400 — ponto já existe no banco. Buscando o existente...");
                buscarPontoExistenteParaTestes();
            } else {
                erro(e);
            }
        }
        System.out.println();
    }

    // NOVO — método auxiliar usado quando não somos admin, pra reaproveitar um ponto já existente no banco
    static void buscarPontoExistenteParaTestes() {
        try {
            String busca = clienteBase.get()
                    .uri("http://localhost:8080/pontos/buscar?nome=Cristo")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            if (busca != null && busca.contains("\"id\":")) {
                pontoId = extrairId(busca.replace("[", "").replace("]", ""));
                System.out.println("   → ID do ponto recuperado para uso nos testes: " + pontoId);
            }
        } catch (WebClientResponseException ex) {
            erro(ex);
        }
    }

    static void testarBuscarPontoPorId() {
        inicio(10, "GET /pontos/{id} — buscar por ID (público, sem token)");
        Long id = pontoId != null ? pontoId : 1L;
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/pontos/" + id)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                System.out.println("ℹ️  404 NOT FOUND — nenhum ponto cadastrado ainda.");
            } else {
                erro(e);
            }
        }
        System.out.println();
    }

    static void testarListarPontos() {
        inicio(11, "GET /pontos?page=0&size=5 — listar todos (público, sem token)");
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/pontos?page=0&size=5")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarBuscarPontoPorNome() {
        inicio(12, "GET /pontos/buscar?nome=Cristo — buscar por nome (público)");
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/pontos/buscar?nome=Cristo")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarAtualizarPontoSemAdmin() {
        inicio(13, "PUT /pontos/{id} com token de USUARIO — deve retornar 403");
        try {
            String body = """
                    {
                      "nome": "Cristo Redentor Atualizado",
                      "descricao": "Monumento atualizado",
                      "cidade": "Rio de Janeiro",
                      "pais": "Brasil",
                      "latitude": -22.9519,
                      "longitude": -43.2105,
                      "categoria": "MONUMENTO"
                    }
                    """;
            String resposta = clienteBase.put()
                    .uri("http://localhost:8080/pontos/99999")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("   ⚠️  200 OK — atualizou (você está logado como ADMIN?) — " + resposta);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 403) {
                ok("403 FORBIDDEN — correto! Token de USUARIO não pode atualizar ponto.");
            } else if (e.getStatusCode().value() == 400) {
                ok("400 — ID inexistente confirmado. Admin tem permissão mas ID não existe.");
            } else {
                erro(e);
            }
        }
        System.out.println();
    }

    static void testarDeletarPontoSemAdmin() {
        inicio(14, "DELETE /pontos/{id} com token de USUARIO — deve retornar 403");
        try {
            clienteBase.delete()
                    .uri("http://localhost:8080/pontos/99999")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("   ⚠️  204 NO CONTENT — deletou (você está logado como ADMIN?)");
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 403) {
                ok("403 FORBIDDEN — correto! Token de USUARIO não pode deletar ponto.");
            } else if (e.getStatusCode().value() == 400) {
                ok("400 — ID inexistente confirmado. Admin tem permissão mas ID não existe.");
            } else {
                erro(e);
            }
        }
        System.out.println();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //   MÓDULO FOTO
    // ══════════════════════════════════════════════════════════════════════════

    static void testarModuloFoto() {
        cabecalho("FOTO");
        testarEnviarFotoSemToken();
        testarEnviarFoto();
        testarBuscarFotoPorId();
        testarListarFotos();
        testarBuscarFotosPorPonto();
        testarBuscarFotosPendentesComUsuarioComum();
        testarAprovarFotoComUsuarioComum();
        testarAprovarFotoComoAdmin();
        testarDeletarFoto();
    }

    static void testarEnviarFotoSemToken() {
        inicio(15, "POST /fotos sem token — deve retornar 403");
        try {
            clienteBase.post()
                    .uri("http://localhost:8080/fotos")
                    .bodyValue(bodyFoto())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("   ⚠️  201 CREATED — enviou sem token? Verifique o SecurityConfig!");
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 403) {
                ok("403 FORBIDDEN — correto! Sem token não envia foto.");
            } else {
                erro(e);
            }
        }
        System.out.println();
    }

    static void testarEnviarFoto() {
        inicio(16, "POST /fotos com token — deve retornar 201");
        if (pontoId == null) {
            pulado("nenhum ponto cadastrado ainda. Dica: Promova um usuário a ADMIN no Neon.");
            return;
        }
        try {
            String body = String.format("""
                    {
                      "url": "https://exemplo.com/foto-cristo.jpg",
                      "legenda": "Vista frontal do Cristo Redentor",
                      "idUsuario": %d,
                      "idPontoTuristico": %d
                    }
                    """, usuarioId, pontoId);
            String resposta = clienteBase.post()
                    .uri("http://localhost:8080/fotos")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("201 CREATED — " + resposta);
            fotoId = extrairId(resposta);
            System.out.println("   → ID da foto salvo: " + fotoId);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarBuscarFotoPorId() {
        inicio(17, "GET /fotos/{id} — buscar por ID");
        if (fotoId == null) { pulado("nenhuma foto cadastrada ainda"); return; }
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/fotos/" + fotoId)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarListarFotos() {
        inicio(18, "GET /fotos?page=0&size=5 — listar todas paginado");
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/fotos?page=0&size=5")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarBuscarFotosPorPonto() {
        inicio(19, "GET /fotos/ponto/{idPonto} — buscar por ponto (público, sem token)");
        Long id = pontoId != null ? pontoId : 1L;
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/fotos/ponto/" + id)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarBuscarFotosPendentesComUsuarioComum() {
        inicio(20, "GET /fotos/aprovacao?aprovado=false com USUARIO — deve retornar 403");
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/fotos/aprovacao?aprovado=false")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("   ⚠️  200 OK — listou pendentes (você está logado como ADMIN?) — " + resposta);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 403) {
                ok("403 FORBIDDEN — correto! Usuário comum não vê fotos pendentes.");
            } else {
                erro(e);
            }
        }
        System.out.println();
    }

    static void testarAprovarFotoComUsuarioComum() {
        inicio(21, "PATCH /fotos/aprovar/{id} com USUARIO — deve retornar 403");
        Long id = fotoId != null ? fotoId : 1L;
        try {
            clienteBase.patch()
                    .uri("http://localhost:8080/fotos/aprovar/" + id)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("   ⚠️  200 OK — aprovou (você está logado como ADMIN?)");
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 403) {
                ok("403 FORBIDDEN — correto! Usuário comum não pode aprovar foto.");
            } else {
                erro(e);
            }
        }
        System.out.println();
    }

    static void testarAprovarFotoComoAdmin() {
        inicio(21, "b) Login ADMIN + PATCH /fotos/aprovar/{id} — deve retornar 200 e disparar notificação");
        if (fotoId == null) {
            pulado("nenhuma foto cadastrada ainda");
            return;
        }
        try {
            String loginBody = """
                {
                  "email": "horizontemeu.adm@gmail.com",
                  "senha": "12345678"
                }
                """;
            String loginResposta = clienteBase.post()
                    .uri("http://localhost:8080/auth/login")
                    .bodyValue(loginBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (loginResposta != null && loginResposta.contains("\"token\":")) {
                tokenAdmin = loginResposta.split("\"token\":\"")[1].split("\"")[0];
            }

            String resposta = clienteBase.patch()
                    .uri("http://localhost:8080/fotos/aprovar/" + fotoId)
                    .header("Authorization", "Bearer " + tokenAdmin)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ok("200 OK — foto aprovada de verdade. Notificação FOTO_APROVADA deve ter sido criada — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarDeletarFoto() {
        inicio(22, "DELETE /fotos/{id} — remover foto");
        if (fotoId == null) { pulado("nenhuma foto cadastrada ainda"); return; }
        try {
            clienteBase.delete()
                    .uri("http://localhost:8080/fotos/" + fotoId)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("204 NO CONTENT — foto removida com sucesso.");
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //   MÓDULO COMENTARIO
    // ══════════════════════════════════════════════════════════════════════════

    static void testarModuloComentario() {
        cabecalho("COMENTÁRIO");
        testarPublicarComentarioSemToken();
        testarPublicarComentario();
        testarBuscarComentarioPorId();
        testarListarComentarios();
        testarBuscarComentariosPorPonto();
        testarBuscarComentariosPorUsuario();
        testarAtualizarComentario();
        testarCurtirComentario();
        testarDeletarComentario();
    }

    static void testarPublicarComentarioSemToken() {
        inicio(23, "POST /comentarios sem token — deve retornar 403");
        try {
            clienteBase.post()
                    .uri("http://localhost:8080/comentarios")
                    .bodyValue(bodyComentario())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("   ⚠️  201 CREATED — publicou sem token? Verifique o SecurityConfig!");
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 403) {
                ok("403 FORBIDDEN — correto! Sem token não publica comentário.");
            } else {
                erro(e);
            }
        }
        System.out.println();
    }

    static void testarPublicarComentario() {
        inicio(24, "POST /comentarios com token — deve retornar 201");
        if (pontoId == null) {
            pulado("nenhum ponto cadastrado ainda. Dica: Promova um usuário a ADMIN no Neon.");
            return;
        }
        try {
            String resposta = clienteBase.post()
                    .uri("http://localhost:8080/comentarios")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .bodyValue(bodyComentario())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("201 CREATED — " + resposta);
            comentarioId = extrairId(resposta);
            System.out.println("   → ID do comentário salvo: " + comentarioId);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarBuscarComentarioPorId() {
        inicio(25, "GET /comentarios/{id} — buscar por ID");
        if (comentarioId == null) { pulado("nenhum comentário cadastrado ainda"); return; }
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/comentarios/" + comentarioId)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarListarComentarios() {
        inicio(26, "GET /comentarios?page=0&size=5 — listar todos paginado");
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/comentarios?page=0&size=5")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarBuscarComentariosPorPonto() {
        inicio(27, "GET /comentarios/ponto/{idPonto} — buscar por ponto (público, sem token)");
        Long id = pontoId != null ? pontoId : 1L;
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/comentarios/ponto/" + id)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarBuscarComentariosPorUsuario() {
        inicio(28, "GET /comentarios/usuario/{idUsuario} — buscar por usuário");
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/comentarios/usuario/" + usuarioId)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarAtualizarComentario() {
        inicio(29, "PUT /comentarios/{id} — atualizar texto e foto");
        if (comentarioId == null) { pulado("nenhum comentário cadastrado ainda"); return; }
        try {
            String body = """
                    {
                      "texto": "Lugar incrível! Visita obrigatória no Rio. Atualizado!",
                      "fotoUrl": "https://exemplo.com/foto-atualizada.jpg"
                    }
                    """;
            String resposta = clienteBase.put()
                    .uri("http://localhost:8080/comentarios/" + comentarioId)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarCurtirComentario() {
        inicio(30, "PATCH /comentarios/{id}/curtir — incrementar curtidas");
        if (comentarioId == null) { pulado("nenhum comentário cadastrado ainda"); return; }
        try {
            String resposta = clienteBase.patch()
                    .uri("http://localhost:8080/comentarios/" + comentarioId + "/curtir")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarDeletarComentario() {
        inicio(31, "DELETE /comentarios/{id} — remover comentário");
        if (comentarioId == null) { pulado("nenhum comentário cadastrado ainda"); return; }
        try {
            clienteBase.delete()
                    .uri("http://localhost:8080/comentarios/" + comentarioId)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("204 NO CONTENT — comentário removido. Nota média do ponto recalculada!");
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //   MÓDULO FAVORITO
    // ══════════════════════════════════════════════════════════════════════════

    static void testarModuloFavorito() {
        cabecalho("FAVORITO");
        testarFavoritarSemToken();
        testarFavoritar();
        testarFavoritarDuplicado();
        testarListarFavoritosPorUsuario();
        testarRemoverFavorito();
    }

    static void testarFavoritarSemToken() {
        inicio(32, "POST /favoritos sem token — deve retornar 403");
        try {
            clienteBase.post()
                    .uri("http://localhost:8080/favoritos")
                    .bodyValue(bodyFavorito())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("   ⚠️  201 CREATED — favoritou sem token? Verifique o SecurityConfig!");
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 403) {
                ok("403 FORBIDDEN — correto! Sem token não favorita.");
            } else {
                erro(e);
            }
        }
        System.out.println();
    }

    static void testarFavoritar() {
        inicio(33, "POST /favoritos com token — deve retornar 201");
        if (pontoId == null) {
            pulado("nenhum ponto cadastrado ainda. Dica: Promova um usuário a ADMIN no Neon.");
            return;
        }
        try {
            String resposta = clienteBase.post()
                    .uri("http://localhost:8080/favoritos")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .bodyValue(bodyFavorito())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("201 CREATED — " + resposta);
            favoritoId = extrairId(resposta);
            System.out.println("   → ID do favorito salvo: " + favoritoId);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarFavoritarDuplicado() {
        inicio(34, "POST /favoritos duplicado — deve retornar 400 (RN03)");
        if (pontoId == null || favoritoId == null) {
            pulado("favorito anterior não foi criado");
            return;
        }
        try {
            clienteBase.post()
                    .uri("http://localhost:8080/favoritos")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .bodyValue(bodyFavorito())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("   ⚠️  201 CREATED — duplicata permitida? Verifique a RN03!");
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 400 &&
                    e.getResponseBodyAsString().contains("já está nos favoritos")) {
                ok("400 — correto! RN03 funcionando: ponto já favoritado.");
            } else {
                erro(e);
            }
        }
        System.out.println();
    }

    static void testarListarFavoritosPorUsuario() {
        inicio(35, "GET /favoritos/usuario/{idUsuario} — listar favoritos do usuário");
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/favoritos/usuario/" + usuarioId)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarRemoverFavorito() {
        inicio(36, "DELETE /favoritos/{id} — remover favorito");
        if (favoritoId == null) { pulado("nenhum favorito cadastrado ainda"); return; }
        try {
            clienteBase.delete()
                    .uri("http://localhost:8080/favoritos/" + favoritoId)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("204 NO CONTENT — favorito removido com sucesso.");
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //   MÓDULO NOTIFICACAO
    // ══════════════════════════════════════════════════════════════════════════

    static void testarModuloNotificacao() {
        cabecalho("NOTIFICACAO");
        testarPublicarSegundoComentarioParaNotificarFavorito();
        testarListarNotificacoes();
        testarMarcarNotificacaoComoLida();
        testarDeletarNotificacao();
    }

    static void testarPublicarSegundoComentarioParaNotificarFavorito() {
        inicio(37, "POST /comentarios (2º comentário) — deve notificar quem favoritou o ponto");
        if (pontoId == null) { pulado("nenhum ponto cadastrado ainda"); return; }
        try {
            String body = String.format("""
                {
                  "texto": "Voltei aqui e continua incrível!",
                  "nota": 5,
                  "idUsuario": %d,
                  "idPontoTuristico": %d
                }
                """, usuarioId, pontoId);
            String resposta = clienteBase.post()
                    .uri("http://localhost:8080/comentarios")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("201 CREATED — comentário publicado. Notificação COMENTARIO deve ter sido criada — " + resposta);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarListarNotificacoes() {
        inicio(38, "GET /notificacoes/usuario/{idUsuario} — listar notificações do usuário");
        try {
            String resposta = clienteBase.get()
                    .uri("http://localhost:8080/notificacoes/usuario/" + usuarioId)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("200 OK — " + resposta);
            notificacaoId = extrairId(resposta);
            System.out.println("   → ID da primeira notificação salvo: " + notificacaoId);
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarMarcarNotificacaoComoLida() {
        inicio(39, "PATCH /notificacoes/{id}/lida — marcar como lida");
        if (notificacaoId == null) { pulado("nenhuma notificação encontrada ainda"); return; }
        try {
            clienteBase.patch()
                    .uri("http://localhost:8080/notificacoes/" + notificacaoId + "/lida")
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("204 NO CONTENT — notificação marcada como lida.");
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    static void testarDeletarNotificacao() {
        inicio(40, "DELETE /notificacoes/{id} — remover notificação");
        if (notificacaoId == null) { pulado("nenhuma notificação encontrada ainda"); return; }
        try {
            clienteBase.delete()
                    .uri("http://localhost:8080/notificacoes/" + notificacaoId)
                    .header("Authorization", "Bearer " + tokenJwt)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ok("204 NO CONTENT — notificação removida com sucesso.");
        } catch (WebClientResponseException e) {
            erro(e);
        }
        System.out.println();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //   UTILITÁRIOS
    // ══════════════════════════════════════════════════════════════════════════

    static void cabecalho(String modulo) {
        System.out.println("\n════════════════════════════════════════════════");
        System.out.printf( "   TESTES — HORIZONTE MEU | módulo %-12s%n", modulo);
        System.out.println("════════════════════════════════════════════════\n");
    }

    static void inicio(int numero, String descricao) {
        System.out.printf("▶ [%02d] %s%n", numero, descricao);
    }

    static void ok(String mensagem) {
        System.out.println("   ✅ " + mensagem);
    }

    static void erro(WebClientResponseException e) {
        System.out.println("   ❌ ERRO " + e.getStatusCode() + " — " + e.getResponseBodyAsString());
    }

    static void pulado(String motivo) {
        System.out.println("   ⚠️  PULADO — " + motivo);
        System.out.println();
    }

    static Long extrairId(String json) {
        if (json == null || !json.contains("\"id\":")) return null;
        try {
            return Long.parseLong(json.split("\"id\":")[1].split("[,}]")[0].trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static String bodyPonto() {
        return """
                {
                  "nome": "Cristo Redentor",
                  "descricao": "Monumento icônico do Rio de Janeiro",
                  "cidade": "Rio de Janeiro",
                  "pais": "Brasil",
                  "latitude": -22.9519,
                  "longitude": -43.2105,
                  "categoria": "MONUMENTO"
                }
                """;
    }

    static String bodyFoto() {
        return String.format("""
                {
                  "url": "https://exemplo.com/foto-cristo.jpg",
                  "legenda": "Vista frontal do Cristo Redentor",
                  "idUsuario": %d,
                  "idPontoTuristico": %d
                }
                """, usuarioId != null ? usuarioId : 1L,
                pontoId  != null ? pontoId  : 1L);
    }

    static String bodyComentario() {
        return String.format("""
                {
                  "texto": "Lugar incrível! Visita obrigatória no Rio de Janeiro.",
                  "nota": 5,
                  "fotoUrl": "https://exemplo.com/foto-comentario.jpg",
                  "idUsuario": %d,
                  "idPontoTuristico": %d
                }
                """, usuarioId != null ? usuarioId : 1L,
                pontoId  != null ? pontoId  : 1L);
    }

    static String bodyFavorito() {
        return String.format("""
                {
                  "idUsuario": %d,
                  "idPontoTuristico": %d
                }
                """, usuarioId != null ? usuarioId : 1L,
                pontoId  != null ? pontoId  : 1L);
    }
}