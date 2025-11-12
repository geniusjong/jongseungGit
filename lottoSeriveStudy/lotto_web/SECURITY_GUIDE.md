# 보안 설정 가이드

민감한 정보(DB 비밀번호, 이메일 비밀번호)를 환경 변수로 관리하는 방법입니다.

## 1. 로컬 개발 환경 설정

### .env 파일 생성

프로젝트 루트 디렉토리에 `.env` 파일을 생성하고 다음 내용을 입력하세요:

```bash
# 데이터베이스 설정
DB_USERNAME=root
DB_PASSWORD=1234

# 이메일 설정 (Gmail)
MAIL_USERNAME=itoweb9@gmail.com
MAIL_PASSWORD=your_gmail_app_password_here
```

**참고:**
- `.env` 파일은 `.gitignore`에 포함되어 있어 Git에 커밋되지 않습니다.
- `env.example` 파일을 복사하여 `.env` 파일을 만들 수 있습니다.

### Docker Compose 실행

```bash
# .env 파일이 있으면 자동으로 로드됩니다
docker compose up -d
```

## 2. GitHub Actions (CI/CD) 설정

GitHub Actions에서 환경 변수를 사용하려면 GitHub Secrets를 설정해야 합니다.

### GitHub Secrets 설정 방법

1. GitHub 저장소 → **Settings** → **Secrets and variables** → **Actions**
2. **New repository secret** 클릭
3. 다음 Secrets를 추가:

#### 필수 Secrets

| Secret 이름 | 설명 | 예시 값 |
|------------|------|---------|
| `DOCKER_USERNAME` | Docker Hub 사용자명 | jongseungpark |
| `DOCKER_PASSWORD` | Docker Hub 비밀번호 | your_docker_password |
| `DB_PASSWORD` | 데이터베이스 비밀번호 | your_db_password |
| `MAIL_USERNAME` | 이메일 주소 | your_email@gmail.com |
| `MAIL_PASSWORD` | 이메일 앱 비밀번호 | your_gmail_app_password |

### GitHub Actions 워크플로우 수정

`.github/workflows/build.yml` 파일에 환경 변수를 추가해야 합니다:

```yaml
jobs:
  build:
    steps:
      # ... 기존 단계들 ...
      
      - name: Build Docker image
        working-directory: lottoSeriveStudy/lotto_web
        env:
          DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
          MAIL_USERNAME: ${{ secrets.MAIL_USERNAME }}
          MAIL_PASSWORD: ${{ secrets.MAIL_PASSWORD }}
        run: |
          docker build -t jongseungpark/lotto-web:latest .
```

## 3. 프로덕션 배포 환경

### 클라우드 서버 (AWS, Azure, GCP 등)

서버의 환경 변수 설정 방법:

#### Docker Compose 사용 시
```bash
# 서버에 .env 파일 생성
nano .env

# 또는 환경 변수 직접 설정
export DB_PASSWORD=your_password
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password
```

#### Kubernetes 사용 시
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: lotto-secrets
type: Opaque
stringData:
  DB_PASSWORD: your_password
  MAIL_USERNAME: your_email@gmail.com
  MAIL_PASSWORD: your_app_password
```

## 4. 보안 체크리스트

- [ ] `.env` 파일이 `.gitignore`에 포함되어 있는지 확인
- [ ] `application-docker.properties`에 하드코딩된 비밀번호가 없는지 확인
- [ ] GitHub Secrets에 모든 민감한 정보가 설정되어 있는지 확인
- [ ] 프로덕션 환경의 환경 변수가 올바르게 설정되어 있는지 확인

## 5. 문제 해결

### 환경 변수가 로드되지 않는 경우

1. `.env` 파일이 프로젝트 루트에 있는지 확인
2. Docker Compose가 `.env` 파일을 읽는지 확인:
   ```bash
   docker compose config
   ```
3. 환경 변수 값이 올바른지 확인:
   ```bash
   docker compose exec app env | grep -E "DB_|MAIL_"
   ```

### GitHub Actions에서 실패하는 경우

1. GitHub Secrets가 올바르게 설정되어 있는지 확인
2. 워크플로우 파일에서 `secrets.` 접두사를 사용했는지 확인
3. Secret 이름이 정확한지 확인 (대소문자 구분)

