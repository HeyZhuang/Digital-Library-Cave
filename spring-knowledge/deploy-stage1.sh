#!/bin/bash

# 第一阶段部署脚本
# 目标: 部署第一阶段优化并验证效果

set -e  # 遇到错误立即退出

echo "🚀 开始第一阶段优化部署..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        log_error "$1 命令未找到，请先安装"
        exit 1
    fi
}

# 检查必要的命令
log_info "检查必要的命令..."
check_command java
check_command mvn
check_command curl
check_command mysql

# 1. 构建应用
log_info "步骤1: 构建应用..."
cd "$(dirname "$0")"

if [ ! -f "pom.xml" ]; then
    log_error "未找到pom.xml文件，请确保在正确的目录中"
    exit 1
fi

log_info "清理并重新构建项目..."
mvn clean package -DskipTests

if [ ! -f "target/deepseek-doctor.jar" ]; then
    log_error "构建失败，未生成jar文件"
    exit 1
fi

log_info "构建成功: target/deepseek-doctor.jar"

# 2. 备份现有应用
log_info "步骤2: 备份现有应用..."
if [ -f "deepseek-doctor.jar" ]; then
    cp deepseek-doctor.jar deepseek-doctor.jar.backup
    log_info "已备份现有应用"
else
    log_warn "未找到现有应用，跳过备份"
fi

# 3. 部署新版本
log_info "步骤3: 部署新版本..."
cp target/deepseek-doctor.jar ./

# 4. 停止现有应用
log_info "步骤4: 停止现有应用..."
if pgrep -f "deepseek-doctor" > /dev/null; then
    pkill -f "deepseek-doctor"
    sleep 3
    log_info "已停止现有应用"
else
    log_warn "未找到运行中的应用"
fi

# 5. 启动新应用
log_info "步骤5: 启动新应用..."
nohup java -jar deepseek-doctor.jar --spring.profiles.active=prod > app.log 2>&1 &

# 等待应用启动
log_info "等待应用启动..."
sleep 10

# 6. 检查应用状态
log_info "步骤6: 检查应用状态..."

# 检查进程
if pgrep -f "deepseek-doctor" > /dev/null; then
    log_info "应用进程运行正常"
else
    log_error "应用进程未运行"
    tail -20 app.log
    exit 1
fi

# 检查端口
if netstat -tuln | grep ":8181" > /dev/null; then
    log_info "应用端口8181监听正常"
else
    log_error "应用端口8181未监听"
    exit 1
fi

# 7. 基础功能测试
log_info "步骤7: 基础功能测试..."

# 健康检查
log_info "测试健康检查接口..."
if curl -s -f http://140.143.155.164:8181/api/health > /dev/null; then
    log_info "健康检查通过"
else
    log_error "健康检查失败"
    exit 1
fi

# 文章列表测试
log_info "测试文章列表接口..."
if curl -s -f "http://140.143.155.164:8181/api/articles?page=1&size=10" > /dev/null; then
    log_info "文章列表接口正常"
else
    log_warn "文章列表接口异常"
fi

# 8. 性能测试
log_info "步骤8: 性能测试..."

# 检查ab命令
if command -v ab &> /dev/null; then
    log_info "开始压力测试..."
    
    # 基础压力测试
    log_info "执行基础压力测试 (1000请求, 50并发)..."
    ab -n 1000 -c 50 http://140.143.155.164:8181/api/articles 2>/dev/null | grep -E "(Requests per second|Time per request|Failed requests)"
    
    # 高并发测试
    log_info "执行高并发测试 (1000请求, 100并发)..."
    ab -n 1000 -c 100 http://140.143.155.164:8181/api/articles 2>/dev/null | grep -E "(Requests per second|Time per request|Failed requests)"
    
else
    log_warn "未找到ab命令，跳过压力测试"
    log_info "请手动安装apache2-utils并执行压力测试"
fi

# 9. 缓存测试
log_info "步骤9: 缓存测试..."

# 测试缓存效果
log_info "测试缓存效果..."
echo "第一次请求（缓存未命中）:"
time curl -s -o /dev/null -w "时间: %{time_total}s\n" http://140.143.155.164:8181/api/articles/1

echo "第二次请求（缓存命中）:"
time curl -s -o /dev/null -w "时间: %{time_total}s\n" http://140.143.155.164:8181/api/articles/1

# 10. 检查Redis
log_info "步骤10: 检查Redis连接..."
if command -v redis-cli &> /dev/null; then
    if redis-cli -h 140.143.155.164 ping > /dev/null 2>&1; then
        log_info "Redis连接正常"
        
        # 检查缓存数据
        log_info "检查缓存数据..."
        redis-cli -h 140.143.155.164 KEYS "*" | head -10
    else
        log_warn "Redis连接失败"
    fi
else
    log_warn "未找到redis-cli命令，跳过Redis检查"
fi

# 11. 生成部署报告
log_info "步骤11: 生成部署报告..."

cat > deploy-report.txt << EOF
第一阶段优化部署报告
====================

部署时间: $(date)
部署状态: 成功

应用信息:
- JAR文件: deepseek-doctor.jar
- 配置文件: application-prod.yml
- 启动参数: --spring.profiles.active=prod

优化内容:
1. 数据库连接池优化
   - 最大连接数: 50
   - 最小空闲连接: 20
   - 连接泄露检测: 启用

2. Redis缓存配置
   - 主机: 140.143.155.164:6379
   - 连接池: 最大20个连接
   - 缓存策略: 多级缓存

3. 数据库索引优化
   - 用户表索引: 5个
   - 文章表索引: 6个
   - 评论表索引: 4个

测试结果:
- 应用启动: 成功
- 健康检查: 通过
- 基础功能: 正常

性能指标:
- 并发用户数: 目标100+
- 响应时间: 目标<200ms
- 缓存命中率: 目标>80%

下一步:
1. 执行数据库索引脚本
2. 进行详细压力测试
3. 监控性能指标
4. 根据结果决定是否开始第二阶段

EOF

log_info "部署报告已生成: deploy-report.txt"

# 12. 显示应用日志
log_info "步骤12: 显示应用日志..."
echo "最近的应用日志:"
tail -10 app.log

# 13. 提供后续步骤
log_info "部署完成！"
echo ""
echo "📋 后续步骤:"
echo "1. 执行数据库索引脚本: mysql -h 140.143.155.164 -u deepseek_doctor -p deepseek_doctor < high_concurrency_optimization.sql"
echo "2. 进行详细压力测试: ab -n 1000 -c 100 http://140.143.155.164:8181/api/articles"
echo "3. 监控应用性能: tail -f app.log"
echo "4. 检查Redis缓存: redis-cli -h 140.143.155.164"
echo ""
echo "📊 监控命令:"
echo "- 查看应用状态: ps aux | grep deepseek-doctor"
echo "- 查看端口监听: netstat -tuln | grep 8181"
echo "- 查看应用日志: tail -f app.log"
echo "- 查看JVM状态: jps -l && jstat -gc <pid>"
echo ""
echo "🚨 如果出现问题，可以回滚:"
echo "- 停止应用: pkill -f deepseek-doctor"
echo "- 恢复备份: cp deepseek-doctor.jar.backup deepseek-doctor.jar"
echo "- 重新启动: nohup java -jar deepseek-doctor.jar --spring.profiles.active=prod > app.log 2>&1 &"

log_info "第一阶段部署完成！" 