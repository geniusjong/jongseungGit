package com.lottoweb.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) 설정 클래스
 * API 문서화를 위한 설정을 담당합니다.
 * 
 * 접속 URL:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - API 문서 (JSON): http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI lottoWebOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("개발 서버");

        Server prodServer = new Server();
        prodServer.setUrl("https://yourdomain.com");
        prodServer.setDescription("프로덕션 서버");

        Contact contact = new Contact();
        contact.setEmail("your-email@example.com");
        contact.setName("Lotto Web API");
        contact.setUrl("https://github.com/yourusername/lottoSeriveStudy");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("로또 번호 추천 서비스 API")
                .version("1.0.0")
                .contact(contact)
                .description("Spring Boot 기반의 로또 번호 추천 웹 애플리케이션 REST API 문서입니다.\n\n" +
                        "## 주요 기능\n" +
                        "- 로또 번호 추첨 (가중치 기반 알고리즘)\n" +
                        "- 로또 당첨 번호 히스토리 조회 (페이징, 필터링, 통계)\n" +
                        "- 저장된 로또 번호 관리 (인증 필요)\n\n" +
                        "## 인증\n" +
                        "일부 API는 로그인이 필요합니다. 로그인 후 세션 쿠키를 사용하여 인증할 수 있습니다.\n\n" +
                        "## 사용 방법\n" +
                        "1. Swagger UI에서 직접 API를 테스트할 수 있습니다.\n" +
                        "2. 각 API의 파라미터와 응답 형식을 확인할 수 있습니다.\n" +
                        "3. \"Try it out\" 버튼을 클릭하여 실제 API를 호출할 수 있습니다.")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}

