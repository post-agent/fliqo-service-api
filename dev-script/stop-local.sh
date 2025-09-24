#!/usr/bin/env bash
# ------------------------------------------------------------
# Fliqo 로컬 종료 스크립트 (Git Bash / macOS 전용)
# - run-local.sh가 남긴 ./dev-script/logs/*.pid 파일을 읽어 프로세스 종료
# - 안전하게 종료 후 PID 파일 정리
# ------------------------------------------------------------

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$SCRIPT_DIR/logs"

# 포트/모듈 매핑 (run-local.sh와 동일 포트 사용)
MODULES=("fliqo-core-api" "fliqo-member-api" "fliqo-gateway")
port_of() {
  case "$1" in
    fliqo-core-api) echo 8082 ;;
    fliqo-member-api) echo 8081 ;;
    fliqo-gateway) echo 8080 ;;
    *) echo 0 ;;
  esac
}

exists() { command -v "$1" >/dev/null 2>&1; }

kill_by_port() {
  local port="$1"
  # macOS: lsof, Git Bash(Windows): netstat+taskkill
  if [[ "${OSTYPE:-}" == darwin* ]]; then
    if exists lsof; then
      local pid
      pid="$(lsof -ti tcp:"$port" || true)"
      if [[ -n "${pid:-}" ]]; then
        echo "   ↳ 포트 ${port} 사용 PID(${pid}) 강제 종료"
        kill -9 $pid >/dev/null 2>&1 || true
      fi
    fi
  else
    if exists netstat; then
      local pid
      pid="$(netstat -ano 2>/dev/null | awk -v p=":$port" '$0 ~ p && $0 ~ /LISTEN/ {print $NF}' | head -n1)"
      if [[ -n "${pid:-}" ]]; then
        echo "   ↳ 포트 ${port} 사용 PID(${pid}) 강제 종료(taskkill)"
        taskkill //F //PID "$pid" >/dev/null 2>&1 || true
      fi
    fi
  fi
}

if ! ls "$LOG_DIR"/*.pid >/dev/null 2>&1; then
  echo "종료할 PID 파일이 없습니다. ($LOG_DIR/*.pid)"
  exit 0
fi

for pid_file in "$LOG_DIR"/*.pid; do
  [[ -f "$pid_file" ]] || continue
  module="$(basename "$pid_file" .pid)"
  pid="$(cat "$pid_file" 2>/dev/null || true)"

  if [[ -n "${pid:-}" ]]; then
    echo "🔻 종료: ${module} (PID $pid)"
    # 정상 종료 시도
    kill "$pid" >/dev/null 2>&1 || true
    sleep 1
    if ps -p "$pid" >/dev/null 2>&1; then
      echo "   ↳ 강제 종료(SIGKILL)"
      kill -9 "$pid" >/dev/null 2>&1 || true
    fi
  fi

  rm -f "$pid_file"
done

# 추가 안전장치: 포트 기준으로 잔존 프로세스 종료(Windows 파일 잠김 방지)
for m in "${MODULES[@]}"; do
  port="$(port_of "$m")"
  [[ "$port" == "0" ]] && continue
  echo "😈 확인: ${m} (port ${port}) 잔존 프로세스 검사"
  kill_by_port "$port"
done

echo "모든 모듈 종료 완료"
echo "💚 로그 디렉토리: $LOG_DIR"
