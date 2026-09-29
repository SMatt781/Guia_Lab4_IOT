package com.example.directorioempleados;

import com.google.gson.Gson;
import org.junit.Test;
import static org.junit.Assert.*;

// JSON mínimo de prueba: no realiza conexiones ni usa datos reales de personas.
public class ApiModelsTest {
    @Test public void parsesDomainJson() {
        ApiModels.UsersResponse response = new Gson().fromJson("{\"users\":[{\"id\":1,\"firstName\":\"Ana\",\"company\":{\"department\":\"Engineering\",\"title\":\"Developer\"}}]}", ApiModels.UsersResponse.class);
        assertEquals(1, response.users.size());
        assertEquals("Ana", response.users.get(0).firstName);
        assertEquals("Engineering", response.users.get(0).company.department);
        assertEquals("Developer", response.users.get(0).company.title);
    }

    @Test public void emptyResponsesAreRepresentable() {
        ApiModels.UsersResponse empty = new Gson().fromJson("{\"users\":[]}", ApiModels.UsersResponse.class);
        assertTrue(empty.users.isEmpty());
    }
}

