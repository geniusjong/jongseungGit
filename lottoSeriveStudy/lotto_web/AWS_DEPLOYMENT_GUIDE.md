# AWS 배포 가이드

AWS EC2를 사용하여 로또 번호 추천 서비스를 배포하는 방법을 안내합니다.

## 배포 아키텍처

```
인터넷
  ↓
AWS EC2 (Ubuntu)
  ├── Docker Compose
  │   ├── MariaDB (컨테이너)
  │   └── Spring Boot App (컨테이너)
  └── Nginx (선택사항 - HTTPS용)
```

## 1단계: AWS EC2 인스턴스 생성

### 1.1 EC2 대시보드 접속

1. AWS 콘솔 로그인: https://console.aws.amazon.com
2. EC2 서비스 선택
3. "인스턴스 시작" 클릭

### 1.2 인스턴스 설정

**이름 및 태그:**
- 이름: `lotto-web-server`

**애플리케이션 및 OS 이미지:**
- Ubuntu Server 22.04 LTS (또는 20.04 LTS)
- 64-bit (x86)

**인스턴스 유형:**
- **프리 티어**: t2.micro (1 vCPU, 1GB RAM) - 테스트용
- **프로덕션**: t3.small (2 vCPU, 2GB RAM) 이상 권장

**키 페어:**
- 새 키 페어 생성 또는 기존 키 페어 선택
- 키 페어 이름: `lotto-web-key`
- 키 페어 유형: RSA
- 프라이빗 키 파일 형식: .pem
- **중요**: 키 페어 파일을 안전하게 보관하세요!

**네트워크 설정:**
- VPC: 기본 VPC 사용
- 서브넷: 기본 서브넷
- 퍼블릭 IP 자동 할당: 활성화
- 보안 그룹: 새 보안 그룹 생성
  - 이름: `lotto-web-sg`
  - 설명: `Lotto Web Application Security Group`
  - 인바운드 규칙:
    - SSH (포트 22): 내 IP 또는 0.0.0.0/0 (개발용)
    - HTTP (포트 80): 0.0.0.0/0
    - HTTPS (포트 443): 0.0.0.0/0
    - Custom TCP (포트 8080): 0.0.0.0/0 (테스트용)

**스토리지:**
- 볼륨 크기: 20GB (프리 티어) 또는 30GB 이상
- 볼륨 유형: gp3 (SSD)

**고급 세부 정보 (선택사항):**
- 사용자 데이터 (User data)에 다음 스크립트 추가:

```bash
#!/bin/bash
apt-get update
apt-get install -y docker.io docker-compose-plugin
systemctl start docker
systemctl enable docker
usermod -aG docker ubuntu
```

### 1.3 인스턴스 시작

- "인스턴스 시작" 클릭
- 인스턴스가 실행될 때까지 대기 (2-3분)

---

## 2단계: EC2 인스턴스 접속

### 2.1 SSH 접속 (Windows)

**PowerShell 또는 Git Bash 사용:**

```bash
# 키 파일 권한 설정 (Git Bash)
chmod 400 lotto-web-key.pem

# SSH 접속
ssh -i lotto-web-key.pem ubuntu@<EC2-PUBLIC-IP>

# 예시:
# ssh -i lotto-web-key.pem ubuntu@3.34.123.45
```

**EC2 Public IP 확인:**
- EC2 대시보드 → 인스턴스 선택 → 퍼블릭 IPv4 주소 확인

### 2.2 SSH 접속 (Mac/Linux)

```bash
# 키 파일 권한 설정
chmod 400 lotto-web-key.pem

# SSH 접속
ssh -i lotto-web-key.pem ubuntu@<EC2-PUBLIC-IP>
```

---

## 3단계: 서버 환경 설정

### 3.1 시스템 업데이트

```bash
# 시스템 업데이트
sudo apt update
sudo apt upgrade -y
```

### 3.2 Docker 설치 (User data에 추가하지 않은 경우)

