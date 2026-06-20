package br.com.ifba.horizontemeu.foto.mapper;

import br.com.ifba.horizontemeu.foto.dto.FotoGetResponseDto;
import br.com.ifba.horizontemeu.foto.entity.Foto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface FotoMapper {

    //Converte uma entidade Foto para FotoGetResponseDto.
    @Mapping(source = "usuario.id", target = "idUsuario")
    @Mapping(source = "pontoTuristico.id", target = "idPontoTuristico")
    FotoGetResponseDto toDto(Foto foto);

    /**
     * Converte uma lista de Foto para lista de FotoGetResponseDto.
     * O MapStruct gera a implementação automaticamente usando o toDto() acima.
     */
    List<FotoGetResponseDto> toDtoList(List<Foto> fotos);
}