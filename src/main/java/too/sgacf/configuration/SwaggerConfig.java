package too.sgacf.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "API de SGACF",
        description = "Sistema de Gestión de Asociados para una cooperativa financiera desarrollado para la asignatura TOO115",
        version = "1.0.0",
        contact = @Contact(
            name = "Katherine Cárdenas",
            url = "https://github.com/kathcardenas",
            email = "katherine.cardenas497@gmail.com"
        )
    ),
    servers = {
        @Server(
            description = "DEV SERVER",
            url = "http://localhost:8081"
        ),
        @Server(
            description = "PROD SERVER",
            url = "https://server.com"
        )
    }
    /*,security =  {
        @SecurityRequirement(
            name = "Security Token"
        )
    }*/
)
/*@SecurityScheme(
    name = "Security Token",
    description = "Access Token for my API",
    type = SecuritySchemeType.HTTP,
    paramName = HttpHeaders.AUTHORIZATION,
    in = SecuritySchemeIn.HEADER,
    scheme = "bearer",
    bearerFormat = "JWT"
)*/
public class SwaggerConfig {
}
