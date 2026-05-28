# AI 客服功能开发交接总结

## 项目背景

本项目为乡镇药店管理系统，当前包含三个主要工程：

- 后端：`pharmacy-admin`
- 管理端前端：`pharmacy-ui`
- 用户端前端：`pharmacy-client`
- 开发记录与参考资料：`docs`

用户端已经完成首页、药品查询、药品详情、会员中心、药店信息等基础查询闭环。AI 客服功能此前已决定延后到项目上传 GitHub 前开发，本文件用于开启 AI 客服开发前的交接。

## 当前用户端状态

用户端项目路径：

```text
E:\traeproject\pharmacy-system\pharmacy-client
```

主要页面：

- `/`：首页
- `/medicines`：药品查询
- `/medicines/:id`：药品详情
- `/member`：会员中心
- `/store`：药店信息
- `/chat`：AI 客服页面，目前为占位/待开发页面

当前用户端已接入的后端接口：

```text
GET  /api/client/medicines
GET  /api/client/medicines/{id}
GET  /api/client/store-info
GET  /api/client/member/page
GET  /api/client/member/profile
GET  /api/client/member/orders
```

## AI 客服开发目标

本次 AI 客服不追求复杂智能问诊，只要求“能用、能回答简单问题”。

建议目标：

- 用户可以在 `/chat` 页面输入问题。
- 系统能返回简单、稳定、可控的回答。
- 优先回答药店相关问题，例如：
  - 营业时间
  - 药店地址
  - 联系电话
  - 处方药购买说明
  - 会员消费记录在哪里看
  - 药品是否可以通过药品查询页检索
- 对无法确认的问题给出安全兜底回答。
- 涉及具体病情、诊断、用药剂量时提示咨询药师或医生。

## 推荐实现方案

考虑到开发者此前未接触过 AI 客服功能，建议先做“规则型智能客服”，暂不接入真正大模型。

这样优点是：

- 不需要申请 API Key。
- 不依赖外网。
- 不涉及费用。
- 更容易测试。
- 对药店系统演示足够稳定。

### 建议后端接口

新增接口：

```text
POST /api/client/chat
```

请求体示例：

```json
{
  "message": "药店营业时间是几点？"
}
```

返回示例：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "reply": "本店营业时间为 08:00 - 21:30，建议到店前可先电话确认。"
  }
}
```

### 后端建议文件

可新增：

```text
pharmacy-admin/src/main/java/com/pharmacy/controller/ClientChatController.java
pharmacy-admin/src/main/java/com/pharmacy/dto/ClientChatDTO.java
pharmacy-admin/src/main/java/com/pharmacy/vo/ClientChatVO.java
```

### 回答规则建议

后端可以用关键词判断：

- 包含 `营业`、`几点`、`时间`：返回营业时间。
- 包含 `地址`、`在哪`、`位置`：返回药店地址。
- 包含 `电话`、`联系`：返回联系电话。
- 包含 `处方`：提示处方药需凭处方购买。
- 包含 `会员`、`消费记录`：引导到会员中心查看。
- 包含 `药品`、`库存`、`有没有`：引导到药品查询页检索，库存以页面展示为准。
- 其他问题：返回兜底说明。

医疗安全兜底建议：

```text
我可以提供药店服务和药品查询相关帮助，但不能替代医生或药师诊断。若涉及具体病情、剂量或联合用药，请咨询店内药师或及时就医。
```

## 前端开发建议

用户端已有页面：

```text
pharmacy-client/src/views/chat/index.vue
```

建议新增 API 文件：

```text
pharmacy-client/src/api/client-chat.js
```

建议前端效果：

- 左侧展示常见问题快捷入口。
- 右侧为聊天窗口。
- 点击常见问题可自动发送或填入输入框。
- 用户输入后展示用户气泡。
- 后端返回后展示客服气泡。
- 请求失败时提示“AI 客服暂时不可用，请稍后再试”。

参考图位置：

```text
docs/images/client-ai-chat.png
```

## 测试方法

### 0. 配置数据库环境变量

后端 `application.yml` 已使用环境变量读取数据库连接信息：

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/pms_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}
```

在 Windows PowerShell 中，本次终端临时设置方式：

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/pms_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的数据库密码"
```

如果要长期保存到当前用户环境变量：

```powershell
[Environment]::SetEnvironmentVariable("DB_URL", "jdbc:mysql://localhost:3306/pms_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false", "User")
[Environment]::SetEnvironmentVariable("DB_USERNAME", "root", "User")
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "你的数据库密码", "User")
```

长期保存后需要重新打开终端或 IDE，环境变量才会在新进程中生效。

### 1. 后端编译测试

在项目根目录执行：

```bash
cd pharmacy-admin
mvn -q -DskipTests package
```

预期结果：

- 命令执行成功。
- 没有 Java 编译错误。

### 2. 前端构建测试

在项目根目录执行：

```bash
cd pharmacy-client
npm run build
```

预期结果：

- 命令执行成功。
- Vite 构建通过。

### 3. 启动后端

在 `pharmacy-admin` 中启动 Spring Boot 服务。

服务地址：

```text
http://127.0.0.1:8080
```

### 4. 启动用户端

在 `pharmacy-client` 中执行：

```bash
npm run dev -- --host 127.0.0.1
```

用户端地址通常为：

```text
http://127.0.0.1:5174
```

### 5. 接口测试

可用浏览器开发者工具、Postman 或 Apifox 测试：

```text
POST http://127.0.0.1:8080/api/client/chat
```

请求体：

```json
{
  "message": "药店营业时间是几点？"
}
```

预期返回：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "reply": "..."
  }
}
```

### 6. 页面测试用例

打开：

```text
http://127.0.0.1:5174/chat
```

建议输入以下问题逐个测试：

```text
药店营业时间是几点？
药店地址在哪里？
联系电话是多少？
处方药怎么买？
我想看会员消费记录
布洛芬还有货吗？
感冒应该吃什么药？
```

预期结果：

- 前 6 个问题应能返回明确服务类回答。
- “感冒应该吃什么药？”这类具体用药问题应触发安全提示，建议咨询药师或医生。
- 页面不应报错。
- 刷新页面后功能仍可正常使用。

## 注意事项

- 本阶段不要把 AI 客服做成真正医疗问诊。
- 不要承诺诊断结果。
- 不要给出明确剂量、疗程或联合用药方案。
- 药品库存以药品查询页和后端库存接口为准。
- 如后续要接入真正大模型，应另行处理 API Key、安全提示、敏感问题兜底和调用失败降级。

## 建议提交信息

```bash
git add .
git commit -m "feat(client): add simple ai customer service"
```
