#!/bin/bash

# 横扫江湖个人知识库 - 前端部署脚本
# 服务器IP: localhost

echo "🚀 开始部署横扫江湖个人知识库前端..."

# 检查Node.js版本
echo "📋 检查Node.js版本..."
node --version
npm --version

# 安装依赖
echo "📦 安装依赖..."
npm install

# 构建生产版本
echo "🔨 构建生产版本..."
npm run build

# 检查构建结果
if [ $? -ne 0 ]; then
    echo "❌ 构建失败，请检查错误信息"
    exit 1
fi

# 创建部署目录
echo "📁 创建部署目录..."
sudo mkdir -p /var/www/knowledge-frontend
sudo chown -R $USER:$USER /var/www/knowledge-frontend

# 复制构建文件
echo "📦 复制构建文件..."
cp -r dist/* /var/www/knowledge-frontend/

# 创建Nginx配置
echo "⚙️ 创建Nginx配置..."
sudo tee /etc/nginx/sites-available/knowledge-frontend << EOF
server {
    listen 3000;
    server_name localhost;
    root /var/www/knowledge-frontend;
    index index.html;

    # 启用gzip压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # API代理到后端
    location /api/ {
        proxy_pass http://localhost:8182;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        
        # CORS headers
        add_header Access-Control-Allow-Origin *;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
        add_header Access-Control-Allow-Headers "Authorization, Content-Type, X-Requested-With";
        
        # 处理OPTIONS预检请求
        if (\$request_method = 'OPTIONS') {
            add_header Access-Control-Allow-Origin *;
            add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
            add_header Access-Control-Allow-Headers "Authorization, Content-Type, X-Requested-With";
            add_header Access-Control-Max-Age 1728000;
            add_header Content-Type "text/plain; charset=utf-8";
            add_header Content-Length 0;
            return 204;
        }
    }

    # 处理前端路由
    location / {
        try_files \$uri \$uri/ /index.html;
    }

    # 安全头
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
}
EOF

# 启用站点
echo "🔗 启用Nginx站点..."
sudo ln -sf /etc/nginx/sites-available/knowledge-frontend /etc/nginx/sites-enabled/

# 测试Nginx配置
echo "🔍 测试Nginx配置..."
sudo nginx -t

if [ $? -ne 0 ]; then
    echo "❌ Nginx配置错误，请检查配置"
    exit 1
fi

# 重启Nginx
echo "🔄 重启Nginx..."
sudo systemctl reload nginx

# 检查服务状态
echo "🔍 检查服务状态..."
sleep 3
if curl -f http://localhost:3000 > /dev/null 2>&1; then
    echo "✅ 前端部署成功！"
    echo "🌐 前端地址: http://localhost:3000"
    echo "🔗 API地址: http://localhost:8182"
else
    echo "❌ 前端部署失败，请检查Nginx状态: sudo systemctl status nginx"
    exit 1
fi

echo "🎉 前端部署完成！" 