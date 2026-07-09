package br.com.ifba.horizontemeu.notificacao.dto;

import br.com.ifba.horizontemeu.notificacao.enums.TipoNotificacao;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoGetResponseDto {

    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "mensagem")
    private String mensagem;

    @JsonProperty(value = "lida")
    private Boolean lida;

    @JsonProperty(value = "data")
    private LocalDateTime data;

    @JsonProperty(value = "tipo")
    private TipoNotificacao tipo;
}
