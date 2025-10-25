#!/usr/bin/env bash
# ------------------------------------------------------------
# Fliqo 로컬 실행 스크립트 (Git Bash / macOS 공통)
# - Gateway(8080) / Member API(8081) / Core API(8082) 순서 실행
# - profile 인자 (기본: local), SKIP_BUILD=1 로 빌드 생략 가능
# - 포트 점유 프로세스 자동 종료(KILL_BUSY_PORTS=1)
# - 로그/ PID 분리 저장
# ------------------------------------------------------------

set -euo pipefail

# ====== 설정 ======
PROFILE="${1:-local}"           # 사용법: ./run-local.sh [profile]
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"   # gradlew가 루트에 있다고 가정
GRADLE="$REPO_ROOT/gradlew"
LOG_DIR="$SCRIPT_DIR/logs"
KILL_BUSY_PORTS="${KILL_BUSY_PORTS:-1}"     # 1: 포트점유 프로세스 종료

# ====== 환경변수 설정 ======
# .env 파일이 있으면 로드
if [[ -f "$REPO_ROOT/.env" ]]; then
  echo "📄 .env 파일 로드 중..."
  # .env 파일의 각 줄을 읽어서 환경변수로 설정
  while IFS= read -r line || [[ -n "$line" ]]; do
    # 주석이나 빈 줄은 건너뛰기
    if [[ "$line" =~ ^[[:space:]]*# ]] || [[ -z "${line// }" ]]; then
      continue
    fi
    # 환경변수 설정
    if [[ "$line" =~ ^[A-Za-z_][A-Za-z0-9_]*= ]]; then
      export "$line"
    fi
  done < "$REPO_ROOT/.env"
fi

# 필수 환경변수 체크
MISSING_VARS=()

if [[ -z "${JWT_SECRET:-}" ]]; then
  MISSING_VARS+=("JWT_SECRET")
fi

if [[ -z "${DB_URL:-}" ]]; then
  MISSING_VARS+=("DB_URL")
fi

if [[ -z "${DB_USERNAME:-}" ]]; then
  MISSING_VARS+=("DB_USERNAME")
fi

if [[ -z "${DB_PASSWORD:-}" ]]; then
  MISSING_VARS+=("DB_PASSWORD")
fi

if [[ ${#MISSING_VARS[@]} -gt 0 ]]; then
  echo "⚠️  다음 환경변수들이 설정되지 않았습니다:"
  printf "   - %s\n" "${MISSING_VARS[@]}"
  echo ""
  echo "   다음 중 하나를 선택하세요:"
  echo "   1. .env 파일에 환경변수 추가:"
  echo "      echo \"JWT_SECRET=your-secret\" >> .env"
  echo "      echo \"DB_URL=jdbc:mysql://localhost:3306/fliqo?useSSL=false\" >> .env"
  echo "      echo \"DB_USERNAME=root\" >> .env"
  echo "      echo \"DB_PASSWORD=password\" >> .env"
  echo ""
  echo "   2. 터미널에서 직접 export:"
  echo "      export JWT_SECRET=your-secret"
  echo "      export DB_URL=jdbc:mysql://localhost:3306/fliqo?useSSL=false"
  echo "      export DB_USERNAME=root"
  echo "      export DB_PASSWORD=password"

  exit 1
fi

# 환경변수 확인 로그
echo "🔧 환경변수 확인:"
echo "   JWT_SECRET: ${JWT_SECRET:0:10}..."
echo "   DB_URL: ${DB_URL:-'NOT_SET'}"
echo "   DB_USERNAME: ${DB_USERNAME:-'NOT_SET'}"
echo "   DB_PASSWORD: ${DB_PASSWORD:0:3}***"

# 모듈/포트 정의 (의존성 순서대로)
MODULES=("fliqo-common" "fliqo-core-api" "fliqo-member-api" "fliqo-gateway")
port_of() {
  case "$1" in
    fliqo-common) echo "0" ;;        # common은 라이브러리 모듈이므로 포트 없음
    fliqo-core-api) echo 8082 ;;
    fliqo-member-api) echo 8081 ;;
    fliqo-gateway) echo 8080 ;;
    *) echo "0" ;;
  esac
}

