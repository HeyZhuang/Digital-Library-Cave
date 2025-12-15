#!/bin/bash

# Deepseek SpringAI 知识库系统启动脚本

echo "=========================================="
echo "  Deepseek SpringAI 知识库系统"
echo "=========================================="

# 检查Java版本
check_java() {
    echo "检查Java环境..."
    if type -p java; then
        _java=java
    elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]]; then
        _java="$JAVA_HOME/bin/java"
    else
        echo "错误：未找到Java环境，请安装JDK 21或更高版本"
        exit 1
    fi

    version=$($_java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo "Java版本: $version"
    
    if [[ "$version" < "21" ]]; then
        echo "错误：Java版本过低，需要JDK 21或更高版本"
        exit 1
    fi
}

# 检查MySQL连接
check_mysql() {
    echo "检查MySQL连接..."
    mysql -h127.0.0.1 -P3306 -uroot -proot -e "SELECT VERSION();" > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "MySQL连接正常"
    else
        echo "警告：MySQL连接失败，请检查数据库配置"
        read -p "是否继续启动？(y/n): " continue_start
        if [ "$continue_start" != "y" ]; then
            exit 1
        fi
    fi
}

# 初始化数据库
init_database() {
    echo "检查数据库..."
    db_exists=$(mysql -h127.0.0.1 -P3306 -uroot -proot -e "SHOW DATABASES LIKE 'deepseek-doctor';" 2>/dev/null | grep deepseek-doctor)
    
    if [ -z "$db_exists" ]; then
        echo "数据库不存在，正在创建..."
        mysql -h127.0.0.1 -P3306 -uroot -proot -e "CREATE DATABASE \`deepseek-doctor\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
        
        if [ -f "../deepseek-doctor.sql" ]; then
            echo "导入数据库结构..."
            mysql -h127.0.0.1 -P3306 -uroot -proot deepseek-doctor < ../deepseek-doctor.sql
            echo "数据库初始化完成"
        else
            echo "警告：未找到数据库初始化文件"
        fi
    else
        echo "数据库已存在"
    fi
}

# 编译项目
build_project() {
    echo "编译项目..."
    mvn clean compile -q
    if [ $? -eq 0 ]; then
        echo "项目编译成功"
    else
        echo "错误：项目编译失败"
        exit 1
    fi
}

# 启动应用
start_application() {
    echo "启动应用..."
    echo "=========================================="
    echo "应用启动中，请稍候..."
    echo "启动完成后访问：http://localhost:8080"
    echo "默认管理员账户：admin / 123456"
    echo "=========================================="
    
    mvn spring-boot:run
}

# 主函数
main() {
    check_java
    check_mysql
    init_database
    build_project
    start_application
}

# 处理信号
trap 'echo "正在停止应用..."; exit 0' SIGINT SIGTERM

# 运行主函数
main 