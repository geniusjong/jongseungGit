package com.lottoweb.config;

import com.lottoweb.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 설정 클래스
 * 보안 설정, 인증/인가 처리 등을 담당합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 비밀번호 암호화 인코더 빈 등록
     * BCrypt 해시 알고리즘을 사용하여 사용자 비밀번호를 암호화합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 관리자 설정
     * 사용자 인증 정보를 처리하는 서비스를 설정합니다.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    /**
     * HTTP 보안 설정
     * URL별 접근 권한, 로그인/로그아웃 설정 등을 처리합니다.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (REST API용)
                .csrf().disable()
                
                // URL별 접근 권한 설정
                .authorizeRequests()
                    // Swagger UI 및 API 문서 접근 허용
                    .antMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                    // 로그인 없이 접근 가능한 페이지 및 정적 리소스
                    .antMatchers("/login", "/register", "/register-success", "/verify-email", 
                                "/css/**", "/js/**", "/images/**").permitAll()
                    // REST API 접근 권한 설정 (인증 필요 여부)
                    // 다음 API는 인증 없이 접근 가능하도록 설정했습니다:
                    // 1. 조회용 API: 로또 번호 추첨, 조회 등은 인증 없이 사용 가능
                    // 2. 테스트 편의성: Postman 등으로 테스트할 때 편리함
                    // 3. 모바일 앱 연동: 추후 모바일 앱에서 인증 없이 조회 가능
                    // 향후 필요시 JWT 토큰 인증을 추가하여 인증을 강화할 수 있습니다.
                    // 로또 번호 저장/삭제 API는 인증 필요 (로그인한 사용자만 가능)
                    .antMatchers("/api/lotto/save").authenticated()
                    .antMatchers("/api/lotto/saved/**").authenticated()
                    // 나머지 REST API는 모두 허용 (조회용 API)
                    .antMatchers("/api/**").permitAll()
                    // 그 외 모든 요청은 인증 필요
                    .anyRequest().authenticated()
                    .and()
                
                // 로그인 설정
                .formLogin()
                    // 로그인 페이지 URL
                    .loginPage("/login")
                    // 로그인 처리 URL (POST)
                    .loginProcessingUrl("/login")
                    // 로그인 성공 시 이동할 URL
                    .defaultSuccessUrl("/", true)
                    // 로그인 실패 시 이동할 URL
                    .failureUrl("/login?error=true")
                    // 로그인 폼의 username 파라미터명
                    .usernameParameter("username")
                    // 로그인 폼의 password 파라미터명
                    .passwordParameter("password")
                    .permitAll()
                    .and()
                
                // 로그아웃 설정
                .logout()
                    // 로그아웃 URL
                    .logoutUrl("/logout")
                    // 로그아웃 성공 시 이동할 URL
                    .logoutSuccessUrl("/login?logout=true")
                    // 세션 무효화
                    .invalidateHttpSession(true)
                    // 쿠키 삭제
                    .deleteCookies("JSESSIONID")
                    .permitAll();
    }
}