# ====== 유틸 ======
exists() { command -v "$1" >/dev/null 2>&1; }

kill_if_port_busy() {
  local port="$1"
  [[ "$KILL_BUSY_PORTS" != "1" ]] && return 0

  # macOS: lsof, Git Bash(Windows): netstat+taskkill
  if [[ "${OSTYPE:-}" == darwin* ]]; then
    if exists lsof; then
      local pid
      pid="$(lsof -ti tcp:"$port" || true)"
      if [[ -n "${pid:-}" ]]; then
        echo "포트 ${port} 사용 중 PID(${pid}) 종료"
        kill -9 $pid || true
      fi
    fi
  else
    # Git Bash(msys/cygwin)
    if exists netstat; then
      # LISTEN/ LISTENING 모두 매칭되도록 LISTEN 사용(서브스트링)
      local pid
      pid="$(netstat -ano 2>/dev/null | awk -v p=":$port" '$0 ~ p && $0 ~ /LISTEN/ {print $NF}' | head -n1)"
      if [[ -n "${pid:-}" ]]; then
        echo "포트 ${port} 사용 중 PID(${pid}) 종료"
        taskkill //F //PID "$pid" >/dev/null 2>&1 || true
      fi
    fi
  fi
}

start_module() {
  local module="$1"
  local port="$2"
  local log_file="$LOG_DIR/$module.log"
  local pid_file="$LOG_DIR/$module.pid"

  # fliqo-common은 라이브러리 모듈이므로 실행하지 않음
  if [[ "$module" == "fliqo-common" ]]; then
    echo "${module} (라이브러리 모듈) - 실행 생략"
    return 0
  fi

  echo "🩷 ${module} (port ${port}, profile=${PROFILE}) 시작"

  kill_if_port_busy "$port"

  # gradlew는 리포트 루트에서 실행
  ( cd "$REPO_ROOT" && \
    nohup env JWT_SECRET="$JWT_SECRET" DB_URL="$DB_URL" DB_USERNAME="$DB_USERNAME" DB_PASSWORD="$DB_PASSWORD" \
    "$GRADLE" ":$module:bootRun" --args="--spring.profiles.active=$PROFILE" \
    >"$log_file" 2>&1 & echo $! > "$pid_file" )

  echo "   ↳ 로그: $log_file / PID: $(cat "$pid_file")"
}

# ====== 준비 ======
mkdir -p "$LOG_DIR"

if [[ "${SKIP_BUILD:-0}" != "1" ]]; then
  echo "빌드 실행 중… (테스트 스킵: -x test)"
  ( cd "$REPO_ROOT" && "$GRADLE" clean build -x test )
else
  echo "빌드 스킵(SKIP_BUILD=1)"
fi

# ====== 모듈 실행: common → core → member → gateway ======
for m in "${MODULES[@]}"; do
  start_module "$m" "$(port_of "$m")"
  # 각 모듈 간 약간의 대기 시간 (의존성 모듈 준비 시간)
  if [[ "$m" != "fliqo-common" ]]; then
    sleep 2
  fi
done

echo ""
echo "모두 백그라운드 실행 완료"
echo "로그 디렉토리: $LOG_DIR"
echo "💚 종료하는 쉘 실행: ./dev-script/stop-local.sh"
echo ""
echo "※ 참고"
echo "- 프로필 변경: ./dev-script/run-local.sh dev"
echo "- 빌드 생략(코드 수정하지 않았을 때 빠르게 재실행):   SKIP_BUILD=1 ./dev-script/run-local.sh"
echo "- 포트 점유 종료 끄기: KILL_BUSY_PORTS=0 ./dev-script/run-local.sh"