package com.example.securitydemo.config.security;

import com.example.securitydemo.config.oauth2.OAuth2Service;
import com.example.securitydemo.config.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.example.securitydemo.config.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.example.securitydemo.config.oauth2.repository.OAuth2Repository;
import com.example.securitydemo.config.security.filter.CustomAuthenticationFilter;
import com.example.securitydemo.config.security.filter.TokenAuthorizationFilter;
import com.example.securitydemo.config.security.handler.CustomAuthenticationFailureHandler;
import com.example.securitydemo.config.security.handler.CustomAuthenticationSuccessHandler;
import com.example.securitydemo.config.security.handler.CustomLogoutSuccessHandler;
import com.example.securitydemo.config.security.provider.CustomAuthenticationProvider;
import com.example.securitydemo.util.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig {

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final ObjectMapper objectMapper;
    private final WhiteListConfig whiteList;
    private final TokenAuthorizationFilter tokenAuthorizationFilter;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final OAuth2Service oAuth2Service;

    // 내부에 추가적으로 시큐리티 필터체인을 통과해야하는 요소를 삽입
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain filterChain (
            HttpSecurity http,
            CustomAuthenticationFilter customAuthenticationFilter,
            OAuth2Repository oAuth2Repository
    ) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whiteList.getWhiteList()).permitAll()
                        .requestMatchers(HttpMethod.GET, whiteList.getWhiteListForGet()).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(tokenAuthorizationFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(configuration -> configuration
                        .authorizationEndpoint(
                                config -> config
                                        .baseUri("/api/oauth2/authorization")
                                        .authorizationRequestRepository(oAuth2Repository))
                        .userInfoEndpoint(
                                config -> config
                                        .userService(oAuth2Service))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                        .loginProcessingUrl("/api/login/oauth2/code/{provider}")
                )
                // 로그아웃 시 로직. "/user/logout" 엔드포인트로 들어오는 로직을 로그아웃으로 인지해 인증 제거, 쿠키 제거, 세션 비활성화 등의 처리를 수행한다.
                .logout(logout -> logout
                        // 로그아웃 페이지에 대한 설정
                        .logoutUrl("/api/user/logout")
                        // 로그아웃 하면서 인증 정보를 삭제하고
                        .clearAuthentication(true)
                        // 쿠키를 삭제함
                        .deleteCookies(TokenType.accessToken.toString(), TokenType.refreshToken.toString())
                        // 세션 무효화
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                )
                .build();
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter(
            AuthenticationManager authenticationManager,
            CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
            CustomAuthenticationFailureHandler customAuthenticationFailureHandler
    ) {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager, objectMapper);
        // "/user/login" 엔드포인트로 들어오는 요청을 로그인으로 인지해 CustomAuthenticationFilter에서 처리하도록 지정한다.
        customAuthenticationFilter.setFilterProcessesUrl("/api/user/login");
        customAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);    // '인증' 성공 시 해당 핸들러로 처리를 전가한다.
        customAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);    // '인증' 실패 시 해당 핸들러로 처리를 전가한다.
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(CustomAuthenticationProvider customAuthenticationProvider) {
        return new ProviderManager(Collections.singletonList(customAuthenticationProvider));
    }

    // cors에 대한 설정 소스를 filterChain 밖에서 따로 설정할 수 있다.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
