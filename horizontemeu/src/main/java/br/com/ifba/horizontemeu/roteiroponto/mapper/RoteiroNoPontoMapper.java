package br.com.ifba.horizontemeu.roteiroponto.mapper; // Tudo minúsculo aqui!

import br.com.ifba.horizontemeu.roteiro.dto.RoteiroNoPontoResponseDto;
import br.com.ifba.horizontemeu.roteiroponto.entity.RoteiroNoPonto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RoteiroNoPontoMapper {

    @Mapping(source = "pontoTuristico.id", target = "idPontoTuristico")
    @Mapping(source = "pontoTuristico.nome", target = "nomePontoTuristico")
    RoteiroNoPontoResponseDto toResponseDto(RoteiroNoPonto roteiroNoPonto);

    List<RoteiroNoPontoResponseDto> toResponseDtoList(List<RoteiroNoPonto> pontos);
}