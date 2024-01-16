package com._7aske.grain.core;

import com._7aske.grain.exception.GrainInitializationException;

public interface ApplicationEntryPoint {
    void run() throws GrainInitializationException;
}
