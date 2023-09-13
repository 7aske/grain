package com._7aske.grain;

/**
 * Utility class to allow easier bootstrapping of Grain applications for
 * development purposes.
 */
class GrainAppMain {
    private GrainAppMain() {}

    public static void main(String[] args) {
        GrainAppRunner.run(GrainAppRunner.class);
    }
}
