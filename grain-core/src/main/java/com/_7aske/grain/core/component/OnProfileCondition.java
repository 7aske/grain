package com._7aske.grain.core.component;

import com._7aske.grain.gtl.interpreter.Interpreter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com._7aske.grain.core.configuration.Configuration.PROFILES_ENV_VARIABLE;

/**
 * Condition evaluator for {@link ConditionalOnProfile}.
 */
public class OnProfileCondition extends AbstractConditionEvaluator<ConditionalOnProfile> {
    public OnProfileCondition(Class<ConditionalOnProfile> type) {
        super(type);
    }

    @Override
    public boolean evaluate(ConditionalOnProfile annotation, Injectable injectable, DependencyContainer container, Interpreter interpreter) {
        String profilesString = Optional.ofNullable(System.getenv(PROFILES_ENV_VARIABLE))
                .orElse(",");
        List<String> profiles = Arrays.stream(profilesString
                        .split("\\s*,\\s*"))
                .toList();

        for (String profile : annotation.value()) {
            if (profile.startsWith("!")) {
                if (profiles.contains(profile.substring(1))) {
                    return false;
                }
            } else if (!profiles.contains(profile)) {
                return false;
            }
        }

        return true;
    }
}
