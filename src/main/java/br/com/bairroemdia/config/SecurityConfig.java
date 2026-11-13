package br.com.bairroemdia.config;

import br.com.bairroemdia.handler.ApiExceptionHandler.Erro;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    OpaqueTokenIntrospector adminTokenIntrospector(@Value("${app.admin.token:}") String configuredToken) {
        if (!configuredToken.isBlank() && configuredToken.length() < 32) {
            throw new IllegalArgumentException("ADMIN_TOKEN deve ter pelo menos 32 caracteres.");
        }
        byte[] expected = digest(configuredToken);
        return token -> {
            if (configuredToken.isBlank() || !MessageDigest.isEqual(expected, digest(token))) {
                throw new BadOpaqueTokenException("Chave de gestão inválida.");
            }
            return new DefaultOAuth2AuthenticatedPrincipal("gestor", Map.of("sub", "gestor"),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        };
    }

    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http, OpaqueTokenIntrospector introspector,
                                    ObjectMapper json) throws Exception {
        // Authentication uses only an explicit Bearer header, never cookies or HTTP Basic.
        http.csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .requestCache(cache -> cache.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/gestao/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/**").permitAll()
                .requestMatchers(HttpMethod.HEAD, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/ocorrencias", "/ocorrencias/*/atualizacoes", "/atualizacoes").permitAll()
                .requestMatchers("/categorias", "/categorias/**", "/ocorrencias", "/ocorrencias/**", "/atualizacoes", "/atualizacoes/**").hasRole("ADMIN")
                .anyRequest().permitAll())
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint((req, res, error) -> writeError(json, req, res, 401, "Acesso restrito à equipe responsável."))
                .accessDeniedHandler((req, res, error) -> writeError(json, req, res, 403, "Você não tem permissão para realizar esta operação.")))
            .oauth2ResourceServer(resource -> resource
                .opaqueToken(token -> token.introspector(introspector))
                .authenticationEntryPoint((req, res, error) -> writeError(json, req, res, 401, "Chave de gestão inválida ou ausente."))
                .accessDeniedHandler((req, res, error) -> writeError(json, req, res, 403, "Você não tem permissão para realizar esta operação.")));
        return http.build();
    }

    private static byte[] digest(String value) {
        try { return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)); }
        catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }

    private static void writeError(ObjectMapper json, HttpServletRequest request,
                                   HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Cache-Control", "no-store");
        json.writeValue(response.getOutputStream(), new Erro(Instant.now(), status, message, request.getRequestURI(), Map.of()));
    }
}
