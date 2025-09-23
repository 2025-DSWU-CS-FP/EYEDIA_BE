package com.eyedia.eyedia.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "EYEDIA API", version = "v1"))
@SecurityScheme(
        name = "naver-oauth",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "https://nid.naver.com/oauth2.0/authorize",
                        tokenUrl = "https://nid.naver.com/oauth2.0/token",
                        scopes = { @OAuthScope(name = "name", description = "name") }
                )
        )
)
public class OpenApiConfig { }
