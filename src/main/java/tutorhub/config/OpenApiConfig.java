package tutorhub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sets up the Swagger UI:
 *  - a global "bearerAuth" scheme so the Authorize button accepts your JWT
 *  - an X-Academy-Id header field shown on every operation, so you can exercise
 *    the academy-scoped endpoints from the UI.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "TutorHub API",
                version = "v1",
                description = "Multi-tenant tutoring platform API. Log in via /api/auth/login, "
                        + "click Authorize and paste the token, then set X-Academy-Id on "
                        + "academy-scoped endpoints."
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public OperationCustomizer academyHeaderCustomizer() {
        return (operation, handlerMethod) -> {
            operation.addParametersItem(new Parameter()
                    .in("header")
                    .name("X-Academy-Id")
                    .required(false)
                    .schema(new IntegerSchema().format("int64"))
                    .description("Active academy id — required for academy-scoped endpoints "
                            + "(courses, members, assignments, submissions)."));
            return operation;
        };
    }
}