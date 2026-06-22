package br.com.ifba.horizontemeu.roteiro.mapper;

import br.com.ifba.horizontemeu.roteiro.dto.RoteiroGetResponseDto;
import br.com.ifba.horizontemeu.roteiro.dto.RoteiroPostRequestDto;
import br.com.ifba.horizontemeu.roteiro.entity.Roteiro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper MapStruct para o módulo Roteiro.
 *
 * toGetResponseDto — mapeia:
 *   roteiro.usuario.id → dto.idUsuario
 *   roteiro.pontos     → dto.pontos (delegado ao RoteiroNoPontoMapper via uses)
 *   roteiro.publico    → dto.publico (mesmo nome, mapeado automaticamente)
 *
 * toEntity — ignora pontos e id (montados manualmente no service)
 */
@Mapper(componentModel = "spring",
        uses = {br.com.ifba.horizontemeu.roteiroponto.mapper.RoteiroNoPontoMapper.class})
public interface RoteiroMapper {

    @Mapping(source = "usuario.id", target = "idUsuario")
    RoteiroGetResponseDto toGetResponseDto(Roteiro roteiro);

    List<RoteiroGetResponseDto> toGetResponseDtoList(List<Roteiro> roteiros);

    // Pontos e id são montados manualmente no service — ignorados aqui
    @Mapping(target = "pontos", ignore = true)
    @Mapping(target = "id", ignore = true)
    Roteiro toEntity(RoteiroPostRequestDto dto);
}