package br.com.ifba.horizontemeu.denuncia.mapper;

import br.com.ifba.horizontemeu.denuncia.dto.DenunciaGetResponseDto;
import br.com.ifba.horizontemeu.denuncia.entity.Denuncia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DenunciaMapper {

    @Mapping(source = "usuario.id", target = "idUsuario")
    @Mapping(source = "foto.id", target = "idFoto")
    @Mapping(source = "comentario.id", target = "idComentario")
    @Mapping(source = "usuarioDenunciado.id", target = "idUsuarioDenunciado")
    DenunciaGetResponseDto toDto(Denuncia denuncia);

    List<DenunciaGetResponseDto> toDtoList(List<Denuncia> denuncias);
}