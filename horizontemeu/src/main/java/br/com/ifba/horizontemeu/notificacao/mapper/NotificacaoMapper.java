package br.com.ifba.horizontemeu.notificacao.mapper;

import br.com.ifba.horizontemeu.notificacao.dto.NotificacaoGetResponseDto;
import br.com.ifba.horizontemeu.notificacao.entity.Notificacao;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificacaoMapper {

    //todos os campos batem por nome, não precisa de @Mapping
    NotificacaoGetResponseDto toGetResponseDto(Notificacao notificacao);

    List<NotificacaoGetResponseDto> toGetResponseDtoList (List<Notificacao> notificacaos);

}