```bash
# Docker 설치
sudo apt install -y docker.io docker-compose-plugin

# Docker 서비스 시작 및 자동 시작 설정
sudo systemctl start docker
sudo systemctl enable docker

# 현재 사용자를 docker 그룹에 추가 (sudo 없이 docker 사용)
sudo usermod -aG docker ubuntu

# 그룹 변경사항 적용 (재접속 필요)
newgrp docker

# Docker 설치 확인
docker --version
docker compose version
```

### 3.3 프로젝트 디렉토리 생성

```bash
# 홈 디렉토리로 이동
cd ~

# 프로젝트 디렉토리 생성
mkdir -p lotto-web
cd lotto-web
```

---

## 4단계: Docker Compose 설정

### 4.1 docker-compose.yml 파일 생성

```bash
nano docker-compose.yml
```

다음 내용을 입력:

```yaml
version: '3.8'

services:
  # MariaDB 데이터베이스
  db:
    image: mariadb:10.11
    container_name: lotto-db
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: lotto
      MYSQL_USER: ${DB_USERNAME:-root}
      MYSQL_PASSWORD: ${DB_PASSWORD}
    volumes:
      - db_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "healthcheck.sh", "--connect", "--innodb_initialized"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - lotto-network

  # Spring Boot 애플리케이션
  app:
    image: jongseungpark/lotto-web:latest
    container_name: lotto-app
    restart: always
    depends_on:
      db:
        condition: service_healthy
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_URL=jdbc:mariadb://db:3306/lotto?useUnicode=true&characterEncoding=UTF-8
      - DB_USERNAME=${DB_USERNAME:-root}
      - DB_PASSWORD=${DB_PASSWORD}
      - MAIL_USERNAME=${MAIL_USERNAME}
      - MAIL_PASSWORD=${MAIL_PASSWORD}
    volumes:
      - ./logs:/app/logs
    networks:
      - lotto-network

volumes:
  db_data:

networks:
  lotto-network:
    driver: bridge
```

저장: `Ctrl + O`, `Enter`, `Ctrl + X`

### 4.2 환경 변수 파일 생성

```bash
nano .env
```

다음 내용을 입력 (실제 값으로 변경):

```bash
# 데이터베이스 설정
DB_PASSWORD=your_secure_password_here_change_this
DB_USERNAME=root

# 이메일 설정 (Gmail 사용 시)
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password

# 애플리케이션 설정 (선택사항)
APP_URL=http://your-ec2-public-ip:8080
```

**보안 주의사항:**
- 강력한 비밀번호 사용 (최소 16자, 대소문자, 숫자, 특수문자 포함)
- Gmail 앱 비밀번호 사용 (일반 비밀번호가 아님)
- `.env` 파일 권한 설정: `chmod 600 .env`

저장 후 권한 설정:
```bash
chmod 600 .env
```

---

## 5단계: 애플리케이션 배포

### 5.1 Docker 이미지 Pull

```bash
# Docker Hub에서 최신 이미지 가져오기
docker pull jongseungpark/lotto-web:latest
```

### 5.2 애플리케이션 실행

```bash
# 백그라운드에서 실행
docker compose up -d

# 실행 확인
docker compose ps
```

### 5.3 로그 확인

```bash
# 애플리케이션 로그 확인
docker compose logs -f app

# 데이터베이스 로그 확인
docker compose logs -f db

# 모든 로그 확인
docker compose logs -f
```

---

## 6단계: 배포 확인

### 6.1 애플리케이션 접속 테스트

브라우저에서 접속:
- http://<EC2-PUBLIC-IP>:8080
- http://<EC2-PUBLIC-IP>:8080/swagger-ui.html

또는 curl로 테스트:
```bash
# 서버에서 직접 테스트
curl http://localhost:8080
curl http://localhost:8080/api/lotto/latest
```

### 6.2 컨테이너 상태 확인

