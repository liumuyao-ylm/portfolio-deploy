@echo off
chcp 65001
title 项目自动启动工具
:: 切换到脚本所在目录
cd /d %~dp0

echo ==============================================
echo  1. 正在执行 npm install 安装依赖
echo ==============================================
call npm install
if %errorlevel% neq 0 (
    echo.
    echo 【错误】npm install 执行失败！
    echo 请检查是否已安装 Node.js 并配置环境变量
    pause
    exit
)

echo.
echo ==============================================
echo  2. 正在启动 npm run dev
echo ==============================================
call npm run dev

echo.
echo 服务已停止运行
pause