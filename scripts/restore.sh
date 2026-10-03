#!/usr/bin/env bash
# ============================================================
# Restore script for website-tattoos
#
# Восстанавливает БД и изображения из бэкапа.
# ВНИМАНИЕ: перезаписывает текущие данные!
#
# Запуск:
#   ./scripts/restore.sh ~/backups/website-tattoos/2026-10-03_18-00-00
# ============================================================

set -euo pipefail

BACKUP_DIR="${1:?usage: restore.sh <backup_dir>}"
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_DIR"

if [[ ! -d "$BACKUP_DIR" ]]; then
    echo "ERROR: backup dir not found: $BACKUP_DIR" >&2
    exit 1
fi

# --- читаем .env ---
if [[ ! -f .env ]]; then
    echo "ERROR: .env not found in $PROJECT_DIR" >&2
    exit 1
fi
set -a
# shellcheck disable=SC1091
source .env
set +a

: "${MYSQL_DATABASE:?}"
: "${MYSQL_USER:?}"
: "${MYSQL_PASSWORD:?}"

DB_CONTAINER="${DB_CONTAINER:-db}"
SERVICE_CONTAINER="${SERVICE_CONTAINER:-service}"

# --- определяем путь картинок ---
IMAGES_HOST_DIR=""
if docker inspect "$SERVICE_CONTAINER" >/dev/null 2>&1; then
    IMAGES_HOST_DIR=$(docker inspect "$SERVICE_CONTAINER" \
        --format '{{range .Mounts}}{{if eq .Destination "/app/img/images"}}{{.Source}}{{end}}{{end}}')
fi
IMAGES_HOST_DIR="${IMAGES_HOST_DIR:-$HOME/data/images}"

echo "============================================"
echo "  RESTORE — это перезапишет текущие данные!"
echo "============================================"
echo "  backup:  $BACKUP_DIR"
echo "  db:      $MYSQL_DATABASE ($DB_CONTAINER)"
echo "  images:  $IMAGES_HOST_DIR"
echo ""
read -rp "Введите 'yes' для продолжения: " ans
[[ "$ans" == "yes" ]] || { echo "Отменено."; exit 0; }

# --- проверяем контрольные суммы ---
if [[ -f "$BACKUP_DIR/SHA256SUMS" ]]; then
    echo "==> Проверка контрольных сумм..."
    ( cd "$BACKUP_DIR" && sha256sum -c SHA256SUMS ) || {
        echo "ERROR: контрольные суммы не совпадают!" >&2
        exit 1
    }
fi

# --- 1. БД ---
echo "==> [1/2] Восстановление БД..."
if [[ -f "$BACKUP_DIR/db.sql.gz" ]]; then
    zcat "$BACKUP_DIR/db.sql.gz" | docker exec -i "$DB_CONTAINER" \
        mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"
    echo "    -> OK"
else
    echo "    WARN: db.sql.gz не найден"
fi

# --- 2. Картинки ---
echo "==> [2/2] Восстановление картинок..."
if [[ -f "$BACKUP_DIR/images.tar.gz" ]]; then
    if [[ ! -w "$IMAGES_HOST_DIR" ]]; then
        echo "    WARN: нет прав на запись в $IMAGES_HOST_DIR"
        echo "    Запустите restore.sh с sudo, или распакуйте вручную:"
        echo "      sudo tar -xzf $BACKUP_DIR/images.tar.gz -C $(dirname "$IMAGES_HOST_DIR")"
    else
        tar -xzf "$BACKUP_DIR/images.tar.gz" -C "$(dirname "$IMAGES_HOST_DIR")"
        echo "    -> OK"
    fi
else
    echo "    WARN: images.tar.gz не найден"
fi

echo ""
echo "==> Восстановление завершено."
