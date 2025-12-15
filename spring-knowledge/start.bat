@echo off
chcp 65001 >nul
title Deepseek SpringAI 知识库系统

echo ==========================================
echo   Deepseek SpringAI 知识库系统
echo ==========================================

:: 检查Java环境
echo 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未找到Java环境，请安装JDK 21或更高版本
    pause
    exit /b 1
)
echo Java环境检查通过

:: 检查Maven环境
echo 检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未找到Maven环境，请安装Maven 3.6或更高版本
    pause
    exit /b 1
)
echo Maven环境检查通过

:: 编译项目
echo 编译项目...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo 错误：项目编译失败
    pause
    exit /b 1
)
echo 项目编译成功

:: 启动应用
echo 启动应用...
echo ==========================================
echo 应用启动中，请稍候...
echo 启动完成后访问：http://localhost:8080
echo 默认管理员账户：admin / 123456
echo 按 Ctrl+C 停止应用
echo ==========================================

call mvn spring-boot:run

pause 