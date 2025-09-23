#!/usr/bin/env bash
# ------------------------------------------------------------
# Fliqo 로컬 실행 스크립트 (Git Bash / macOS 전용)
# - Gateway(8080) / Member API(8081) / Core API(8082)를 순서대로 백그라운드 실행
# - 프로필(application-<profile>.yml) 지정 가능 (기본: local)
# - 각 모듈 로그는 ./dev-script/logs/<모듈명>.log 로 분리 저장
# - 기존 포트 점유 프로세스가 있으면 강제 종료(옵션)
# - 빌드 수행 (SKIP_BUILD=1 로 건너뛰기 가능) -> 코드를 수정하지 않았을 때 빠르게 재실행하는 용도로 사용
# ------------------------------------------------------------

set -euo pipefail

# ====== 설정(필요 시 수정) ======
PROFILE="${1:-local}"            # 사용법: ./run-local.sh [profile]  (기본: local)
GRADLE="./gradlew"               # Gradle 실행 파일
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$ROOT_DIR/logs"
KILL_BUSY_PORTS="${KILL_BUSY_PORTS:-1}"  # 1: 포트점유 프로세스 자동 종료, 0: 종료 안 함

# 모듈 실행 순서(의존 관계 고려: core → member → gateway)
declare -A PORTS=(
  [fliqo-core-api]=8082
  [fliqo-member-api]=8081
  [fliqo-gateway]=8080
)

# ====== 유틸 함수 ======
exists() { command -v "$1" >/dev/null 2>&1; }

kill_if_port_busy() {
  # 포트가 이미 점유되어 있으면 해당 PID를 찾아 종료
  local port="$1"
  [[ "$KILL_BUSY_PORTS" != "1" ]] && return 0

  # macOS: lsof 사용 / Git Bash(Windows): netstat + taskkill 사용
  if [[ "$OSTYPE" == "darwin"* ]]; then
    if exists lsof; then
      local pid
      pid=$(lsof -ti tcp:"$port" || true)
      if [[ -n "${pid:-}" ]]; then
        echo "포트 ${port} 사용 중 PID(${pid}) 종료"
        kill -9 $pid || true
      fi
    fi
  else
    # Git Bash (msys/cygwin)
    # LISTEN 상태의 PID를 찾아 강제 종료
    if exists netstat; then
      # netstat 출력에서 포트 매칭 라인의 마지막 열이 PID
      local pid
      pid=$(netstat -ano 2>/dev/null | awk -v p=":$port" '$0 ~ p && $0 ~ /LISTEN/ {print $NF}' | head -n1)
      if [[ -n "${pid:-}" ]]; then
        echo "포트 ${port} 사용 중 PID(${pid}) 종료"
        taskkill //F //PID "$pid" >/dev/null 2>&1 || true
      fi
    fi
  fi
}

start_module() {
  # 모듈을 백그라운드로 실행하고 PID/로그 파일을 남긴다
  local module="$1"
  local port="$2"
  local log_file="$LOG_DIR/$module.log"
  local pid_file="$LOG_DIR/$module.pid"

  echo "🩷 ${module} (port ${port}, profile=${PROFILE}) 시작"

  kill_if_port_busy "$port"

  # nohup으로 백그라운드 실행, 로그 파일 분리 저장
  nohup "$GRADLE" ":$module:bootRun" \
    --args="--spring.profiles.active=$PROFILE" \
    >"$log_file" 2>&1 &

  # PID 기록
  echo $! > "$pid_file"
  echo "   ↳ 로그: $log_file / PID: $(cat "$pid_file")"
}

# ====== 준비 작업 ======
mkdir -p "$LOG_DIR"

if [[ "${SKIP_BUILD:-0}" != "1" ]]; then
  echo "빌드 실행 중… (테스트 스킵: -x test)"
  "$GRADLE" clean build -x test
else
  echo "빌드 스킵(SKIP_BUILD=1)"
fi

# ====== 모듈 실행(core → member → gateway) ======
start_module "fliqo-core-api"   "${PORTS[fliqo-core-api]}"
start_module "fliqo-member-api" "${PORTS[fliqo-member-api]}"
start_module "fliqo-gateway"    "${PORTS[fliqo-gateway]}"

echo ""
echo "모두 백그라운드 실행 완료"
echo "로그 디렉토리: $LOG_DIR"
echo "💚 종료하는 쉘 실행: ./dev-script/stop-local.sh"
echo ""
echo "※ 참고"
echo "- 프로필 변경: ./dev-script/run-local.sh dev"
echo "- 빌드 생략(코드 수정하지 않았을 때 빠르게 재실행):   SKIP_BUILD=1 ./dev-script/run-local.sh"
echo "- 포트 점유 종료 끄기: KILL_BUSY_PORTS=0 ./dev-script/run-local.sh"
