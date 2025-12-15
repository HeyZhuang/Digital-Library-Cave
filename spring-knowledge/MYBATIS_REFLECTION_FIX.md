# MyBatis反射异常修复说明

## 问题描述

**异常类型**: `org.mybatis.spring.MyBatisSystemException`
**根本原因**: `org.apache.ibatis.reflection.ReflectionException`
**错误信息**: 
```
Illegal overloaded getter method with ambiguous type for property 'enabled' in class 'com.itzixi.entity.User'. 
This breaks the JavaBeans specification and can cause unpredictable results.
```

## 问题分析

在`User`实体类中，`enabled`属性存在方法重载冲突：

### 问题原因
1. **字段定义**: `private Boolean enabled;` (包装类型)
2. **Lombok @Data**: 自动生成 `getEnabled()` 方法返回 `Boolean`
3. **UserDetails接口**: 要求实现 `isEnabled()` 方法返回 `boolean`

这导致了两个不同类型的getter方法：
- `Boolean getEnabled()` (Lombok生成)
- `boolean isEnabled()` (UserDetails接口要求)

MyBatis在进行反射操作时无法确定使用哪个方法，违反了JavaBeans规范。

## 解决方案

### ✅ 修改字段类型
将`enabled`字段从包装类型`Boolean`改为原始类型`boolean`：

```java
// 修改前
@TableField("enabled")
private Boolean enabled;  // 包装类型

@Override
@JsonIgnore
public boolean isEnabled() {
    return this.enabled != null && this.enabled;  // 需要空值检查
}

// 修改后
@TableField("enabled")
private boolean enabled;  // 原始类型

@Override
@JsonIgnore
public boolean isEnabled() {
    return this.enabled;  // 直接返回
}
```

### 修改效果
1. **消除方法冲突**: Lombok的`@Data`现在生成 `isEnabled()` 方法，与UserDetails接口要求一致
2. **符合JavaBeans规范**: 只有一个getter方法，类型明确
3. **简化逻辑**: 不需要空值检查，代码更简洁
4. **MyBatis兼容**: 反射操作正常，SQL映射正确

## 修改的文件

### User.java
```java
@TableField("enabled")
private boolean enabled;  // Boolean -> boolean

@Override
@JsonIgnore
public boolean isEnabled() {
    return this.enabled;  // 简化逻辑
}
```

## 验证修复

### 1. 编译检查
```bash
cd deepseek-doctor
mvn compile
```

### 2. 启动测试
```bash
./start.sh
```

### 3. 功能测试
- 用户注册：确保enabled字段正确设置为true
- 用户登录：验证UserDetails.isEnabled()方法工作正常
- 数据库操作：确认MyBatis能正确映射enabled字段

## 其他解决方案（备选）

如果需要保持`Boolean`类型，可以使用以下方案：

### 方案1：排除Lombok生成
```java
@Data
@EqualsAndHashCode(callSuper = false)
public class User implements UserDetails {
    
    @TableField("enabled")
    @Getter(AccessLevel.NONE) // 阻止Lombok生成getter
    private Boolean enabled;
    
    // 手动实现getter
    public Boolean getEnabled() {
        return this.enabled;
    }
    
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.enabled != null && this.enabled;
    }
}
```

### 方案2：重命名字段
```java
@TableField("enabled")
private Boolean isEnabled;  // 重命名字段

@Override
@JsonIgnore
public boolean isEnabled() {
    return this.isEnabled != null && this.isEnabled;
}
```

## 最佳实践

1. **类型选择**: 对于boolean字段，优先使用原始类型`boolean`而非包装类型`Boolean`
2. **接口实现**: 实现接口时注意方法签名与Lombok生成的方法是否冲突
3. **JavaBeans规范**: 确保每个属性只有一个明确的getter/setter方法
4. **测试验证**: 修改实体类后务必进行完整的功能测试

## 相关文档

- [JavaBeans规范](https://docs.oracle.com/javase/tutorial/javabeans/)
- [MyBatis反射机制](https://mybatis.org/mybatis-3/zh/configuration.html)
- [Lombok注解说明](https://projectlombok.org/features/all)
- [Spring Security UserDetails](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/user-details.html)

现在应用应该能够正常启动，不会再出现MyBatis反射异常了！ 