# CORS跨域问题解决方案

## 问题描述

**错误信息**:
```
java.lang.IllegalArgumentException: When allowCredentials is true, allowedOrigins cannot contain the special value "*" since that cannot be set on the "Access-Control-Allow-Origin" response header. To allow credentials to a set of origins, list them explicitly or consider using "allowedOriginPatterns" instead.
```

**影响范围**: 前端请求`/api/articles`和`/api/tags`等接口时被浏览器拦截

## 问题原因

1. **安全限制**: 当`allowCredentials = true`时，浏览器的CORS安全策略要求：
   - 不能使用通配符`"*"`作为`allowedOrigins`
   - 必须明确指定允许的域名

2. **配置冲突**: 项目中存在两个CORS配置：
   - `SecurityConfig.java` - Spring Security的CORS配置
   - `CorsConfig.java` - WebMVC的CORS配置

## 解决方案

### ✅ 1. 更新CorsConfig.java

**修改前**:
```java
registry.addMapping("/**")
    .allowedOrigins(domain)  // 可能导致问题
    .allowCredentials(true);
```

**修改后**:
```java
registry.addMapping("/**")
    .allowedOriginPatterns(
        "http://localhost:*",     // 开发环境
        "http://127.0.0.1:*",    // 本地环境  
        domain,                   // 配置域名
        "https://*.yourdomain.com" // 生产环境
    )
    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
    .allowCredentials(true);
```

### ✅ 2. 更新SecurityConfig.java

**修改前**:
```java
configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
```

**修改后**:
```java
configuration.setAllowedOriginPatterns(Arrays.asList(
    "http://localhost:*",     // 本地开发环境
    "http://127.0.0.1:*",    // 本地环境
    "https://*.yourdomain.com" // 生产环境
));
```

### ✅ 3. 更新配置文件

**application-dev.yml**:
```yaml
website:
  domain: http://localhost:3000  # 前端开发服务器地址
```

## 技术原理

### allowedOrigins vs allowedOriginPatterns

| 配置项 | 支持通配符 | 安全性 | 使用场景 |
|--------|------------|---------|----------|
| `allowedOrigins` | ❌ | 高 | 生产环境，明确域名 |
| `allowedOriginPatterns` | ✅ | 中 | 开发环境，动态端口 |

### allowCredentials的影响

当`allowCredentials = true`时：
- ✅ 允许发送Cookie和Authorization头
- ✅ 支持JWT token认证
- ❌ 不能使用`allowedOrigins("*")`
- ✅ 必须明确指定域名或使用patterns

## 验证修复

### 1. 重启后端服务
```bash
cd deepseek-doctor
./start.sh
```

### 2. 启动前端服务
```bash
cd knowledge-frontend
npm run dev
```

### 3. 检查浏览器Console
- 不应该再有CORS错误
- API请求应该正常返回数据

### 4. 测试认证流程
- 用户登录功能
- JWT token携带
- 受保护API访问

## 生产环境配置

在生产环境中，建议：

### 1. 使用明确的域名
```java
configuration.setAllowedOriginPatterns(Arrays.asList(
    "https://yourdomain.com",
    "https://www.yourdomain.com",
    "https://api.yourdomain.com"
));
```

### 2. 配置环境变量
```yaml
# application-prod.yml
website:
  domain: https://yourdomain.com

cors:
  allowed-origins: 
    - https://yourdomain.com
    - https://www.yourdomain.com
```

### 3. 使用配置类
```java
@Value("${cors.allowed-origins}")
private List<String> allowedOrigins;

configuration.setAllowedOriginPatterns(allowedOrigins);
```

## 常见问题

### Q1: 仍然有CORS错误？
**A**: 检查是否有多个CORS配置冲突，确保所有配置都使用`allowedOriginPatterns`

### Q2: 本地开发端口变化？
**A**: 使用`http://localhost:*`模式自动适配不同端口

### Q3: 生产环境如何配置？
**A**: 使用明确的域名，避免使用通配符模式

### Q4: OPTIONS预检请求失败？
**A**: 确保`allowedMethods`包含`"OPTIONS"`

## 最佳实践

1. **开发环境**: 使用`allowedOriginPatterns`支持动态端口
2. **生产环境**: 使用`allowedOrigins`明确指定域名
3. **安全考虑**: 避免在生产环境使用通配符
4. **配置分离**: 通过配置文件管理不同环境的域名
5. **监控日志**: 定期检查CORS相关日志

现在CORS问题应该已经解决，前后端可以正常通信了！🎉 