# 横扫江湖个人知识库 - 跨域测试脚本
# 测试服务器: 140.143.155.164

Write-Host "🔍 开始测试横扫江湖个人知识库的跨域配置..." -ForegroundColor Green

# 测试后端API健康检查
Write-Host "`n📊 测试后端健康检查..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://140.143.155.164:8181/actuator/health" -Method GET -TimeoutSec 10
    Write-Host "✅ 后端服务正常: $($response.status)" -ForegroundColor Green
} catch {
    Write-Host "❌ 后端服务异常: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试CORS预检请求
Write-Host "`n🌐 测试CORS预检请求..." -ForegroundColor Yellow
try {
    $headers = @{
        "Origin" = "http://140.143.155.164:3000"
        "Access-Control-Request-Method" = "GET"
        "Access-Control-Request-Headers" = "Authorization,Content-Type"
    }
    
    $response = Invoke-WebRequest -Uri "http://140.143.155.164:8181/api/articles" -Method OPTIONS -Headers $headers -TimeoutSec 10
    
    Write-Host "✅ CORS预检请求成功" -ForegroundColor Green
    Write-Host "   允许的源: $($response.Headers['Access-Control-Allow-Origin'])" -ForegroundColor Cyan
    Write-Host "   允许的方法: $($response.Headers['Access-Control-Allow-Methods'])" -ForegroundColor Cyan
    Write-Host "   允许的头部: $($response.Headers['Access-Control-Allow-Headers'])" -ForegroundColor Cyan
} catch {
    Write-Host "❌ CORS预检请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试实际API请求
Write-Host "`n📡 测试API请求..." -ForegroundColor Yellow
try {
    $headers = @{
        "Origin" = "http://140.143.155.164:3000"
        "Content-Type" = "application/json"
    }
    
    $response = Invoke-RestMethod -Uri "http://140.143.155.164:8181/api/articles" -Method GET -Headers $headers -TimeoutSec 10
    Write-Host "✅ API请求成功，获取到 $($response.data.records.Count) 条文章" -ForegroundColor Green
} catch {
    Write-Host "❌ API请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试前端访问
Write-Host "`n🌍 测试前端访问..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://140.143.155.164:3000" -Method GET -TimeoutSec 10
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ 前端服务正常" -ForegroundColor Green
    } else {
        Write-Host "⚠️ 前端服务状态码: $($response.StatusCode)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ 前端服务异常: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试数据库连接
Write-Host "`n🗄️ 测试数据库连接..." -ForegroundColor Yellow
try {
    # 这里需要MySQL客户端，暂时跳过
    Write-Host "ℹ️ 数据库连接测试需要MySQL客户端，请手动验证" -ForegroundColor Cyan
} catch {
    Write-Host "❌ 数据库连接测试失败" -ForegroundColor Red
}

Write-Host "`n🎉 跨域测试完成！" -ForegroundColor Green
Write-Host "`n📋 测试结果总结:" -ForegroundColor Cyan
Write-Host "   - 后端API: http://140.143.155.164:8181" -ForegroundColor White
Write-Host "   - 前端应用: http://140.143.155.164:3000" -ForegroundColor White
Write-Host "   - 健康检查: http://140.143.155.164:8181/actuator/health" -ForegroundColor White
Write-Host "   - API文档: http://140.143.155.164:8181/swagger-ui/" -ForegroundColor White 