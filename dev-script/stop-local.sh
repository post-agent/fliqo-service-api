#!/usr/bin/env bash
# ------------------------------------------------------------
# Fliqo 로컬 종료 스크립트 (Git Bash / macOS 전용)
# - run-local.sh가 남긴 ./dev-script/logs/*.pid 파일을 읽어 프로세스 종료
# - 안전하게 종료 후 PID 파일 정리
# ------------------------------------------------------------

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$SCRIPT_DIR/logs"

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

echo "모든 모듈 종료 완료"
echo "💚 로그 디렉토리: $LOG_DIR"
