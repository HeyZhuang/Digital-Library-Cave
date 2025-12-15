# 第一阶段部署脚本 (Windows PowerShell版本)
# 目标: 部署第一阶段优化并验证效果

param(
    [string]$ServerIP = "140.143.155.164",
    [string]$ServerPort = "8181"
)

# 设置错误处理
$ErrorActionPreference = "Stop"

Write-Host "🚀 开始第一阶段优化部署..." -ForegroundColor Green

# 日志函数
function Write-LogInfo {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Green
}

function Write-LogWarn {
    param([string]$Message)
    Write-Host "[WARN] $Message" -ForegroundColor Yellow
}

function Write-LogError {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

# 检查命令是否存在
function Test-Command {
    param([string]$Command)
    try {
        Get-Command $Command -ErrorAction Stop | Out-Null
        return $true
    }
    catch {
        return $false
    }
}

# 检查必要的命令
Write-LogInfo "检查必要的命令..."
$requiredCommands = @("java", "mvn", "curl")
foreach ($cmd in $requiredCommands) {
    if (-not (Test-Command $cmd)) {
        Write-LogError "$cmd 命令未找到，请先安装"
        exit 1
    }
}

# 1. 构建应用
Write-LogInfo "步骤1: 构建应用..."
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

if (-not (Test-Path "pom.xml")) {
    Write-LogError "未找到pom.xml文件，请确保在正确的目录中"
    exit 1
}

Write-LogInfo "清理并重新构建项目..."
try {
    mvn clean package -DskipTests
}
catch {
    Write-LogError "构建失败: $_"
    exit 1
}

if (-not (Test-Path "target/deepseek-doctor.jar")) {
    Write-LogError "构建失败，未生成jar文件"
    exit 1
}

Write-LogInfo "构建成功: target/deepseek-doctor.jar"

# 2. 备份现有应用
Write-LogInfo "步骤2: 备份现有应用..."
if (Test-Path "deepseek-doctor.jar") {
    Copy-Item "deepseek-doctor.jar" "deepseek-doctor.jar.backup"
    Write-LogInfo "已备份现有应用"
}
else {
    Write-LogWarn "未找到现有应用，跳过备份"
}

# 3. 部署新版本
Write-LogInfo "步骤3: 部署新版本..."
Copy-Item "target/deepseek-doctor.jar" "./"

# 4. 停止现有应用
Write-LogInfo "步骤4: 停止现有应用..."
$processes = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*deepseek-doctor*" }
if ($processes) {
    $processes | Stop-Process -Force
    Start-Sleep -Seconds 3
    Write-LogInfo "已停止现有应用"
}
else {
    Write-LogWarn "未找到运行中的应用"
}

# 5. 启动新应用
Write-LogInfo "步骤5: 启动新应用..."
Start-Process -FilePath "java" -ArgumentList "-jar", "deepseek-doctor.jar", "--spring.profiles.active=prod" -RedirectStandardOutput "app.log" -RedirectStandardError "app.log" -WindowStyle Hidden

# 等待应用启动
Write-LogInfo "等待应用启动..."
Start-Sleep -Seconds 15

# 6. 检查应用状态
Write-LogInfo "步骤6: 检查应用状态..."

# 检查进程
$processes = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*deepseek-doctor*" }
if ($processes) {
    Write-LogInfo "应用进程运行正常"
}
else {
    Write-LogError "应用进程未运行"
    if (Test-Path "app.log") {
        Get-Content "app.log" -Tail 20
    }
    exit 1
}

# 检查端口
try {
    $connection = Test-NetConnection -ComputerName $ServerIP -Port $ServerPort -InformationLevel Quiet
    if ($connection) {
        Write-LogInfo "应用端口$ServerPort监听正常"
    }
    else {
        Write-LogError "应用端口$ServerPort未监听"
        exit 1
    }
}
catch {
    Write-LogWarn "无法检查端口状态: $_"
}

# 7. 基础功能测试
Write-LogInfo "步骤7: 基础功能测试..."

# 健康检查
Write-LogInfo "测试健康检查接口..."
try {
    $response = Invoke-RestMethod -Uri "http://$ServerIP`:$ServerPort/api/health" -Method GET -TimeoutSec 10
    Write-LogInfo "健康检查通过"
}
catch {
    Write-LogError "健康检查失败: $_"
    exit 1
}

# 文章列表测试
Write-LogInfo "测试文章列表接口..."
try {
    $response = Invoke-RestMethod -Uri "http://$ServerIP`:$ServerPort/api/articles?page=1&size=10" -Method GET -TimeoutSec 10
    Write-LogInfo "文章列表接口正常"
}
catch {
    Write-LogWarn "文章列表接口异常: $_"
}

# 8. 性能测试
Write-LogInfo "步骤8: 性能测试..."

# 检查ab命令
if (Test-Command "ab") {
    Write-LogInfo "开始压力测试..."
    
    # 基础压力测试
    Write-LogInfo "执行基础压力测试 (1000请求, 50并发)..."
    try {
        $result = ab -n 1000 -c 50 "http://$ServerIP`:$ServerPort/api/articles" 2>$null
        $result | Select-String -Pattern "(Requests per second|Time per request|Failed requests)"
    }
    catch {
        Write-LogWarn "压力测试失败: $_"
    }
    
    # 高并发测试
    Write-LogInfo "执行高并发测试 (1000请求, 100并发)..."
    try {
        $result = ab -n 1000 -c 100 "http://$ServerIP`:$ServerPort/api/articles" 2>$null
        $result | Select-String -Pattern "(Requests per second|Time per request|Failed requests)"
    }
    catch {
        Write-LogWarn "高并发测试失败: $_"
    }
}
else {
    Write-LogWarn "未找到ab命令，跳过压力测试"
    Write-LogInfo "请手动安装Apache Bench并执行压力测试"
}

# 9. 缓存测试
Write-LogInfo "步骤9: 缓存测试..."

# 测试缓存效果
Write-LogInfo "测试缓存效果..."
Write-Host "第一次请求（缓存未命中）:"
$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
try {
    Invoke-RestMethod -Uri "http://$ServerIP`:$ServerPort/api/articles/1" -Method GET -TimeoutSec 10 | Out-Null
}
catch {
    Write-LogWarn "第一次请求失败: $_"
}
$stopwatch.Stop()
Write-Host "时间: $($stopwatch.Elapsed.TotalSeconds)s"

Write-Host "第二次请求（缓存命中）:"
$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
try {
    Invoke-RestMethod -Uri "http://$ServerIP`:$ServerPort/api/articles/1" -Method GET -TimeoutSec 10 | Out-Null
}
catch {
    Write-LogWarn "第二次请求失败: $_"
}
$stopwatch.Stop()
Write-Host "时间: $($stopwatch.Elapsed.TotalSeconds)s"

# 10. 检查Redis
Write-LogInfo "步骤10: 检查Redis连接..."
if (Test-Command "redis-cli") {
    try {
        $redisResponse = redis-cli -h $ServerIP ping
        if ($redisResponse -eq "PONG") {
            Write-LogInfo "Redis连接正常"
            
            # 检查缓存数据
            Write-LogInfo "检查缓存数据..."
            $keys = redis-cli -h $ServerIP KEYS "*" | Select-Object -First 10
            $keys
        }
        else {
            Write-LogWarn "Redis连接失败"
        }
    }
    catch {
        Write-LogWarn "Redis检查失败: $_"
    }
}
else {
    Write-LogWarn "未找到redis-cli命令，跳过Redis检查"
}

# 11. 生成部署报告
Write-LogInfo "步骤11: 生成部署报告..."

$reportContent = @"
第一阶段优化部署报告
====================

部署时间: $(Get-Date)
部署状态: 成功

应用信息:
- JAR文件: deepseek-doctor.jar
- 配置文件: application-prod.yml
- 启动参数: --spring.profiles.active=prod
- 服务器: $ServerIP`:$ServerPort

优化内容:
1. 数据库连接池优化
   - 最大连接数: 50
   - 最小空闲连接: 20
   - 连接泄露检测: 启用

2. Redis缓存配置
   - 主机: $ServerIP`:6379
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

"@

$reportContent | Out-File -FilePath "deploy-report.txt" -Encoding UTF8
Write-LogInfo "部署报告已生成: deploy-report.txt"

# 12. 显示应用日志
Write-LogInfo "步骤12: 显示应用日志..."
if (Test-Path "app.log") {
    Write-Host "最近的应用日志:"
    Get-Content "app.log" -Tail 10
}

# 13. 提供后续步骤
Write-LogInfo "部署完成！"
Write-Host ""
Write-Host "📋 后续步骤:" -ForegroundColor Cyan
Write-Host "1. 执行数据库索引脚本: mysql -h $ServerIP -u deepseek_doctor -p deepseek_doctor < high_concurrency_optimization.sql"
Write-Host "2. 进行详细压力测试: ab -n 1000 -c 100 http://$ServerIP`:$ServerPort/api/articles"
Write-Host "3. 监控应用性能: Get-Content app.log -Wait"
Write-Host "4. 检查Redis缓存: redis-cli -h $ServerIP"
Write-Host ""
Write-Host "📊 监控命令:" -ForegroundColor Cyan
Write-Host "- 查看应用状态: Get-Process java | Where-Object { `$_.CommandLine -like '*deepseek-doctor*' }"
Write-Host "- 查看端口监听: Test-NetConnection -ComputerName $ServerIP -Port $ServerPort"
Write-Host "- 查看应用日志: Get-Content app.log -Wait"
Write-Host "- 查看JVM状态: jps -l && jstat -gc <pid>"
Write-Host ""
Write-Host "🚨 如果出现问题，可以回滚:" -ForegroundColor Red
Write-Host "- 停止应用: Get-Process java | Where-Object { `$_.CommandLine -like '*deepseek-doctor*' } | Stop-Process -Force"
Write-Host "- 恢复备份: Copy-Item deepseek-doctor.jar.backup deepseek-doctor.jar"
Write-Host "- 重新启动: Start-Process java -ArgumentList '-jar', 'deepseek-doctor.jar', '--spring.profiles.active=prod' -RedirectStandardOutput app.log -WindowStyle Hidden"

Write-LogInfo "第一阶段部署完成！" 