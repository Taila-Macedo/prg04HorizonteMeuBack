package br.com.ifba.horizontemeu.favorito.mapper;

import br.com.ifba.horizontemeu.favorito.dto.FavoritoGetResponseDto;
import br.com.ifba.horizontemeu.favorito.dto.FavoritoPostRequestDto;
import br.com.ifba.horizontemeu.favorito.entity.Favorito;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoritoMapper {

    // Entidade → DTO de resposta
    // id é mapeado automaticamente (mesmo nome)
    @Mapping(source = "pontoTuristico.id", target = "idPontoTuristico")
    FavoritoGetResponseDto toGetResponseDto(Favorito favorito);

    // Lista de entidades → lista de DTOs
    List<FavoritoGetResponseDto> toGetResponseDtoList(List<Favorito> favoritos);

    // DTO de entrada → Entidade (casca com só os IDs — service completa)
    @Mapping(source = "idUsuario", target = "usuario.id")
    @Mapping(source = "idPontoTuristico", target = "pontoTuristico.id")
    Favorito toEntity(FavoritoPostRequestDto dto);
}