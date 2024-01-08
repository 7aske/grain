package com._7aske.grain.web.controller.parameter;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.util.ReflectionUtil;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

@Grain
public class ParameterConverterRegistry {
    private final List<ParameterConverter> parameterConverters;

    public ParameterConverterRegistry(List<ParameterConverter> parameterConverters) {
        this.parameterConverters = parameterConverters;
    }

    public Optional<ParameterConverter> getConverter(Parameter parameter) {
        return parameterConverters.stream()
                .filter(converter -> converter.supports(parameter))
                .min(ReflectionUtil::sortByOrder);
    }

    public List<ParameterConverter> getParameterConverters() {
        return parameterConverters;
    }

    public void addParameterConverter(ParameterConverter parameterConverter) {
        parameterConverters.add(parameterConverter);
    }

    public void removeParameterConverter(ParameterConverter parameterConverter) {
        parameterConverters.remove(parameterConverter);
    }
}
