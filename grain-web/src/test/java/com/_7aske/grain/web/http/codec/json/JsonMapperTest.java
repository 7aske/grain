package com._7aske.grain.web.http.codec.json;

import com._7aske.grain.web.http.codec.json.JsonMapper;
import com._7aske.grain.web.http.codec.json.annotation.JsonAlias;
import com._7aske.grain.web.http.codec.json.annotation.JsonIgnore;
import com._7aske.grain.web.http.codec.json.nodes.JsonNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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

        User parsed = (User) jsonMapper.parseValue(json, User.class);

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

    @Test
    void test_jsonPrettyPrint() throws IOException {
        User manager = new User();
        manager.username = "bob";
        manager.password = "bigsecret";

        User user = new User();
        user.password = "secret";
        user.username = "john";
        user.manager = manager;

        JsonMapper jsonMapper = new JsonMapper();
        JsonNode root = jsonMapper.mapValue(user);
        String value = jsonMapper.stringifyValue(root, true);

        assertNotEquals(0 , value.length());
        System.out.println(value);
    }
}