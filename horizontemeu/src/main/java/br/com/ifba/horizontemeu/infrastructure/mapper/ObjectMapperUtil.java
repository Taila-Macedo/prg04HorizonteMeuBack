package br.com.ifba.horizontemeu.infrastructure.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;
import org.modelmapper.config.Configuration;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ObjectMapperUtil {

    private static final ModelMapper MODEL_MAPPER;

    // Bloco estático — inicializa e configura o ModelMapper uma única vez!
    static {
        MODEL_MAPPER = new ModelMapper();

        // As configurações vieram para cá. Elas rodam apenas 1 vez quando a API liga.
        MODEL_MAPPER.getConfiguration()
                .setAmbiguityIgnored(true)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                // Permite acessar campos privados das classes
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
    }

    // Converte um único objeto de Input para Output
    public <Input, Output> Output map(final Input object, final Class<Output> clazz) {
        return MODEL_MAPPER.map(object, clazz);
    }

    // Converte uma lista inteira de Input para uma lista de Output
    public <Input, Output> List<Output> mapAll(final List<Input> list, final Class<Output> clazz) {
        return list.stream()
                .map(obj -> map(obj, clazz))
                .collect(Collectors.toList());
    }
}