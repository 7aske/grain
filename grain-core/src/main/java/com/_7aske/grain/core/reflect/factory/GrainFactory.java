package com._7aske.grain.core.reflect.factory;

import com._7aske.grain.core.component.Injectable;
import com._7aske.grain.core.component.Ordered;

public interface GrainFactory extends Ordered {
    boolean supports(Injectable dependency);

    <T> T create(Injectable dependency, Object[] args);
}
