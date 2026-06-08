@echo off
chcp 65001 >nul
echo ========================================
echo   校园消费数据管理平台 - 快速启动脚本
echo ========================================
echo.

echo [步骤1] 检查Java环境...
where java >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Java环境
    echo 请先安装JDK 17或更高版本
    echo 下载地址: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
echo [成功] Java环境正常
echo.

echo [步骤2] 检查Maven环境...
where mvn >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Maven环境
    echo 请先安装Maven 3.6或更高版本
    echo 下载地址: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
echo [成功] Maven环境正常
echo.

echo [步骤3] 编译项目...
echo 正在执行: mvn clean package -DskipTests
echo.
call mvn clean package -DskipTests
if errorlevel 1 (
    echo.
    echo [错误] 编译失败，请检查错误信息
    pause
    exit /b 1
)
echo.
echo [成功] 编译完成
echo.

echo [步骤4] 启动应用...
echo.
echo ========================================
echo   应用启动中，请稍候...
echo   前端页面: http://localhost:8080/index.html
echo   API文档:  http://localhost:8080/doc.html
echo   按 Ctrl+C 停止应用
echo ========================================
echo.

java -jar target\campus-dashboard-1.0.0.jar

pause
