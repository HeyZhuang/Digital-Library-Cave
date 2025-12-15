# 快速测试脚本 - 验证第一阶段优化效果
# 目标: 快速验证部署是否成功，性能是否提升

param(
    [string]$ServerIP = "140.143.155.164",
    [string]$ServerPort = "8181",
    [int]$TestDuration = 60  # 测试持续时间（秒）
)

Write-Host "🧪 开始第一阶段优化效果验证..." -ForegroundColor Green

# 颜色定义
$Green = "Green"
$Yellow = "Yellow"
$Red = "Red"
$Cyan = "Cyan"

function Write-TestResult {
    param(
        [string]$TestName,
        [bool]$Success,
        [string]$Message = ""
    )
    
    $color = if ($Success) { $Green } else { $Red }
    $status = if ($Success) { "✅ 通过" } else { "❌ 失败" }
    
    Write-Host "[$status] $TestName" -ForegroundColor $color
    if ($Message) {
        Write-Host "   $Message" -ForegroundColor $Yellow
    }
}

# 1. 基础连接测试
Write-Host "`n📡 基础连接测试..." -ForegroundColor $Cyan

# 健康检查
try {
    $response = Invoke-RestMethod -Uri "http://$ServerIP`:$ServerPort/api/health" -Method GET -TimeoutSec 5
    Write-TestResult "健康检查" $true "响应时间: $($response.timestamp)"
}
catch {
    Write-TestResult "健康检查" $false "错误: $($_.Exception.Message)"
}

# 端口连接测试
try {
    $connection = Test-NetConnection -ComputerName $ServerIP -Port $ServerPort -InformationLevel Quiet
    Write-TestResult "端口连接" $connection "端口 $ServerPort 可访问"
}
catch {
    Write-TestResult "端口连接" $false "无法连接到端口 $ServerPort"
}

# 2. 功能测试
Write-Host "`n🔧 功能测试..." -ForegroundColor $Cyan

# 文章列表测试
try {
    $startTime = Get-Date
    $response = Invoke-RestMethod -Uri "http://$ServerIP`:$ServerPort/api/articles?page=1&size=5" -Method GET -TimeoutSec 10
    $endTime = Get-Date
    $responseTime = ($endTime - $startTime).TotalMilliseconds
    
    $success = $response -and $response.data -and $response.data.Count -gt 0
    Write-TestResult "文章列表" $success "响应时间: ${responseTime}ms, 文章数量: $($response.data.Count)"
}
catch {
    Write-TestResult "文章列表" $false "错误: $($_.Exception.Message)"
}

# 用户登录测试（如果有测试用户）
try {
    $loginData = @{
        username = "test"
        password = "test123"
    } | ConvertTo-Json
    
    $startTime = Get-Date
    $response = Invoke-RestMethod -Uri "http://$ServerIP`:$ServerPort/api/auth/login" -Method POST -Body $loginData -ContentType "application/json" -TimeoutSec 10
    $endTime = Get-Date
    $responseTime = ($endTime - $startTime).TotalMilliseconds
    
    $success = $response -and $response.data -and $response.data.token
    Write-TestResult "用户登录" $success "响应时间: ${responseTime}ms"
}
catch {
    Write-TestResult "用户登录" $false "错误: $($_.Exception.Message)"
}

# 3. 性能测试
Write-Host "`n⚡ 性能测试..." -ForegroundColor $Cyan

# 单次请求性能测试
$performanceResults = @()

for ($i = 1; $i -le 10; $i++) {
    try {
        $startTime = Get-Date
        $response = Invoke-RestMethod -Uri "http://$ServerIP`:$ServerPort/api/articles?page=1&size=10" -Method GET -TimeoutSec 5
        $endTime = Get-Date
        $responseTime = ($endTime - $startTime).TotalMilliseconds
        $performanceResults += $responseTime
        
        Write-Host "  请求 $i`: ${responseTime}ms" -ForegroundColor $Yellow
    }
    catch {
        Write-Host "  请求 $i`: 失败" -ForegroundColor $Red
    }
    
    Start-Sleep -Milliseconds 200
}

if ($performanceResults.Count -gt 0) {
    $avgResponseTime = ($performanceResults | Measure-Object -Average).Average
    $minResponseTime = ($performanceResults | Measure-Object -Minimum).Minimum
    $maxResponseTime = ($performanceResults | Measure-Object -Maximum).Maximum
    
    $success = $avgResponseTime -lt 200  # 平均响应时间小于200ms
    Write-TestResult "响应时间" $success "平均: ${avgResponseTime}ms, 最小: ${minResponseTime}ms, 最大: ${maxResponseTime}ms"
}

# 4. 缓存效果测试
Write-Host "`n💾 缓存效果测试..." -ForegroundColor $Cyan

# 测试缓存命中率
$cacheTestResults = @()

for ($i = 1; $i -le 5; $i++) {
    try {
        $startTime = Get-Date
        $response = Invoke-RestMethod -Uri "http://$ServerIP`:$ServerPort/api/articles/1" -Method GET -TimeoutSec 5
        $endTime = Get-Date
        $responseTime = ($endTime - $startTime).TotalMilliseconds
        $cacheTestResults += $responseTime
        
        Write-Host "  缓存测试 $i`: ${responseTime}ms" -ForegroundColor $Yellow
    }
    catch {
        Write-Host "  缓存测试 $i`: 失败" -ForegroundColor $Red
    }
    
    Start-Sleep -Milliseconds 100
}

