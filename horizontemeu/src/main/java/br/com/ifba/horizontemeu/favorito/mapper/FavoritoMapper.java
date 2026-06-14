package br.com.ifba.horizontemeu.favorito.mapper;

import br.com.ifba.horizontemeu.favorito.dto.FavoritoGetResponseDto;
import br.com.ifba.horizontemeu.favorito.dto.FavoritoPostRequestDto;
import br.com.ifba.horizontemeu.favorito.entity.Favorito;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoritoMapper {

    // 1. RESOLVE A VOLTA: Entidade -> DTO de Resposta
    @Mapping(source = "pontoTuristico.id", target = "idPontoTuristico")
    FavoritoGetResponseDto toGetResponseDto(Favorito favorito);

    // 2. RESOLVE AS LISTAS: Converte a lista inteira automaticamente
    List<FavoritoGetResponseDto> toGetResponseDtoList(List<Favorito> favoritos);

    // 3. RESOLVE A IDA: DTO de Envio -> Entidade (Olha a mágica aqui!)
    @Mapping(source = "idUsuario", target = "usuario.id")
    @Mapping(source = "idPontoTuristico", target = "pontoTuristico.id")
    Favorito toEntity(FavoritoPostRequestDto dto);
}