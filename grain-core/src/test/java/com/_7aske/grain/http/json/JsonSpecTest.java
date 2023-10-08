package com._7aske.grain.http.json;

import com._7aske.grain.exception.json.JsonDeserializationException;
import com._7aske.grain.http.json.nodes.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonSpecTest {
    JsonParser parser;

    @BeforeEach
    void setUp() {
        parser = new JsonParser();
    }

    @ParameterizedTest
    @MethodSource("com._7aske.grain.http.json.JsonSpecTest#getJsonSpecsY")
    void test_jsonSpecY(Path file) throws IOException {
        String jsonString = Files.readString(file);

        System.err.println(jsonString);

        JsonNode parsed = parser.parse(jsonString);
        assertNotNull(parsed);
    }

    @ParameterizedTest
    @MethodSource("com._7aske.grain.http.json.JsonSpecTest#getJsonSpecsN")
    void test_jsonSpecN(Path file) throws IOException {
        byte[] jsonBytes = Files.readAllBytes(file);
        String jsonString = new String(jsonBytes);

        System.err.println(jsonString);

        assertThrows(JsonDeserializationException.class, () -> parser.parse(jsonString));
    }

    @ParameterizedTest
    @MethodSource("com._7aske.grain.http.json.JsonSpecTest#getJsonSpecsI")
    void test_jsonSpecI(Path file) throws IOException {
        byte[] jsonBytes = Files.readAllBytes(file);
        String jsonString = new String(jsonBytes);

        System.err.println(jsonString);


        parser.parse(jsonString);
    }


    static Stream<Arguments> getJsonSpecsY() throws IOException {
        return Files.list(Path.of("src/test/resources/json"))
                .filter(path -> path.toString().startsWith("src/test/resources/json/y_"))
                .filter(path -> path.toString().endsWith(".json"))
                .sorted()
                .map(Arguments::of);
    }

    static Stream<Arguments> getJsonSpecsN() throws IOException {
        return Files.list(Path.of("src/test/resources/json"))
                .filter(path -> path.toString().startsWith("src/test/resources/json/n_"))
                .filter(path -> path.toString().endsWith(".json"))
                .sorted()
                .map(Arguments::of);
    }

    static Stream<Arguments> getJsonSpecsI() throws IOException {
        return Files.list(Path.of("src/test/resources/json"))
                .filter(path -> path.toString().startsWith("src/test/resources/json/i_"))
                .filter(path -> path.toString().endsWith(".json"))
                .sorted()
                .map(Arguments::of);
    }
}
