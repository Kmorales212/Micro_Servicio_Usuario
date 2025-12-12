package com.example.demo.testUnitarios;

import com.example.demo.configuration.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    @Test
    void testCustomOpenAPI_Coverage100() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();

        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        assertNotNull(openAPI, "El objeto OpenAPI no debe ser nulo");
        
        assertNotNull(openAPI.getInfo());
        assertEquals("API Microservicio Usuarios", openAPI.getInfo().getTitle());
        assertEquals("1.0", openAPI.getInfo().getVersion());
        assertEquals("Documentación de la API de Usuarios con JWT", openAPI.getInfo().getDescription());

        assertNotNull(openAPI.getSecurity());
        assertFalse(openAPI.getSecurity().isEmpty());
        assertTrue(openAPI.getSecurity().get(0).containsKey("JavaInUseSecurityScheme"));

        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("JavaInUseSecurityScheme");
        
        assertNotNull(scheme);
        assertEquals(SecurityScheme.Type.HTTP, scheme.getType());
        assertEquals("bearer", scheme.getScheme());
        assertEquals("JWT", scheme.getBearerFormat());
    }
}