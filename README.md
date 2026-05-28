# portfolio-deploy

这是姚学良个人项目统一部署仓库，用于 Git 上传和服务器拉取部署。

## 目录说明

- `pharmacy-system/`：乡镇药店管理系统。
- `pharmacy-system/pharmacy-admin/`：Spring Boot 后端源码。
- `pharmacy-system/pharmacy-ui/`：管理端前端源码。
- `pharmacy-system/pharmacy-client/`：用户端前端源码。
- `pharmacy-system/docs/pms_db.sql`：数据库表结构和必要初始化用户。
- `portfolio-site/`：个人作品集静态站点源码。
- `newplayer/`：轻量级音乐播放器源码。
- `deploy/`：Nginx、systemd 和服务器部署说明。

## 简要部署

1. 在服务器安装 JDK、Maven、Node.js、MySQL、Nginx 和 Git。
2. 克隆本仓库并导入 `pharmacy-system/docs/pms_db.sql`。
3. 通过环境变量配置后端数据库连接：`DB_URL`、`DB_USERNAME`、`DB_PASSWORD`。
4. 在 `pharmacy-system/pharmacy-admin` 执行 `mvn package` 并启动生成的 jar。
5. 分别在 `pharmacy-system/pharmacy-ui` 和 `pharmacy-system/pharmacy-client` 执行 `npm install`、`npm run build`。
6. 将 Nginx 指向作品集目录和两个前端项目的 `dist` 目录，并把 `/api/` 反向代理到 Spring Boot 后端。

详细步骤见 `deploy/deploy.md`。

## 注意

不要提交本地环境私密配置、数据库密码、构建产物、依赖目录、日志文件和 IDE 缓存。