```bash
# 실행 중인 컨테이너 확인
docker compose ps

# 컨테이너 리소스 사용량 확인
docker stats

# 네트워크 확인
docker network ls
docker network inspect lotto-web_lotto-network
```

---

## 7단계: Nginx 리버스 프록시 설정 (선택사항)

도메인을 사용하고 HTTPS를 적용하려면 Nginx를 설정합니다.

### 7.1 Nginx 설치

```bash
sudo apt install -y nginx
```

### 7.2 Nginx 설정 파일 생성

```bash
sudo nano /etc/nginx/sites-available/lotto-web
```

설정 내용:
```nginx
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;

    # HTTP를 HTTPS로 리다이렉트
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;

    # SSL 인증서 설정
    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    # SSL 보안 설정
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # 로그 설정
    access_log /var/log/nginx/lotto-access.log;
    error_log /var/log/nginx/lotto-error.log;

    # 프록시 설정
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 타임아웃 설정
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # 파일 크기 제한
    client_max_body_size 10M;
}
```

### 7.3 Let's Encrypt SSL 인증서 설치

```bash
# Certbot 설치
sudo apt install -y certbot python3-certbot-nginx

# SSL 인증서 발급
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# 자동 갱신 테스트
sudo certbot renew --dry-run
```

### 7.4 Nginx 활성화 및 재시작

```bash
# 심볼릭 링크 생성
sudo ln -s /etc/nginx/sites-available/lotto-web /etc/nginx/sites-enabled/

# 기본 설정 비활성화 (선택사항)
sudo rm /etc/nginx/sites-enabled/default

# 설정 테스트
sudo nginx -t

# Nginx 재시작
sudo systemctl restart nginx
sudo systemctl enable nginx
```

---

## 8단계: 방화벽 설정 (보안 그룹)

### 8.1 AWS 보안 그룹 확인

EC2 대시보드에서:
1. 인스턴스 선택 → 보안 탭
2. 보안 그룹 클릭
3. 인바운드 규칙 확인:
   - SSH (22): 내 IP만 허용 (프로덕션)
   - HTTP (80): 0.0.0.0/0
   - HTTPS (443): 0.0.0.0/0
   - Custom TCP (8080): 0.0.0.0/0 (테스트용, 프로덕션에서는 제거)

### 8.2 보안 그룹 규칙 수정

**프로덕션 환경 권장 설정:**
- SSH (22): 내 IP만 허용
- HTTP (80): 0.0.0.0/0 (Nginx용)
- HTTPS (443): 0.0.0.0/0 (Nginx용)
- Custom TCP (8080): 제거 (Nginx를 통해만 접근)

---

## 9단계: 애플리케이션 업데이트

### 9.1 새 버전 배포

```bash
# 프로젝트 디렉토리로 이동
cd ~/lotto-web

# 최신 이미지 pull
docker compose pull app

# 애플리케이션 재시작
docker compose up -d app

# 로그 확인
docker compose logs -f app
```

### 9.2 롤백 (이전 버전으로 되돌리기)

```bash
# 특정 태그의 이미지 사용
# docker-compose.yml에서 이미지 태그 변경 후
docker compose up -d app
```

---

## 10단계: 모니터링 및 로그 관리

### 10.1 로그 확인

```bash
# 실시간 로그
docker compose logs -f app

# 최근 100줄
docker compose logs --tail=100 app

# 특정 시간 이후
docker compose logs --since 30m app
```

### 10.2 리소스 모니터링

```bash
# 컨테이너 리소스 사용량
docker stats

# 디스크 사용량
df -h
docker system df

# 메모리 사용량
free -h
```

### 10.3 CloudWatch 연동 (선택사항)

AWS CloudWatch를 사용하여 모니터링:
1. CloudWatch Agent 설치
2. 로그 그룹 생성
3. 메트릭 수집 설정

---

## 11단계: 백업 설정

### 11.1 데이터베이스 백업

```bash
# 백업 스크립트 생성
nano ~/backup-lotto.sh
```

