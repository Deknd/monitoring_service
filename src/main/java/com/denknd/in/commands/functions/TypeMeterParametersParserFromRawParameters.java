package com.denknd.in.commands.functions;

import com.denknd.entity.TypeMeter;
import com.denknd.services.TypeMeterService;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TypeMeterParametersParserFromRawParameters implements Function<String[], Set<String>> {

    private final TypeMeterService typeMeterService;

    @Override
    public Set<String> apply(String[] commandAndParam) {
        var availableOptions = this.typeMeterService.getTypeMeter().stream().map(TypeMeter::getTypeCode).toList();
        return  Arrays.stream(commandAndParam)
                .filter(param -> availableOptions.contains(param))
                .collect(Collectors.toSet());
    }
}
