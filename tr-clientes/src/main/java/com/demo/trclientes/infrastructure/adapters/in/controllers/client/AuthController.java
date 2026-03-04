package com.demo.trclientes.infrastructure.adapters.in.controllers.client;

import com.demo.trclientes.infrastructure.config.SecurityConfig;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.OAuthTokenResponse;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.UserInfoResponse;
import com.demo.trclientes.infrastructure.adapters.in.rest.api.AuthApi;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class AuthController implements AuthApi {

    private static final Map<String, String> authCodes = new ConcurrentHashMap<>();

    @Override
    public ResponseEntity<Void> authorize(String responseType, String clientId, String redirectUri, String scope) throws Exception {
        log.info("PASO 1 OAUTH2: Solicitud de autorizaciÃ³n para cliente: {}", clientId);
        
        String code = UUID.randomUUID().toString().substring(0, 8);
        authCodes.put(code, "usuario-demo");
        
        String redirectUrl = redirectUri + "?code=" + code;
        log.info("REDIRECCIÃ“N: Usuario autenticado. Enviando cÃ³digo a: {}", redirectUrl);
        
        return ResponseEntity.status(302).header("Location", redirectUrl).build();
    }

    @Override
    public ResponseEntity<OAuthTokenResponse> getToken() throws Exception {
        log.info("PASO 2 OAUTH2: Intercambio de codigo por token.");
        
        long now = System.currentTimeMillis();
        
        // 1. Crear Access Token (OAuth2)
        String accessToken = Jwts.builder()
                .setSubject("usuario-demo")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 3600000))
                .claim("roles", "ROLE_USER")
                .signWith(SecurityConfig.SECRET_KEY)
                .compact();
                
        // 2. Crear ID Token (OIDC - OpenID Connect)
        String idToken = Jwts.builder()
                .setSubject("usuario-demo")
                .setIssuer("mi-microservicio-auth")
                .setAudience("mi-cliente-app")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 3600000))
                .claim("name", "Usuario Demo")
                .claim("email", "demo@example.com")
                .claim("preferred_username", "userdemo")
                .signWith(SecurityConfig.SECRET_KEY)
                .compact();

        OAuthTokenResponse response = new OAuthTokenResponse()
                .accessToken(accessToken)
                .idToken(idToken)
                .tokenType("Bearer")
                .expiresIn(3600);
                
        log.info("TOKENS GENERADOS: Access Token e ID Token (OIDC) creados exitosamente.");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserInfoResponse> getUserInfo() throws Exception {
        log.info("PASO 3 OIDC: Solicitud de UserInfo usando Access Token.");
        
        UserInfoResponse userInfo = new UserInfoResponse()
                .sub("usuario-demo")
                .name("Usuario Demo")
                .email("demo@example.com")
                .preferredUsername("userdemo");
                
        return ResponseEntity.ok(userInfo);
    }
}
