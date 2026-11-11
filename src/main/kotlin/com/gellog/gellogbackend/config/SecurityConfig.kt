package com.gellog.gellogbackend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

        @Bean
        fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
                http {
                        csrf { disable() }                                                                // CSRF 보호끄기, CSRF = 다른 사이트가 브라우저에 저장된 "쿠키"를 몰래 이용해 요청을 보내는 공격
                        cors { }                                                                          // CORS 처리 기능 켜기
                        formLogin { disable() }                                                           // 폼 로그인(/login 페이지) 끄기
                        httpBasic { disable() }                                                           // HTTP BASIC 인증(브라우저 기본 로그인 팝업) 끄기
                        sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS       // 세션 사용 안 함. 서버가 로그인 상태를 기억하지 않고, 요청마다 토큰만 보고 판단한다.
                        }
                        authorizeHttpRequests {
                                authorize("/public/**", permitAll)                       // /public/ 으로 시작하는 모든 경로 → 누구나 접근 가능
                                authorize("/error", permitAll)                           // 스프링 내부 에러 경로 → 열어둬야 404/500이 401로 둔갑하지 않는다
                                authorize(anyRequest, authenticated)                    // 위에 해당하지 않는 나머지 전부 → 인증(로그인) 필요
                        }
                        exceptionHandling {
                                authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)  // 인증 안 된 사용자가 막힌 경로에 접근했을 때의 동작.
                                                                                                          // 로그인 페이지로 이동시키는 대신 HTTP 401(Unauthorized) 상태코드만 응답한다.
                        }
                }
                return http.build()
        }

        @Bean
        fun corsConfigurationSource(): CorsConfigurationSource {                                   // 메서드 이름이 corsConfigurationSource 여야 위의 cors { }가 자동으로 찾아 쓴다.
                val config = CorsConfiguration().apply {
                        allowedOrigins = listOf("http://localhost:3000", "http://localhost:5173")       // 이 출처에서 온 요청만 허락
                        allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")     // 이 HTTP 메서드들만 허락
                        allowedHeaders = listOf("*")                                                    // 요청에 어떤 헤더를 붙여도 OK (Authorization 헤더 포함)
                        allowCredentials = true                                                         // 쿠키 같은 인증 정보가 섞인 요청도 허락
                }

                return UrlBasedCorsConfigurationSource().apply {
                        registerCorsConfiguration("/**", config)                                // 모든 경로(/**)에 위 규칙 세트를 적용
                }
        }


}