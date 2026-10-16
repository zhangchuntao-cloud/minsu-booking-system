#!/bin/bash
# ============================================================================
#  启动本项目专用的 MySQL 实例（端口 3307）
#
#  说明：本项目的数据库使用独立实例，与本机原有的 3306 实例互不影响。
#       该实例通过 mysqld 直接启动，未注册为系统服务，
#       因此每次重启电脑后需要手动执行本脚本。
#
#  用法：bash 启动数据库.sh
# ============================================================================

set -e

MISU_HOME="$HOME/minsu-mysql"
SOCK="$MISU_HOME/mysql.sock"
PORT=3307

# 已在运行则直接退出
if mysqladmin --socket="$SOCK" -u root ping >/dev/null 2>&1; then
    echo "MySQL 实例已在运行（端口 $PORT）"
    exit 0
fi

if [ ! -d "$MISU_HOME/data" ]; then
    echo "错误：未找到数据目录 $MISU_HOME/data"
    echo "请先执行建库脚本初始化数据库："
    echo "  mysql -u root -p --default-character-set=utf8mb4 < src/main/resources/db/schema.sql"
    echo "  mysql -u root -p --default-character-set=utf8mb4 < src/main/resources/db/data.sql"
    exit 1
fi

echo "正在启动 MySQL 实例（端口 $PORT）..."
nohup mysqld \
    --datadir="$MISU_HOME/data" \
    --port=$PORT \
    --socket="$SOCK" \
    --mysqlx=OFF \
    --pid-file="$MISU_HOME/mysqld.pid" \
    --log-error="$MISU_HOME/error.log" \
    >/dev/null 2>&1 &

# 等待就绪，最多 30 秒
for i in $(seq 1 30); do
    if mysqladmin --socket="$SOCK" -u root ping >/dev/null 2>&1; then
        echo "启动成功，MySQL 已监听端口 $PORT"
        echo "现在可以启动应用：mvn spring-boot:run"
        exit 0
    fi
    sleep 1
done

echo "启动超时，请查看错误日志：$MISU_HOME/error.log"
exit 1
