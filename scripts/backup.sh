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
#   6. Offsite: загрузка дампа и архива на Яндекс.Диск (если задан YANDEX_DISK_TOKEN)
#
# Запуск:
#   ./scripts/backup.sh
# или с указанием корня бэкапов:
#   BACKUP_ROOT=/mnt/backups ./scripts/backup.sh
#
# Автоматизация (cron на сервере, ежедневно в 3:00):
#   0 3 * * * cd /home/server/website-tattoos && ./scripts/backup.sh >> /var/log/website-tattoos-backup.log 2>&1
#
# Переменные для Яндекс.Диска (в .env):
#   YANDEX_DISK_TOKEN - OAuth-токен приложения с правами cloud_api:disk.read/write
#   YANDEX_DISK_DIR   - папка на Диске, по умолчанию /tattoo-backups
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
echo "==> [1/6] Dumping database..."
docker exec "$DB_CONTAINER" \
    mysqldump --single-transaction --quick --routines --triggers --no-tablespaces \
    -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" \
    | gzip > "$DEST/db.sql.gz"
echo "    -> $(du -h "$DEST/db.sql.gz" | cut -f1)"

# --- 2. Архив картинок ---
echo "==> [2/6] Archiving images..."
if [[ -d "$IMAGES_HOST_DIR" ]]; then
    tar -czf "$DEST/images.tar.gz" -C "$(dirname "$IMAGES_HOST_DIR")" "$(basename "$IMAGES_HOST_DIR")"
    echo "    -> $(du -h "$DEST/images.tar.gz" | cut -f1)"
else
    echo "    WARN: images dir not found: $IMAGES_HOST_DIR" >&2
fi

# --- 3. Конфиги ---
echo "==> [3/6] Copying configs..."
cp docker-compose.yml "$DEST/"
[[ -f .env.example ]] && cp .env.example "$DEST/"
echo "    -> docker-compose.yml, .env.example"

# --- 4. sha256 ---
echo "==> [4/6] Computing checksums..."
(
    cd "$DEST"
    sha256sum db.sql.gz > SHA256SUMS
    [[ -f images.tar.gz ]] && sha256sum images.tar.gz >> SHA256SUMS
    sha256sum docker-compose.yml >> SHA256SUMS
    [[ -f .env.example ]] && sha256sum .env.example >> SHA256SUMS
)
echo "    -> SHA256SUMS"

# --- 5. Ротация ---
echo "==> [5/6] Rotating old backups (keep last $KEEP_LAST)..."
if [[ -d "$BACKUP_ROOT" ]]; then
    ls -1dt "$BACKUP_ROOT"/*/ 2>/dev/null | tail -n +$((KEEP_LAST + 1)) | while read -r old; do
        echo "    removing: $old"
        rm -rf "$old"
    done
fi

# --- 6. Загрузка на Яндекс.Диск (offsite) ---
YANDEX_API="${YANDEX_API_BASE:-https://cloud-api.yandex.net/v1/disk}"

ensure_disk_dir() {
    local dir="$1"
    # 409 (папка уже есть) — нормальная ситуация, поэтому ошибку не пробрасываем
    curl -sS -o /dev/null -X PUT -G \
        --data-urlencode "path=$dir" \
        -H "Authorization: OAuth ${YANDEX_DISK_TOKEN}" \
        "$YANDEX_API/resources" || true
}

upload_file_to_disk() {
    local local_file="$1" remote_path="$2"
    local response code href

    response="$(curl -sS -G --max-time 60 \
        --data-urlencode "path=$remote_path" \
        --data-urlencode "overwrite=true" \
        -H "Authorization: OAuth ${YANDEX_DISK_TOKEN}" \
        -w $'\n%{http_code}' \
        "$YANDEX_API/resources/upload")" || return 1

    code="${response##*$'\n'}"
    response="${response%$'\n'*}"

    if [[ "$code" != "200" ]]; then
        echo "    ERROR: URL загрузки не получен (HTTP $code): $response" >&2
        return 1
    fi

    href="$(printf '%s' "$response" \
        | grep -o '"href"[[:space:]]*:[[:space:]]*"[^"]*"' \
        | head -n1 | cut -d'"' -f4 | sed 's#\\/#/#g' || true)"

    if [[ -z "$href" ]]; then
        echo "    ERROR: Яндекс.Диск не вернул href" >&2
        return 1
    fi

    code="$(curl -sS -o /dev/null -w '%{http_code}' --max-time 600 \
        -T "$local_file" -H "Content-Type: application/octet-stream" "$href")" || return 1

    if [[ "$code" != 2* ]]; then
        echo "    ERROR: загрузка вернула HTTP $code" >&2
        return 1
    fi
}

UPLOAD_FAILED=0
if [[ -n "${YANDEX_DISK_TOKEN:-}" ]]; then
    DISK_DIR="${YANDEX_DISK_DIR:-/tattoo-backups}"
    echo "==> [6/6] Uploading to Yandex.Disk (${DISK_DIR})..."
    ensure_disk_dir "$DISK_DIR"

    for file in "$DEST"/*.gz; do
        [[ -f "$file" ]] || continue
        remote_path="${DISK_DIR%/}/${DATE}_$(basename "$file")"
        if upload_file_to_disk "$file" "$remote_path"; then
            echo "    -> $remote_path"
        else
            echo "    WARN: не удалось загрузить $(basename "$file")" >&2
            UPLOAD_FAILED=1
        fi
    done
else
    echo "==> [6/6] Yandex.Disk upload skipped (YANDEX_DISK_TOKEN is not set)"
fi

echo ""
echo "==> Backup complete: $DEST"
echo ""
echo "Contents:"
ls -lh "$DEST"

if [[ "$UPLOAD_FAILED" -ne 0 ]]; then
    echo ""
    echo "==> WARNING: локальный бэкап создан, но offsite-копия на Яндекс.Диск не загружена" >&2
    exit 1
fi
