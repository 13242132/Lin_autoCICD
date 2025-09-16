#!/bin/bash

# =============================================================================
# Spring Boot 应用部署脚本（增强版）
# 功能：自动停止服务 + 备份旧版本 + 部署新版本
# 适用于：CentOS 7/8/Stream
# 作者：你的名字
# 日期：$(date +%Y-%m-%d)
# =============================================================================

set -euo pipefail  # 严格模式

# ------------------ 配置变量 ------------------
APP_NAME="autocode-generator"
JAR_FILE="target/demo-0.0.1-SNAPSHOT.jar"
APP_USER="$USER"     # 使用当前用户（如 root）
APP_PORT=8200        # 应用端口

# ------------------ 检查 JAR 文件 ------------------
echo "🔍 正在检查 JAR 文件..."
if [[ ! -f "$JAR_FILE" ]]; then
    echo "❌ 错误：JAR 文件不存在！请先上传 $JAR_FILE"
    echo "上传命令示例："
    echo "  scp target/demo-0.0.1-SNAPSHOT.jar root@124.222.232.31:/opt/autoCICD/target/"
    exit 1
fi
echo "✅ JAR 文件已找到: $JAR_FILE"

# ------------------ 停止旧服务 ------------------
echo "🛑 正在停止旧服务..."
if systemctl is-active --quiet "$APP_NAME.service"; then
    systemctl stop "$APP_NAME.service"
    echo "✅ 旧服务已停止"
else
    echo "ℹ️ 服务未运行，跳过停止"
fi

# ------------------ 备份旧 JAR 文件 ------------------
echo "📦 正在备份旧 JAR 文件..."
BACKUP_DIR="/opt/autoCICD/backup"
JAR_DIR=$(dirname "$JAR_FILE")
JAR_NAME=$(basename "$JAR_FILE")
mkdir -p "$BACKUP_DIR"
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
BACKUP_PATH="$BACKUP_DIR/${JAR_NAME%.*}_backup_${TIMESTAMP}.jar"

if [[ -f "$JAR_DIR/$JAR_NAME" ]]; then
    cp "$JAR_DIR/$JAR_NAME" "$BACKUP_PATH"
    echo "✅ 旧版本已备份到: $BACKUP_PATH"
else
    echo "⚠️ 未找到旧 JAR 进行备份，跳过"
fi

# ------------------ 安装 Java 21 ------------------
echo "☕ 正在检查并安装 Java 21..."
if ! command -v java &> /dev/null; then
    echo "🔄 安装 OpenJDK 21..."
    yum install -y java-21-openjdk java-21-openjdk-devel
    if [[ $? -ne 0 ]]; then
        echo "❌ 安装 Java 21 失败！请检查网络或 yum 源。"
        exit 1
    fi
else
    CURRENT_VERSION=$(java -version 2>&1 | head -1 | awk '{print $3}' | tr -d '"')
    echo "✅ Java 已安装，版本: $CURRENT_VERSION"
fi

# ------------------ 创建日志目录 ------------------
echo "📁 创建日志目录..."
LOG_DIR="/var/log/$APP_NAME"
sudo mkdir -p "$LOG_DIR"
sudo chown "$APP_USER":"$APP_USER" "$LOG_DIR"
echo "日志将保存在: $LOG_DIR"

# ------------------ 创建 systemd 服务文件 ------------------
echo "⚙️ 正在创建 systemd 服务文件..."
SERVICE_FILE="/etc/systemd/system/$APP_NAME.service"

cat > "$SERVICE_FILE" << EOF
[Unit]
Description=Auto Code Generator Service
After=network.target

[Service]
Type=simple
User=$APP_USER
WorkingDirectory=/opt/autoCICD
ExecStart=/usr/bin/java -Dserver.port=$APP_PORT -Djava.security.egd=file:/dev/./urandom -jar $JAR_FILE
StandardOutput=append:/var/log/$APP_NAME/app.log
StandardError=append:/var/log/$APP_NAME/error.log
Restart=always
RestartSec=10
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
EOF

echo "✅ systemd 服务文件已创建: $SERVICE_FILE"

# ------------------ 重新加载 systemd 并启动服务 ------------------
echo "🔄 重新加载 systemd 配置..."
systemctl daemon-reload

echo "✅ 启用服务（开机自启）..."
systemctl enable "$APP_NAME.service"

echo "🚀 启动服务..."
systemctl start "$APP_NAME.service"

# ------------------ 配置防火墙 ------------------
echo "🔒 配置防火墙 (firewalld)..."
if systemctl is-active --quiet firewalld; then
    firewall-cmd --permanent --add-port=${APP_PORT}/tcp
    firewall-cmd --reload
    echo "✅ 防火墙已放行 $APP_PORT 端口"
else
    echo "⚠️  firewalld 未运行，跳过防火墙配置。请确保 $APP_PORT 端口已开放。"
fi

# ------------------ 输出部署信息 ------------------
echo ""
echo "=================================================="
echo "✅ 部署成功！"
echo "=================================================="
echo "🌐 服务地址: http://$(hostname -I | awk '{print $1}'):$APP_PORT"
echo "🔍 健康检查: http://$(hostname -I | awk '{print $1}'):$APP_PORT/actuator/health"
echo "📊 服务状态: sudo systemctl status $APP_NAME.service"
echo "📋 查看日志: tail -f /var/log/$APP_NAME/app.log"
echo "📋 实时日志: journalctl -u $APP_NAME.service -f"
echo "🛑 停止服务: sudo systemctl stop $APP_NAME.service"
echo "🔄 重启服务: sudo systemctl restart $APP_NAME.service"
echo "↩️  回滚命令（示例）:"
echo "      sudo systemctl stop $APP_NAME.service"
echo "      cp /opt/autoCICD/backup/demo-0.0.1-SNAPSHOT_backup_*.jar /opt/autoCICD/target/demo-0.0.1-SNAPSHOT.jar"
echo "      sudo systemctl start $APP_NAME.service"
echo "=================================================="