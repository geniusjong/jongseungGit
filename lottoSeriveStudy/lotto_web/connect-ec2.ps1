# EC2 인스턴스 접속 스크립트 (PowerShell)
# 사용법: .\connect-ec2.ps1 <EC2-PUBLIC-IP>

param(
    [Parameter(Mandatory=$true)]
    [string]$Ec2Ip
)

# 키 파일 경로 (수정 필요)
$KeyFile = "$env:USERPROFILE\.ssh\lotto-web-key.pem"

# 키 파일 존재 확인
if (-not (Test-Path $KeyFile)) {
    Write-Host "오류: 키 파일을 찾을 수 없습니다: $KeyFile" -ForegroundColor Red
    Write-Host "키 파일을 $env:USERPROFILE\.ssh\ 폴더에 저장하세요." -ForegroundColor Yellow
    exit 1
}

# SSH 접속
Write-Host "EC2 인스턴스에 접속합니다: $Ec2Ip" -ForegroundColor Green
ssh -i $KeyFile ubuntu@$Ec2Ip

