# 🚀 横扫江湖个人知识库 - 部署指南

## 📋 部署概览

- **服务器IP**: 140.143.155.164
- **前端端口**: 3000
- **后端端口**: 8181
- **数据库端口**: 3306

## 🔧 服务器环境要求

### 必需软件
```bash
# Java 11+
java -version

# Maven 3.6+
mvn -version

# Node.js 16+
node --version
npm --version

# Nginx
nginx -v

# MySQL 8.0+
mysql --version
```

### 系统要求
- **内存**: 至少 2GB RAM
- **存储**: 至少 10GB 可用空间
- **网络**: 稳定的网络连接

## 🛠️ 部署步骤

### 1. 后端部署

```bash
# 进入后端目录
cd spring-knowledge

# 给部署脚本执行权限
chmod +x deploy.sh

# 执行部署
./deploy.sh
```

### 2. 前端部署

```bash
# 进入前端目录
cd knowledge-frontend

# 给部署脚本执行权限
chmod +x deploy.sh

# 执行部署
./deploy.sh
```

## 🔍 验证部署

### 检查服务状态

```bash
# 检查后端服务
curl http://140.143.155.164:8181/actuator/health

# 检查前端服务
curl http://140.143.155.164:3000

# 检查数据库连接
mysql -h 140.143.155.164 -u deepseek_doctor -p
```

### 访问地址

- **前端应用**: http://140.143.155.164:3000
- **后端API**: http://140.143.155.164:8181
- **健康检查**: http://140.143.155.164:8181/actuator/health
- **API文档**: http://140.143.155.164:8181/swagger-ui/

## 🔧 跨域配置说明

### 后端CORS配置

已配置允许的源：
- `http://localhost:*` - 本地开发
- `http://127.0.0.1:*` - 本地环境
- `http://140.143.155.164:*` - 服务器IP
- `http://140.143.155.164:3000` - 前端地址

### 前端API配置

- **开发环境**: `http://localhost:8080/api`
- **生产环境**: `http://140.143.155.164:8181/api`

## 🚨 故障排除

### 常见问题

#### 1. 跨域错误 (CORS)
```bash
# 检查后端CORS配置
curl -H "Origin: http://140.143.155.164:3000" \
     -H "Access-Control-Request-Method: GET" \
     -H "Access-Control-Request-Headers: X-Requested-With" \
     -X OPTIONS http://140.143.155.164:8181/api/articles
```

#### 2. 服务无法访问
```bash
# 检查端口是否开放
netstat -tlnp | grep :8181
netstat -tlnp | grep :3000

# 检查防火墙
sudo ufw status
```

#### 3. 数据库连接失败
```bash
# 检查MySQL服务
sudo systemctl status mysql

# 检查数据库连接
mysql -h 140.143.155.164 -u deepseek_doctor -p deepseek_doctor
```

### 日志查看

```bash
# 后端日志
tail -f spring-knowledge/app.log

# Nginx日志
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log

# 系统日志
sudo journalctl -u nginx -f
```

## 🔄 更新部署

### 后端更新
```bash
cd spring-knowledge
git pull
./deploy.sh
```

### 前端更新
```bash
cd knowledge-frontend
git pull
./deploy.sh
```

## 📊 监控和维护

### 性能监控
```bash
# 查看系统资源
htop

# 查看磁盘使用
df -h

# 查看内存使用
free -h
```

### 备份策略
```bash
# 数据库备份
mysqldump -h 140.143.155.164 -u deepseek_doctor -p deepseek_doctor > backup.sql

# 文件备份
tar -czf uploads_backup.tar.gz uploads/
```

## 🔒 安全配置

### 防火墙设置
```bash
# 只开放必要端口
sudo ufw allow 22    # SSH
sudo ufw allow 3000  # 前端
sudo ufw allow 8181  # 后端
sudo ufw allow 3306  # 数据库
sudo ufw enable
```

### SSL证书（可选）
```bash
# 安装Let's Encrypt
sudo apt install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com
```

## 📞 技术支持

如遇到部署问题，请检查：
1. 服务器环境是否满足要求
2. 网络连接是否正常
3. 端口是否被占用
4. 日志文件中的错误信息

---

**部署完成时间**: $(date)
**部署版本**: v1.0.0 