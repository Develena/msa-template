package com.msa.template.elena.config;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.nuri.green.onm.user.entity.results.ExceptionMessage;
import com.nuri.utils.DateUtil;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SwaggerConfig {

  @Value("${swagger.api.title}")
  private String apiTitle;
  @Value("${swagger.api.desc}")
  private String apiDesc;
  @Value("${swagger.contact.name}")
  private String contName;
  @Value("${swagger.contact.url}")
  private String contUrl;
  @Value("${swagger.contact.email}")
  private String contEmail;
  @Value("${swagger.license.name}")
  private String licenseName;
  @Value("${swagger.license.url}")
  private String licenseUrl;
  @Value("${swagger.doc.desc}")
  private String apiDocDesc;
  @Value("${swagger.doc.path}")
  private String apiDocPath;
  @Value("${swagger.path}")
  private String swaggerPath;

  @Value("${nuri.jwt.auth.name}")
  private String authToken;
  @Value("${nuri.jwt.access.name}")
  private String accessToken;

  @Bean
  public OpenAPI openAPI(@Value("${springdoc.version}") String appVersion) {

    Info info = new Info().title(apiTitle).version(appVersion)
        .description("created on <b> " + DateUtil.getCurrentDate("yyyy-MM-dd") + "</b>" + apiDesc+ "(/error.html)")
        .contact(new Contact().name(contName).url(contUrl).email(contEmail))
        .license(new License().name(licenseName).url(licenseUrl));

    return new OpenAPI()
        .info(info)
        .externalDocs(new ExternalDocumentation()
            .description(apiDocDesc)
            .url(apiDocPath))
        .components(new Components()
            .addSecuritySchemes("auth_token",
                new SecurityScheme().type(Type.APIKEY).name(authToken).in(In.HEADER)
            )
            .addSecuritySchemes("access_token",
                new SecurityScheme().type(Type.APIKEY).name(accessToken).in(In.HEADER)
            )
        ).addSecurityItem(
            new SecurityRequirement().addList("auth_token").addList("access_token"))
        ;


  }

  @Bean
  public OpenApiCustomiser openApiCustomiser() {

    return openApi -> {
      openApi.getComponents().getSchemas()
          .putAll(ModelConverters.getInstance().read(ExceptionMessage.class));

      Schema sharedErrorSchema = new Schema();
      sharedErrorSchema.setName("Error");
      sharedErrorSchema.set$ref("#/components/schemas/Error");

      openApi.getPaths().values()
          .forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
            ApiResponses apiResponses = operation.getResponses();
            apiResponses
                .addApiResponse("400", createApiResponse("BAD REQUEST", sharedErrorSchema));
            apiResponses.addApiResponse("403", createApiResponse("FORBIDDEN", sharedErrorSchema));
            apiResponses.addApiResponse("404", createApiResponse("NOT FOUND", sharedErrorSchema));
            apiResponses
                .addApiResponse("500", createApiResponse("SERVER ERROR", sharedErrorSchema));
          }));

    };

  }


  private ApiResponse createApiResponse(String message, Schema schema) {
    MediaType mediaType = new MediaType();
    mediaType.schema(schema);
    return new ApiResponse().description(message)
        .content(new Content()
            .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaType));
  }


}
