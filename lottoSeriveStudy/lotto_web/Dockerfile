# ? Dockerfile 설명:
# Dockerfile은 애플리케이션을 컨테이너 이미지로 만드는 방법을 정의합니다.
# 각 줄은 레이어를 만들고, 레이어는 캐시되어 빌드 속도가 향상됩니다.

# 1단계: 베이스 이미지 선택
# eclipse-temurin:11-jre = Java 11 실행 환경 (OpenJDK의 공식 후속 프로젝트)
# ? 왜 eclipse-temurin을 사용하나요?
# - OpenJDK의 공식 후속 프로젝트 (가장 널리 사용됨)
# - 정기적인 보안 업데이트 제공
# - Docker Hub에서 공식 지원
# - 경량 버전(jre)으로 이미지 크기 최소화
FROM eclipse-temurin:11-jre

# 2단계: 작업 디렉토리 설정
# 컨테이너 내부에서 작업할 디렉토리 지정
WORKDIR /app

# 3단계: JAR 파일 복사
# Maven 빌드로 생성된 JAR 파일을 컨테이너로 복사
# ? 왜 COPY를 나중에 하나요?
# - 소스 코드 변경 시마다 모든 레이어를 다시 빌드하지 않도록
# - 의존성(dependencies)은 자주 바뀌지 않으므로 위에 두고,
# - 소스 코드는 자주 바뀌므로 아래에 둡니다
# - 이렇게 하면 빌드 시간이 단축됩니다!
COPY target/lotto-web-1.0.0.jar app.jar

# 4단계: 포트 노출
# 애플리케이션이 사용할 포트를 외부에 노출
# ? EXPOSE는 실제로 포트를 열지 않고, 문서화 목적
# 실제 포트는 docker run -p 옵션으로 매핑합니다
EXPOSE 8080

# 5단계: 실행 명령
# 컨테이너가 시작될 때 실행할 명령
# ? ENTRYPOINT vs CMD:
# - ENTRYPOINT: 항상 실행 (변경 불가)
# - CMD: 기본값으로 사용 (변경 가능)
# exec 형식 사용: ["java", "-jar", "app.jar"]
# - WORKDIR이 /app이므로 상대 경로 app.jar 사용
# - 셸 형식보다 효율적 (프로세스 직접 실행)
ENTRYPOINT ["java", "-jar", "app.jar"]

# ? Dockerfile 작성 팁:
# 1. 레이어 순서: 자주 바뀌지 않는 것 → 자주 바뀌는 것 순서로
# 2. 캐시 활용: COPY는 자주 바뀌는 파일을 나중에
# 3. 경량 이미지: 불필요한 파일 제거, slim 이미지 사용
# 4. 보안: root 사용자 권한 최소화 (선택사항)

