package com._7aske.grain.core.component;

import java.lang.annotation.*;

/**
 * Matches if the specified profile is active. Also, profiles marked with '!' are negated.
 */
@Conditional(OnProfileCondition.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface ConditionalOnProfile {
    String[] value();
}
