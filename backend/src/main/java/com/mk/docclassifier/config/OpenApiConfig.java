package com.mk.docclassifier.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("DocClassifier API")
                        .description("""
                                ## 📄 DocClassifier - Intelligent Document Classification Platform
                                
                                This API provides endpoints for:
                                - **Authentication**: User registration and login with JWT
                                - **Documents**: Upload, search, classify, and manage documents
                                - **Categories**: Manage document categories
                                - **Tags**: Organize documents with custom tags
                                - **Statistics**: Dashboard and analytics data
                                - **Real-time Events**: SSE for document processing updates
                                
                                ### 🔐 Authentication
                                Most endpoints require JWT authentication. Use the `/api/auth/login` endpoint 
                                to get a token, then click the **Authorize** button and enter: `Bearer <your-token>`
                                
                                ### 📊 Document Processing Flow
                                1. Upload document → Status: `UPLOADED`
                                2. OCR extraction → Status: `PROCESSING`
                                3. AI Classification → Status: `PROCESSED` or `ERROR`
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("El Mehdi KACHOURE")
                                .url("https://github.com/KACHOURElmehdi")
                                .email("contact@docclassifier.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://docclassifier-backend.onrender.com")
                                .description("Production Server")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token obtained from /api/auth/login")));
    }
}
