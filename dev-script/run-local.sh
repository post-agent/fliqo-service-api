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

# 모듈/포트 정의 (연관배열 없이 Bash 3.2 호환)
MODULES=("fliqo-core-api" "fliqo-member-api" "fliqo-gateway")
port_of() {
  case "$1" in
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

  echo "🩷 ${module} (port ${port}, profile=${PROFILE}) 시작"

  kill_if_port_busy "$port"

  # gradlew는 리포트 루트에서 실행
  ( cd "$REPO_ROOT" && \
    nohup "$GRADLE" ":$module:bootRun" --args="--spring.profiles.active=$PROFILE" \
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

# ====== 모듈 실행: core → member → gateway ======
for m in "${MODULES[@]}"; do
  start_module "$m" "$(port_of "$m")"
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