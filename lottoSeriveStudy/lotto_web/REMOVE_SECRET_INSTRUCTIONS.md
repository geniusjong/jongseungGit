# Git 히스토리에서 비밀번호 제거 방법

GitGuardian이 감지한 SMTP 비밀번호를 Git 히스토리에서 제거하는 방법입니다.

## 방법 1: Git Bash에서 직접 실행 (권장)

### 1단계: Git Bash 열기
- 프로젝트 루트 디렉토리(`C:\jongseungGit`)에서 우클릭
- "Git Bash Here" 선택

### 2단계: 다음 명령어 실행

```bash
# 경고 메시지 억제
export FILTER_BRANCH_SQUELCH_WARNING=1

# Git 히스토리에서 비밀번호 제거
git filter-branch --force --tree-filter '
if [ -f lottoSeriveStudy/lotto_web/src/main/resources/application.properties ]; then
  sed -i "s/vjjadehbenqrbsyg/[REMOVED]/g" lottoSeriveStudy/lotto_web/src/main/resources/application.properties
fi
' --prune-empty --tag-name-filter cat -- --all
```

### 3단계: 히스토리 정리

```bash
# 임시 백업 제거
rm -rf .git/refs/original/
git reflog expire --expire=now --all
git gc --prune=now --aggressive
```

### 4단계: 강제 푸시 (주의!)

```bash
# 모든 브랜치 강제 푸시
git push origin --force --all

# 모든 태그 강제 푸시
git push origin --force --tags
```

## 방법 2: BFG Repo-Cleaner 사용 (더 빠름)

1. BFG 다운로드: https://rtyley.github.io/bfg-repo-cleaner/
2. 비밀번호가 포함된 파일 제거:
```bash
java -jar bfg.jar --delete-files application.properties
git reflog expire --expire=now --all
git gc --prune=now --aggressive
git push origin --force --all
```

## ?? 중요 주의사항

1. **Force Push는 팀원들과 반드시 협의해야 합니다!**
   - Force push는 Git 히스토리를 재작성합니다
   - 다른 개발자들이 pull을 받으면 충돌이 발생할 수 있습니다
   - 팀 전체가 새로 clone해야 할 수 있습니다

2. **백업 필수**
   - 작업 전에 저장소를 백업하세요
   - `git clone --mirror`로 전체 백업 가능

3. **비밀번호는 이미 변경 완료**
   - Gmail 앱 비밀번호: `ovyp szjk xzwi bnlb`로 변경됨
   - GitHub Secrets 업데이트 완료
   - .env 파일 업데이트 완료

## 확인 방법

히스토리에서 비밀번호가 제거되었는지 확인:

```bash
git log --all --full-history -S "vjjadehbenqrbsyg"
```

결과가 없으면 성공입니다!

