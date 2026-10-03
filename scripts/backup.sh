#!/usr/bin/env bash
# ============================================================
# Backup script for website-tattoos
#
# Что делает:
#   1. Дамп БД через mysqldump
#   2. Архив изображений (реальный путь volume через docker inspect)
#   3. Копия docker-compose.yml и .env.example
#   4. sha256sum для проверки целостности
#   5. Ротация: хранить N последних бэкапов
#
# Запуск:
#   ./scripts/backup.sh
# или с указанием корня бэкапов:
#   BACKUP_ROOT=/mnt/backups ./scripts/backup.sh
# ============================================================

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_DIR"

BACKUP_ROOT="${BACKUP_ROOT:-$HOME/backups/website-tattoos}"
KEEP_LAST="${KEEP_LAST:-7}"

DB_CONTAINER="${DB_CONTAINER:-db}"
SERVICE_CONTAINER="${SERVICE_CONTAINER:-service}"

# --- читаем .env ---
if [[ ! -f .env ]]; then
    echo "ERROR: .env not found in $PROJECT_DIR" >&2
    exit 1
fi
set -a
# shellcheck disable=SC1091
source .env
set +a

: "${MYSQL_DATABASE:?MYSQL_DATABASE not set in .env}"
: "${MYSQL_USER:?MYSQL_USER not set in .env}"
: "${MYSQL_PASSWORD:?MYSQL_PASSWORD not set in .env}"

# --- определяем путь картинок через docker inspect ---
IMAGES_HOST_DIR=""
if docker inspect "$SERVICE_CONTAINER" >/dev/null 2>&1; then
    IMAGES_HOST_DIR=$(docker inspect "$SERVICE_CONTAINER" \
        --format '{{range .Mounts}}{{if eq .Destination "/app/img/images"}}{{.Source}}{{end}}{{end}}')
fi
IMAGES_HOST_DIR="${IMAGES_HOST_DIR:-$HOME/data/images}"

# --- создаём папку бэкапа ---
DATE="$(date +%F_%H-%M-%S)"
DEST="$BACKUP_ROOT/$DATE"
mkdir -p "$DEST"

echo "==> Backup started: $DATE"
echo "    project:    $PROJECT_DIR"
echo "    backup to:  $DEST"
echo "    db:         $MYSQL_DATABASE ($DB_CONTAINER)"
echo "    images:     $IMAGES_HOST_DIR"

# --- 1. Дамп БД ---
echo "==> [1/5] Dumping database..."
docker exec "$DB_CONTAINER" \
    mysqldump --single-transaction --quick --routines --triggers \
    -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" \
    | gzip > "$DEST/db.sql.gz"
echo "    -> $(du -h "$DEST/db.sql.gz" | cut -f1)"

# --- 2. Архив картинок ---
echo "==> [2/5] Archiving images..."
if [[ -d "$IMAGES_HOST_DIR" ]]; then
    tar -czf "$DEST/images.tar.gz" -C "$(dirname "$IMAGES_HOST_DIR")" "$(basename "$IMAGES_HOST_DIR")"
    echo "    -> $(du -h "$DEST/images.tar.gz" | cut -f1)"
else
    echo "    WARN: images dir not found: $IMAGES_HOST_DIR" >&2
fi

# --- 3. Конфиги ---
echo "==> [3/5] Copying configs..."
cp docker-compose.yml "$DEST/"
[[ -f .env.example ]] && cp .env.example "$DEST/"
echo "    -> docker-compose.yml, .env.example"

# --- 4. sha256 ---
echo "==> [4/5] Computing checksums..."
(
    cd "$DEST"
    sha256sum db.sql.gz > SHA256SUMS
    [[ -f images.tar.gz ]] && sha256sum images.tar.gz >> SHA256SUMS
    sha256sum docker-compose.yml >> SHA256SUMS
    [[ -f .env.example ]] && sha256sum .env.example >> SHA256SUMS
)
echo "    -> SHA256SUMS"

# --- 5. Ротация ---
echo "==> [5/5] Rotating old backups (keep last $KEEP_LAST)..."
if [[ -d "$BACKUP_ROOT" ]]; then
    ls -1dt "$BACKUP_ROOT"/*/ 2>/dev/null | tail -n +$((KEEP_LAST + 1)) | while read -r old; do
        echo "    removing: $old"
        rm -rf "$old"
    done
fi

echo ""
echo "==> Backup complete: $DEST"
echo ""
echo "Contents:"
ls -lh "$DEST"
