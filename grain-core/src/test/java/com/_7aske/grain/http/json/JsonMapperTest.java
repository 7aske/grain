package com._7aske.grain.http.json;

import com._7aske.grain.http.json.annotation.JsonAlias;
import com._7aske.grain.http.json.annotation.JsonIgnore;
import com._7aske.grain.http.json.nodes.JsonNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonMapperTest {

    static class User {
        @JsonAlias("email")
        String username;

        @JsonIgnore
        String password;

        User manager;
    }

    @Test
    void test_mapValue_fromJson() {
        User manager = new User();
        manager.username = "manager";
        manager.password = "bigsecret";

        User user = new User();
        user.password = "secret";
        user.username = "username";
        user.manager = manager;

        String json = """
                {
                    "email": "username",
                    "password": "secret",
                    "manager": {
                        "username": "manager",
                        "password": "bigsecret"
                    }
                }
                """;


        JsonMapper jsonMapper = new JsonMapper();

        User parsed = jsonMapper.parseValue(json, User.class);

        assertEquals(user.username, parsed.username);
        assertNull(parsed.password);
        assertEquals(manager.username, parsed.manager.username);
        assertNull(parsed.manager.password);
    }


    @Test
    void test_mapValue_toJson() {
        User manager = new User();
        manager.username = "manager";
        manager.password = "bigsecret";

        User user = new User();
        user.password = "secret";
        user.username = "username";
        user.manager = manager;


        JsonMapper jsonMapper = new JsonMapper();

        JsonNode parsed = jsonMapper.mapValue(user);

        assertEquals(user.username, parsed.get("username").getString());
        assertNull(parsed.get("password"));
        assertEquals(manager.username, parsed.get("manager").get("username").getString());
        assertNull(parsed.get("manager").get("password"));
    }
}