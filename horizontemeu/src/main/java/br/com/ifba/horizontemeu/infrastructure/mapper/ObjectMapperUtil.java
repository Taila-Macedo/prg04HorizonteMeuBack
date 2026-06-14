package br.com.ifba.horizontemeu.infrastructure.mapper;

import br.com.ifba.horizontemeu.favorito.dto.FavoritoGetResponseDto;
import br.com.ifba.horizontemeu.favorito.entity.Favorito;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;
import org.modelmapper.config.Configuration;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ObjectMapperUtil {

    private static final ModelMapper MODEL_MAPPER;

    static {
        MODEL_MAPPER = new ModelMapper();

        MODEL_MAPPER.getConfiguration()
                .setAmbiguityIgnored(true)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        // Mapeamento customizado: Favorito -> FavoritoGetResponseDto
        // Necessário porque idPontoTuristico (Long) no DTO não casa
        // diretamente com pontoTuristico (objeto) na entidade.
        MODEL_MAPPER.typeMap(Favorito.class, FavoritoGetResponseDto.class)
                .addMapping(src -> src.getPontoTuristico().getId(),
                        FavoritoGetResponseDto::setIdPontoTuristico);
    }

    public <Input, Output> Output map(final Input object, final Class<Output> clazz) {
        Output c = MODEL_MAPPER.map(object, clazz);
        return c;
    }

    public <Input, Output> List<Output> mapAll(final List<Input> list, final Class<Output> clazz) {
        return list.stream()
                .map(obj -> map(obj, clazz))
                .collect(Collectors.toList());
    }
}