package com.denknd.in.commands.functions;

import com.denknd.entity.TypeMeter;
import com.denknd.services.TypeMeterService;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * проверяет какие есть доступные типы параметров
 */
@RequiredArgsConstructor
public class TypeMeterParametersParserFromRawParameters implements Function<String[], Set<String>> {

    /**
     * Сервис для получения доступных типов параметров
     */
    private final TypeMeterService typeMeterService;

    /**
     * отделяет принятые в консоле параметры от типов доступных параметров
     * @param commandAndParam параметры из консоли
     * @return сет из параметров, доступных показаний
     */
    @Override
    public Set<String> apply(String[] commandAndParam) {
        var availableOptions = this.typeMeterService.getTypeMeter().stream().map(TypeMeter::getTypeCode).toList();
        return  Arrays.stream(commandAndParam)
                .filter(param -> availableOptions.contains(param))
                .collect(Collectors.toSet());
    }
}
