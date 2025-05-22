package org.telemis.bowling.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Dev Server");

        Info info = new Info()
                .title("Ancient African Bowling Game API")
                .version("1.0")
                .description("This API exposes endpoints to manage Ancient African Bowling games.");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
//Swagger Endpoints:
//Swagger UI: http://localhost:8080/swagger-ui/index.html
//OpenAPI JSON: http://localhost:8080/v3/api-docs
//OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml
} 