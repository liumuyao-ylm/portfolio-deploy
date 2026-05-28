# 用户端第一阶段开发记录

## 本阶段目标

完成用户端前端项目骨架和 6 个静态页面框架，保持与管理端相近的 Vue 3 技术栈，不接入真实后端接口。

## 完成内容

- 新增 `pharmacy-client` 独立前端项目。
- 配置 Vite、Vue Router、Element Plus、Axios、`@element-plus/icons-vue`。
- 建立统一顶部导航：安心乡镇药房、首页、药品查询、AI客服、会员中心、药店信息。
- 建立全局主题样式，采用医药蓝、白色、浅灰和少量绿色/橙色状态色。
- 创建首页、药品查询、药品详情、AI 智能客服、会员中心、药店信息 6 个静态页面。
- 使用集中 mock 数据展示药品、会员、消费记录、门店与客服消息。

## 新增文件

- `pharmacy-client/package.json`
- `pharmacy-client/index.html`
- `pharmacy-client/vite.config.js`
- `pharmacy-client/src/main.js`
- `pharmacy-client/src/App.vue`
- `pharmacy-client/src/router/index.js`
- `pharmacy-client/src/utils/request.js`
- `pharmacy-client/src/data/mock.js`
- `pharmacy-client/src/styles/theme.css`
- `pharmacy-client/src/views/home/index.vue`
- `pharmacy-client/src/views/medicines/index.vue`
- `pharmacy-client/src/views/medicine-detail/index.vue`
- `pharmacy-client/src/views/chat/index.vue`
- `pharmacy-client/src/views/member/index.vue`
- `pharmacy-client/src/views/store/index.vue`
- `docs/client-dev-notes/phase-01.md`

## 页面路由

- `/`：首页
- `/medicines`：药品查询页
- `/medicines/:id`：药品详情页
- `/chat`：AI 智能客服页
- `/member`：会员中心页
- `/store`：药店信息页

## 静态数据说明

静态数据位于 `pharmacy-client/src/data/mock.js`，包含门店信息、常用分类、药品列表、会员信息、消费记录和聊天演示消息。当前页面只读取本地数据，不请求后端。

## 当前限制

- 未新增后端接口。
- 未修改数据库。
- 未接入真实药品库存、会员登录、消费记录和 AI 服务。
- 未改动管理端已有功能。

## 自测结果

- 已执行 `npm install` 安装依赖并生成锁文件。
- 已执行 `npm run build`，生产构建通过。
- 已启动 Vite 开发服务并通过 HTTP 请求验证 `/`、`/medicines`、`/chat`、`/store` 均返回 200。

## 下一阶段建议

- 明确用户端需要开放的后端查询接口。
- 将药品查询、药品详情和门店信息替换为真实接口数据。
- 设计会员登录/手机号验证流程。
- 接入可控的 AI 咨询服务，并补充药师免责声明和敏感问题兜底。
