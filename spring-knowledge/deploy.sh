#!/bin/bash

# 横扫江湖个人知识库 - 后端部署脚本
# 服务器IP: 140.143.155.164

echo "🚀 开始部署横扫江湖个人知识库后端..."

# 设置环境变量
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# 检查Java版本
echo "📋 检查Java版本..."
java -version

# 检查Maven版本
echo "📋 检查Maven版本..."
mvn -version

# 清理并编译项目
echo "🔨 编译项目..."
mvn clean package -DskipTests -Pprod

# 检查编译结果
if [ $? -ne 0 ]; then
    echo "❌ 编译失败，请检查错误信息"
    exit 1
fi

# 停止现有服务
echo "🛑 停止现有服务..."
pkill -f "spring-knowledge" || true

# 等待服务完全停止
sleep 3

# 备份旧版本
echo "💾 备份旧版本..."
if [ -f "spring-knowledge.jar" ]; then
    mv spring-knowledge.jar spring-knowledge.jar.backup.$(date +%Y%m%d_%H%M%S)
fi

# 复制新版本
echo "📦 部署新版本..."
cp target/spring-knowledge-*.jar spring-knowledge.jar

# 启动服务
echo "🚀 启动服务..."
nohup java -jar spring-knowledge.jar --spring.profiles.active=prod > app.log 2>&1 &

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 10

# 检查服务状态
echo "🔍 检查服务状态..."
if curl -f http://localhost:8181/actuator/health > /dev/null 2>&1; then
    echo "✅ 服务启动成功！"
    echo "🌐 服务地址: http://140.143.155.164:8181"
    echo "📊 健康检查: http://140.143.155.164:8181/actuator/health"
else
    echo "❌ 服务启动失败，请检查日志: tail -f app.log"
    exit 1
fi

echo "🎉 部署完成！" 