# Git 히스토리에서 민감한 정보 제거 가이드

GitGuardian이 Git 히스토리에서 SMTP 자격 증명을 감지했습니다.
Git 히스토리에서 민감한 정보를 완전히 제거해야 합니다.

## 현재 상황

- `application.properties` 파일에 하드코딩된 이메일 비밀번호가 Git 히스토리에 포함됨
- 파일은 수정했지만, Git 히스토리에는 여전히 남아있음

## 해결 방법

### 방법 1: git filter-branch 사용 (권장)

```bash
# Git 히스토리에서 비밀번호 제거
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch src/main/resources/application.properties" \
  --prune-empty --tag-name-filter cat -- --all

# 강제 푸시 (주의: 팀원들과 협의 필요)
git push origin --force --all
git push origin --force --tags
```

### 방법 2: BFG Repo-Cleaner 사용 (더 빠름)

1. BFG 다운로드: https://rtyley.github.io/bfg-repo-cleaner/
2. 실행:
```bash
# 비밀번호가 포함된 파일 제거
java -jar bfg.jar --delete-files application.properties
git reflog expire --expire=now --all
git gc --prune=now --aggressive
```

### 방법 3: 새 저장소로 마이그레이션 (가장 안전)

1. 새 저장소 생성
2. 현재 파일만 복사 (히스토리 제외)
3. 새 저장소에 푸시

## 중요: 비밀번호 변경

**Git 히스토리에 노출된 비밀번호는 이미 유출되었으므로 반드시 변경해야 합니다!**

1. Gmail 앱 비밀번호 재생성:
   - https://myaccount.google.com/apppasswords
   - 기존 비밀번호 삭제
   - 새 앱 비밀번호 생성

2. GitHub Secrets 업데이트:
   - Settings → Secrets → MAIL_PASSWORD 업데이트

3. .env 파일 업데이트:
   - 로컬 .env 파일의 MAIL_PASSWORD 업데이트

## 현재 파일 수정 완료

`application.properties` 파일은 이미 환경 변수를 사용하도록 수정되었습니다:
- `spring.mail.username=${MAIL_USERNAME:itoweb9@gmail.com}`
- `spring.mail.password=${MAIL_PASSWORD}`

## 주의사항

?? **Force Push는 팀원들과 반드시 협의해야 합니다!**
- Force push는 Git 히스토리를 재작성합니다
- 다른 개발자들이 pull을 받으면 충돌이 발생할 수 있습니다
- 팀 전체가 새로 clone해야 할 수 있습니다

## 권장 순서

1. ? 현재 파일 수정 완료 (환경 변수 사용)
2. ?? Gmail 앱 비밀번호 재생성 (필수!)
3. ?? GitHub Secrets 업데이트
4. ?? Git 히스토리 정리 (팀 협의 후)
5. ? 새 비밀번호로 테스트

