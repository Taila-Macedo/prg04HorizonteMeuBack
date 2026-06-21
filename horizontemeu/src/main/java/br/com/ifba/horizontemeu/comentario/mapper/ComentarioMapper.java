package br.com.ifba.horizontemeu.comentario.mapper;

import br.com.ifba.horizontemeu.comentario.dto.ComentarioGetResponseDto;
import br.com.ifba.horizontemeu.comentario.entity.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper do MapStruct para converter Comentario → ComentarioGetResponseDto.
 *
 * Por que MapStruct aqui e não ModelMapper?
 * O ComentarioGetResponseDto tem campos PLANOS (idUsuario, idPontoTuristico),
 * mas na entidade Comentario eles estão ANINHADOS dentro de objetos:
 *   comentario.usuario.id        → idUsuario
 *   comentario.pontoTuristico.id → idPontoTuristico
 *
 * O @Mapping declara explicitamente esses mapeamentos de forma tipada e segura.
 *
 * componentModel = "spring" — gera um @Component automaticamente,
 * permitindo injeção via @RequiredArgsConstructor normalmente.
 */
@Mapper(componentModel = "spring")
public interface ComentarioMapper {
    /**
     * Converte Comentario → ComentarioGetResponseDto.
     *
     * Os campos id, texto, fotoUrl, nota, curtidas, data e editado
     * são mapeados automaticamente por terem o mesmo nome.
     *
     * Apenas os campos aninhados precisam de @Mapping explícito.
     */
    @Mapping(source = "usuario.id", target = "idUsuario")
    @Mapping(source = "pontoTuristico.id", target = "idPontoTuristico")
    ComentarioGetResponseDto toDto(Comentario comentario);

    /**
     * Converte lista de Comentario → lista de ComentarioGetResponseDto.
     * Implementação gerada automaticamente usando o toDto() acima.
     */
    List<ComentarioGetResponseDto> toDtoList(List<Comentario> comentarios);
}