스크립트 내용:
```bash
#!/bin/bash
BACKUP_DIR="/home/ubuntu/backups"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

# 환경 변수 로드
source ~/lotto-web/.env

# 데이터베이스 백업
docker compose -f ~/lotto-web/docker-compose.yml exec -T db \
  mysqldump -u root -p${DB_PASSWORD} lotto > $BACKUP_DIR/db_$DATE.sql

# 7일 이상 된 백업 삭제
find $BACKUP_DIR -name "db_*.sql" -mtime +7 -delete

echo "Backup completed: db_$DATE.sql"
```

실행 권한 부여:
```bash
chmod +x ~/backup-lotto.sh
```

### 11.2 자동 백업 설정 (Cron)

```bash
# Crontab 편집
crontab -e

# 매일 새벽 2시에 백업 실행
0 2 * * * /home/ubuntu/backup-lotto.sh >> /home/ubuntu/backup.log 2>&1
```

### 11.3 S3에 백업 업로드 (선택사항)

```bash
# AWS CLI 설치
sudo apt install -y awscli

# AWS 자격 증명 설정
aws configure

# 백업 스크립트 수정하여 S3 업로드 추가
aws s3 cp $BACKUP_DIR/db_$DATE.sql s3://your-backup-bucket/lotto/
```

---

## 문제 해결

### 애플리케이션이 시작되지 않는 경우

```bash
# 로그 확인
docker compose logs app

# 컨테이너 상태 확인
docker compose ps

# 컨테이너 재시작
docker compose restart app

# 컨테이너 재생성
docker compose up -d --force-recreate app
```

### 데이터베이스 연결 오류

```bash
# 데이터베이스 상태 확인
docker compose ps db

# 데이터베이스 로그 확인
docker compose logs db

# 네트워크 확인
docker network inspect lotto-web_lotto-network

# 데이터베이스 재시작
docker compose restart db
```

### 포트 충돌

```bash
# 포트 사용 확인
sudo netstat -tulpn | grep 8080

# 다른 포트 사용 (docker-compose.yml 수정)
ports:
  - "8081:8080"
```

### 메모리 부족

```bash
# 스왑 메모리 추가
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# 영구적으로 활성화
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

---

## 비용 최적화

### 프리 티어 활용

- t2.micro 인스턴스: 월 750시간 무료
- EBS 스토리지: 30GB 무료
- 데이터 전송: 월 15GB 무료

### 비용 절감 팁

1. **인스턴스 크기 최적화**: 실제 사용량에 맞게 조정
2. **예약 인스턴스**: 장기 사용 시 예약 인스턴스 구매
3. **스팟 인스턴스**: 개발/테스트 환경에서 사용
4. **불필요한 리소스 삭제**: 사용하지 않는 인스턴스 종료

---

## 보안 체크리스트

- [ ] 강력한 데이터베이스 비밀번호 설정
- [ ] .env 파일 권한 설정 (chmod 600)
- [ ] SSH 키 파일 안전하게 보관
- [ ] 보안 그룹 규칙 최소화 (필요한 포트만 열기)
- [ ] 정기적인 시스템 업데이트
- [ ] HTTPS 설정 (프로덕션)
- [ ] 로그 모니터링 설정
- [ ] 백업 정책 수립
- [ ] AWS IAM 사용자 권한 최소화

---

## 다음 단계

배포가 완료되면:

1. **모니터링 설정**: CloudWatch 또는 Prometheus + Grafana
2. **로깅 시스템**: ELK Stack 또는 CloudWatch Logs
3. **자동 스케일링**: 트래픽에 따라 인스턴스 자동 확장
4. **로드 밸런서**: 여러 인스턴스 간 트래픽 분산
5. **CDN 설정**: CloudFront를 사용한 정적 리소스 캐싱

---

이 가이드를 따라하면 AWS EC2에 성공적으로 배포할 수 있습니다!