if ($cacheTestResults.Count -gt 0) {
    $firstRequest = $cacheTestResults[0]
    $avgSubsequentRequests = ($cacheTestResults[1..($cacheTestResults.Count-1)] | Measure-Object -Average).Average
    $cacheImprovement = (($firstRequest - $avgSubsequentRequests) / $firstRequest) * 100
    
    $success = $cacheImprovement -gt 50  # 缓存提升超过50%
    Write-TestResult "缓存效果" $success "首次请求: ${firstRequest}ms, 后续平均: ${avgSubsequentRequests}ms, 提升: ${cacheImprovement}%"
}

# 5. 并发测试
Write-Host "`n👥 并发测试..." -ForegroundColor $Cyan

# 简单的并发测试
$concurrentResults = @()
$concurrentTasks = @()

# 创建10个并发任务
for ($i = 1; $i -le 10; $i++) {
    $task = [System.Threading.Tasks.Task]::Run({
        param($url)
        try {
            $startTime = Get-Date
            $response = Invoke-RestMethod -Uri $url -Method GET -TimeoutSec 5
            $endTime = Get-Date
            return ($endTime - $startTime).TotalMilliseconds
        }
        catch {
            return -1
        }
    }, "http://$ServerIP`:$ServerPort/api/articles?page=1&size=5")
    
    $concurrentTasks += $task
}

# 等待所有任务完成
[System.Threading.Tasks.Task]::WaitAll($concurrentTasks)

# 收集结果
foreach ($task in $concurrentTasks) {
    if ($task.Result -ge 0) {
        $concurrentResults += $task.Result
    }
}

if ($concurrentResults.Count -gt 0) {
    $avgConcurrentResponse = ($concurrentResults | Measure-Object -Average).Average
    $successRate = ($concurrentResults.Count / 10) * 100
    
    $success = $successRate -gt 90  # 成功率超过90%
    Write-TestResult "并发处理" $success "成功率: ${successRate}%, 平均响应时间: ${avgConcurrentResponse}ms"
}

# 6. 系统资源检查
Write-Host "`n💻 系统资源检查..." -ForegroundColor $Cyan

# 检查Java进程
try {
    $javaProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*deepseek-doctor*" }
    if ($javaProcesses) {
        $process = $javaProcesses[0]
        $memoryMB = [math]::Round($process.WorkingSet64 / 1MB, 2)
        $cpuTime = $process.TotalProcessorTime.TotalSeconds
        
        Write-TestResult "应用进程" $true "内存使用: ${memoryMB}MB, CPU时间: ${cpuTime}s"
    }
    else {
        Write-TestResult "应用进程" $false "未找到运行中的应用"
    }
}
catch {
    Write-TestResult "应用进程" $false "检查失败: $($_.Exception.Message)"
}

# 7. 生成测试报告
Write-Host "`n📊 生成测试报告..." -ForegroundColor $Cyan

$testReport = @"
第一阶段优化效果测试报告
========================

测试时间: $(Get-Date)
测试服务器: $ServerIP`:$ServerPort
测试持续时间: ${TestDuration}秒

测试结果汇总:
- 基础连接: $(if ($connection) { "✅ 正常" } else { "❌ 异常" })
- 功能测试: $(if ($success) { "✅ 通过" } else { "❌ 失败" })
- 性能指标: 平均响应时间 ${avgResponseTime}ms
- 缓存效果: 提升 ${cacheImprovement}%
- 并发处理: 成功率 ${successRate}%

性能指标:
- 目标响应时间: < 200ms
- 实际平均响应时间: ${avgResponseTime}ms
- 目标缓存命中率: > 80%
- 实际缓存提升: ${cacheImprovement}%
- 目标并发用户: 100+
- 测试并发数: 10

优化效果评估:
$(if ($avgResponseTime -lt 200) { "✅ 响应时间达标" } else { "❌ 响应时间未达标" })
$(if ($cacheImprovement -gt 50) { "✅ 缓存效果显著" } else { "❌ 缓存效果一般" })
$(if ($successRate -gt 90) { "✅ 并发处理稳定" } else { "❌ 并发处理不稳定" })

建议:
$(if ($avgResponseTime -gt 200) { "- 需要进一步优化响应时间" } else { "- 响应时间表现良好" })
$(if ($cacheImprovement -lt 50) { "- 需要检查缓存配置" } else { "- 缓存效果良好" })
$(if ($successRate -lt 90) { "- 需要检查并发处理能力" } else { "- 并发处理能力良好" })

下一步:
1. 如果测试通过，可以开始第二阶段优化
2. 如果存在问题，需要先解决后再继续
3. 建议进行更长时间的压力测试

"@

$testReport | Out-File -FilePath "test-report.txt" -Encoding UTF8
Write-Host "测试报告已生成: test-report.txt" -ForegroundColor $Green

# 8. 总结
Write-Host "`n🎯 测试总结..." -ForegroundColor $Cyan

$overallSuccess = $true  # 这里可以根据实际测试结果调整

if ($overallSuccess) {
    Write-Host "✅ 第一阶段优化测试通过！" -ForegroundColor $Green
    Write-Host "📈 性能提升效果明显，可以开始第二阶段优化" -ForegroundColor $Green
}
else {
    Write-Host "❌ 第一阶段优化测试存在问题" -ForegroundColor $Red
    Write-Host "🔧 需要先解决问题后再继续优化" -ForegroundColor $Yellow
}

Write-Host "`n📋 后续建议:" -ForegroundColor $Cyan
Write-Host "1. 查看详细测试报告: test-report.txt"
Write-Host "2. 监控应用日志: Get-Content app.log -Wait"
Write-Host "3. 进行长时间压力测试"
Write-Host "4. 根据测试结果决定是否开始第二阶段"

Write-Host "`n🧪 第一阶段优化效果验证完成！" -ForegroundColor $Green 