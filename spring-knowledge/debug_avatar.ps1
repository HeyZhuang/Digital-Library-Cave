# 头像显示问题调试脚本

Write-Host "=== 头像显示问题调试 ===" -ForegroundColor Yellow

# 1. 检查后端服务是否运行
Write-Host "`n1. 检查后端服务状态..." -ForegroundColor Cyan
try {
    $healthCheck = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/test" -Method GET -ErrorAction Stop
    Write-Host "✓ 后端服务正常运行" -ForegroundColor Green
} catch {
    Write-Host "✗ 后端服务未启动或有问题: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. 登录获取用户信息
Write-Host "`n2. 登录获取用户信息..." -ForegroundColor Cyan
$loginData = @{
    username = "cckzcc"
    password = "123456"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    Write-Host "✓ 登录成功" -ForegroundColor Green
    $token = $loginResponse.data.token
    $user = $loginResponse.data.user
    
    Write-Host "用户信息:" -ForegroundColor White
    Write-Host "  用户名: $($user.username)" -ForegroundColor Gray
    Write-Host "  昵称: $($user.nickname)" -ForegroundColor Gray
    Write-Host "  邮箱: $($user.email)" -ForegroundColor Gray
    Write-Host "  头像URL: $($user.avatar)" -ForegroundColor Gray
    
} catch {
    Write-Host "✗ 登录失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 3. 检查头像文件是否存在
Write-Host "`n3. 检查头像文件..." -ForegroundColor Cyan
if ($user.avatar) {
    # 从URL提取文件名
    $avatarUrl = $user.avatar
    if ($avatarUrl -match "/uploads/avatar/(.+)$") {
        $filename = $matches[1]
        $filePath = "./uploads/avatar/$filename"
        
        if (Test-Path $filePath) {
            $fileInfo = Get-Item $filePath
            Write-Host "✓ 头像文件存在: $filePath" -ForegroundColor Green
            Write-Host "  文件大小: $($fileInfo.Length) bytes" -ForegroundColor Gray
            Write-Host "  修改时间: $($fileInfo.LastWriteTime)" -ForegroundColor Gray
        } else {
            Write-Host "✗ 头像文件不存在: $filePath" -ForegroundColor Red
        }
    } else {
        Write-Host "✗ 无法解析头像URL: $avatarUrl" -ForegroundColor Red
    }
} else {
    Write-Host "✗ 用户没有设置头像" -ForegroundColor Red
}

# 4. 测试头像URL访问
Write-Host "`n4. 测试头像URL访问..." -ForegroundColor Cyan
if ($user.avatar) {
    try {
        $headers = @{
            "Authorization" = "Bearer $token"
        }
        
        # 测试直接访问头像URL
        $response = Invoke-WebRequest -Uri $user.avatar -Method GET -Headers $headers -ErrorAction Stop
        Write-Host "✓ 头像URL可访问" -ForegroundColor Green
        Write-Host "  状态码: $($response.StatusCode)" -ForegroundColor Gray
        Write-Host "  内容类型: $($response.Headers['Content-Type'])" -ForegroundColor Gray
        Write-Host "  内容长度: $($response.Headers['Content-Length']) bytes" -ForegroundColor Gray
        
    } catch {
        Write-Host "✗ 头像URL无法访问: $($_.Exception.Message)" -ForegroundColor Red
        
        # 尝试不带Authorization访问
        try {
            $response = Invoke-WebRequest -Uri $user.avatar -Method GET -ErrorAction Stop
            Write-Host "✓ 头像URL无需认证即可访问" -ForegroundColor Green
        } catch {
            Write-Host "✗ 头像URL完全无法访问" -ForegroundColor Red
        }
    }
}

# 5. 检查uploads目录结构
Write-Host "`n5. 检查uploads目录结构..." -ForegroundColor Cyan
if (Test-Path "./uploads") {
    Write-Host "✓ uploads目录存在" -ForegroundColor Green
    
    if (Test-Path "./uploads/avatar") {
        Write-Host "✓ avatar子目录存在" -ForegroundColor Green
        $avatarFiles = Get-ChildItem "./uploads/avatar" -File
        Write-Host "  头像文件数量: $($avatarFiles.Count)" -ForegroundColor Gray
        
        if ($avatarFiles.Count -gt 0) {
            Write-Host "  最近的头像文件:" -ForegroundColor Gray
            $avatarFiles | Sort-Object LastWriteTime -Descending | Select-Object -First 3 | ForEach-Object {
                Write-Host "    $($_.Name) - $($_.Length) bytes - $($_.LastWriteTime)" -ForegroundColor Gray
            }
        }
    } else {
        Write-Host "✗ avatar子目录不存在" -ForegroundColor Red
    }
} else {
    Write-Host "✗ uploads目录不存在" -ForegroundColor Red
}

Write-Host "`n=== 调试完成 ===" -ForegroundColor Yellow 