package com.denknd.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Контекст для сваггера
 */
@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI ApiInfo() {
    SecurityScheme securityScheme = new SecurityScheme().type(SecurityScheme.Type.APIKEY).name("__Host-auth-token").in(SecurityScheme.In.COOKIE).scheme("cookie");
    return new OpenAPI()
            .info(new Info().title("Monitoring service.")
                    .description("Monitoring service представляет собой веб-сервис для управления и мониторинга показаний счетчиков." +
                            " Сервис позволяет пользователям регистрировать новые аккаунты, входить в систему," +
                            " отправлять и просматривать показания счетчиков, добавлять новые адреса и дополнять информацию о счетчиках." +
                            " Доступ к различным функциям сервиса регулируется ролями пользователей," +
                            " такими как USER и ADMIN." +
                            " Monitoring service обеспечивает удобный интерфейс для работы с показаниями счетчиков" +
                            " и предоставляет различные функции для администрирования системы," +
                            " обеспечивая управление пользователями и типами показаний счетчиков.")
                    .version("0.0.X SUPER_AGENT")
                    .contact(new Contact().name("Denis").email("fellix.knd@gmail.com").url("myUrl"))
                    .license(new License().name("Apache 2.0").url("http://springdoc.org")))
            .components(new Components()
                    .addSecuritySchemes("basicScheme", new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("basic"))
                    .addSecuritySchemes("basicCookie", securityScheme)

            )
            .addSecurityItem(new SecurityRequirement().addList("basicScheme").addList("basicCookie"));
  }

}
