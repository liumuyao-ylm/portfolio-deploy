# 乡镇药店管理系统后续开发前景提要

本文档用于新开一次会话时作为项目背景交接资料，帮助后续继续开发“采购入库、销售出库与库存自动联动逻辑”和“Excel 导入导出功能”。

## 1. 项目基本信息

- 项目名称：乡镇药店管理系统
- 项目路径：`E:\traeproject\pharmacy-system`
- GitHub 仓库：`https://github.com/ylm29942678-ai/pharmacy-system`
- 当前主分支：`master`
- 最近一次已推送提交：`2011e20 feat(client): add user portal and ai customer service`
- 上线设想：Nginx 静态资源托管前端 + Nginx 反向代理 Spring Boot 后端

## 2. 技术栈

### 后端

- Spring Boot 3.2.5
- Spring Security
- Spring AOP
- MyBatis-Plus
- MySQL 8.x
- Maven
- JDK 17

### 前端

- Vue 3
- Vue Router
- Element Plus
- Axios
- Vite

### 数据库

- 数据库名：`pms_db`
- 初始化脚本：`docs/pms_db.sql`
- 数据库连接配置位于：`pharmacy-admin/src/main/resources/application.yml`
- 当前数据库连接信息已改为读取环境变量：

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/pms_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}
```

注意：不要把真实数据库密码提交到 GitHub。

## 3. 当前项目模块

```text
pharmacy-system/
├── pharmacy-admin/      后端服务，Spring Boot 项目
├── pharmacy-ui/         管理端前端，Vue 3 项目
├── pharmacy-client/     用户端前端，Vue 3 项目
├── docs/                数据库脚本、截图、开发记录、后续开发提要
└── README.md            项目说明文档
```

## 4. 后端分层说明

后端主要采用常见的 Controller、Service、Mapper 分层结构。

```text
controller   接收前端请求，提供 REST API
service      处理业务逻辑
service/impl 业务逻辑实现
mapper       数据库访问层
entity       数据库表对应的 Java 实体
dto          前端传入参数对象
vo           返回给前端展示的数据对象
common       统一返回结果封装
config       安全、跨域、MyBatis-Plus 等配置
exception    全局异常处理
aspect       AOP 操作日志切面
annotation   自定义操作日志注解
```

统一响应对象：

```text
pharmacy-admin/src/main/java/com/pharmacy/common/Result.java
```

返回格式大致为：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

## 5. 已完成开发进程总结

### 5.1 管理端基础功能

管理端项目路径：

```text
pharmacy-ui
```

主要页面：

```text
登录页
控制台
药品管理
供应商管理
顾客/会员管理
采购订单
采购明细
销售订单
销售明细
库存管理
库存盘点
用户管理
系统日志
```

管理端主要用于药店工作人员进行日常进销存管理。

### 5.2 后端管理接口

后端项目路径：

```text
pharmacy-admin
```

已包含的核心 Controller：

```text
UserController
MedicineController
SupplierController
CustomerController
PurchaseOrderController
PurchaseItemController
SaleOrderController
SaleItemController
StockController
StockCheckController
DashboardController
SysLogController
```

目前这些模块已经支撑管理端基础 CRUD、分页查询、订单管理、库存管理、盘点管理和日志查询。

### 5.3 用户端开发

用户端项目路径：

```text
pharmacy-client
```

用户端已完成页面：

```text
/                         首页
/medicines                药品查询
/medicines/:id            药品详情
/member                   会员中心
/store                    药店信息
/chat                     AI 客服
```

用户端 API 文件：

```text
pharmacy-client/src/api/client-medicine.js
pharmacy-client/src/api/client-member.js
pharmacy-client/src/api/client-store.js
pharmacy-client/src/api/client-chat.js
```

用户端请求封装：

```text
pharmacy-client/src/utils/request.js
```

Vite 代理配置：

```text
pharmacy-client/vite.config.js
```

用户端默认开发端口：

```text
http://127.0.0.1:5174
```

### 5.4 用户端后端接口

已完成的用户端后端接口：

```text
GET  /api/client/medicines
GET  /api/client/medicines/{id}
GET  /api/client/store-info
POST /api/client/member/login
GET  /api/client/member/page
GET  /api/client/member/profile
GET  /api/client/member/orders
POST /api/client/chat
```

相关后端文件：

```text
pharmacy-admin/src/main/java/com/pharmacy/controller/ClientMedicineController.java
pharmacy-admin/src/main/java/com/pharmacy/controller/ClientMemberController.java
pharmacy-admin/src/main/java/com/pharmacy/controller/ClientStoreController.java
pharmacy-admin/src/main/java/com/pharmacy/controller/ClientChatController.java
```

相关 VO/DTO：

```text
ClientMedicineVO
ClientStoreInfoVO
ClientMemberProfileVO
ClientMemberOrderVO
ClientMemberOrderItemVO
ClientChatDTO
ClientChatVO
```

### 5.5 AI 客服功能

AI 客服不是接入大模型，而是规则型客服。

前端页面：

```text
pharmacy-client/src/views/chat/index.vue
```

前端 API：

```text
pharmacy-client/src/api/client-chat.js
```

后端接口：

```text
POST /api/client/chat
```

后端文件：

```text
pharmacy-admin/src/main/java/com/pharmacy/controller/ClientChatController.java
pharmacy-admin/src/main/java/com/pharmacy/dto/ClientChatDTO.java
pharmacy-admin/src/main/java/com/pharmacy/vo/ClientChatVO.java
```

支持回答的问题类型：

```text
营业时间
药店地址
联系电话
处方药购买说明
会员消费记录查看入口
药品查询和库存入口
医疗安全兜底提示
```

医疗安全原则：

```text
不做诊断
不给剂量
不给疗程
不提供联合用药方案
遇到具体病情、剂量、用法等问题时，引导咨询药师或医生
```

## 6. README 和 GitHub 上传情况

README 已经重新整理为适合 GitHub 展示的中文说明文档，包含：

```text
项目简介
项目模块
功能概览
页面预览
技术栈
项目结构
快速启动
数据库环境变量配置
验证命令
AI 客服接口示例
数据库说明
安全说明
后续规划
```

已经完成 GitHub 推送。

最近一次提交：

```text
2011e20 feat(client): add user portal and ai customer service
```

注意：推送过程中曾遇到 GitHub SSH 连接重置，最后使用如下方式成功：

```bash
git -c pack.threads=1 push github master
```

## 7. 已验证内容

后端编译验证：

```bash
cd pharmacy-admin
mvn -q -DskipTests package
```

结果：通过。

用户端构建验证：

```bash
cd pharmacy-client
npm run build
```

结果：通过。存在 Vite 大 chunk 警告，不影响构建。

管理端构建验证：

```bash
cd pharmacy-ui
npm run build
```

结果：通过。存在 Vite 大 chunk 警告，不影响构建。

AI 客服接口验证：

```text
POST http://127.0.0.1:8080/api/client/chat
POST http://127.0.0.1:5174/api/client/chat
```

结果：后端直连和用户端代理链路都返回 `code: 200`。

## 8. 当前需要重点注意的问题

### 8.1 中文编码显示

部分终端读取旧文件时会显示乱码，这是 PowerShell/历史文件编码显示问题。README 已重写为 UTF-8 内容，但在某些 PowerShell 输出里仍可能显示为乱码。

后续如果需要处理中文文件，应尽量保证：

```text
文件保存为 UTF-8
终端使用 UTF-8 编码
不要把乱码内容继续复制扩散
```

### 8.2 数据库密码

当前数据库密码已经放到电脑用户环境变量中，后端 `application.yml` 读取：

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

后续开发不要把真实密码写到：

```text
application.yml
README.md
bat 脚本
docs 文档
```

### 8.3 工作区协作

后续如果继续开发，应该先检查：

```bash
git status --short
```

避免覆盖用户或前一次会话留下的改动。

## 9. 后续开发计划一：采购入库、销售出库与库存自动联动

这是下一阶段优先级最高的功能。

### 9.1 当前业务背景

系统已有：

```text
采购订单
采购明细
销售订单
销售明细
库存管理
```

但后续需要进一步完善它们之间的自动联动：

```text
采购入库后，库存自动增加
销售出库后，库存自动减少
库存不足时，销售应拦截或提示
订单状态变化时，避免重复入库或重复出库
```

### 9.2 建议实现目标

采购入库：

```text
1. 创建采购订单和采购明细
2. 点击“入库”
3. 系统根据采购明细增加库存
4. 更新采购订单入库状态
5. 禁止重复入库
```

销售出库：

```text
1. 创建销售订单和销售明细
2. 提交销售或确认支付后
3. 系统校验库存是否足够
4. 库存足够则扣减库存
5. 库存不足则返回错误提示
6. 避免重复扣减库存
```

### 9.3 重点关注文件

后端 Controller：

```text
pharmacy-admin/src/main/java/com/pharmacy/controller/PurchaseOrderController.java
pharmacy-admin/src/main/java/com/pharmacy/controller/PurchaseItemController.java
pharmacy-admin/src/main/java/com/pharmacy/controller/SaleOrderController.java
pharmacy-admin/src/main/java/com/pharmacy/controller/SaleItemController.java
pharmacy-admin/src/main/java/com/pharmacy/controller/StockController.java
```

后端 Service：

```text
pharmacy-admin/src/main/java/com/pharmacy/service/PurchaseOrderService.java
pharmacy-admin/src/main/java/com/pharmacy/service/PurchaseItemService.java
pharmacy-admin/src/main/java/com/pharmacy/service/SaleOrderService.java
pharmacy-admin/src/main/java/com/pharmacy/service/SaleItemService.java
pharmacy-admin/src/main/java/com/pharmacy/service/StockService.java
```

后端 Service 实现：

```text
pharmacy-admin/src/main/java/com/pharmacy/service/impl/PurchaseOrderServiceImpl.java
pharmacy-admin/src/main/java/com/pharmacy/service/impl/PurchaseItemServiceImpl.java
pharmacy-admin/src/main/java/com/pharmacy/service/impl/SaleOrderServiceImpl.java
pharmacy-admin/src/main/java/com/pharmacy/service/impl/SaleItemServiceImpl.java
pharmacy-admin/src/main/java/com/pharmacy/service/impl/StockServiceImpl.java
```

实体类：

```text
pharmacy-admin/src/main/java/com/pharmacy/entity/PurchaseOrder.java
pharmacy-admin/src/main/java/com/pharmacy/entity/PurchaseItem.java
pharmacy-admin/src/main/java/com/pharmacy/entity/SaleOrder.java
pharmacy-admin/src/main/java/com/pharmacy/entity/SaleItem.java
pharmacy-admin/src/main/java/com/pharmacy/entity/Stock.java
```

Mapper：

```text
pharmacy-admin/src/main/java/com/pharmacy/mapper/PurchaseOrderMapper.java
pharmacy-admin/src/main/java/com/pharmacy/mapper/PurchaseItemMapper.java
pharmacy-admin/src/main/java/com/pharmacy/mapper/SaleOrderMapper.java
pharmacy-admin/src/main/java/com/pharmacy/mapper/SaleItemMapper.java
pharmacy-admin/src/main/java/com/pharmacy/mapper/StockMapper.java
```

管理端页面：

```text
pharmacy-ui/src/views/purchase-order/
pharmacy-ui/src/views/purchase-item/
pharmacy-ui/src/views/sale-order/
pharmacy-ui/src/views/sale-item/
pharmacy-ui/src/views/stock/
```

### 9.4 实现建议

建议后续先从后端开始，不要先改页面。

推荐顺序：

```text
1. 梳理数据库表字段，确认采购订单、销售订单、库存表里的状态字段
2. 梳理现有入库或出库相关接口是否已经有雏形
3. 在 Service 层实现库存联动逻辑
4. 加事务 @Transactional，保证订单状态和库存更新同时成功或同时失败
5. 后端接口测通后，再补管理端按钮和页面提示
```

后端必须关注事务：

```java
@Transactional
```

原因：

```text
采购入库和库存增加必须同时成功
销售出库和库存扣减必须同时成功
如果中途报错，数据库应该自动回滚，避免订单状态变了但库存没变
```

### 9.5 可能的难点

```text
同一种药品可能有多个批次库存
销售扣库存时要决定按哪个批次扣
库存不足时如何提示
订单是否已经入库/出库，防止重复操作
采购退货或销售退货是否纳入本阶段
```

建议第一版先做简单规则：

```text
采购入库：按采购明细新增或累加库存
销售出库：按药品可用库存扣减，先不做复杂批次策略
重复操作：通过订单状态字段拦截
```

## 10. 后续开发计划二：Excel 导入导出功能

### 10.1 目标

给管理端增加常用数据的 Excel 导入导出能力。

优先建议：

```text
药品数据导出
药品数据导入
库存数据导出
销售订单导出
采购订单导出
```

### 10.2 技术建议

后端可选方案：

```text
EasyExcel
Apache POI
```

建议优先使用 EasyExcel，理由：

```text
写法相对简单
适合常规导入导出
性能比直接手写 POI 更友好
毕业设计和实习项目更容易讲清楚
```

### 10.3 推荐实现顺序

第一步：先做导出，不做导入。

```text
1. 药品列表导出为 Excel
2. 库存列表导出为 Excel
3. 采购订单或销售订单导出为 Excel
```

第二步：再做导入。

```text
1. 下载导入模板
2. 上传 Excel
3. 后端解析
4. 校验必填字段
5. 批量新增或更新
6. 返回导入成功/失败数量
```

### 10.4 重点关注文件

药品相关：

```text
pharmacy-admin/src/main/java/com/pharmacy/controller/MedicineController.java
pharmacy-admin/src/main/java/com/pharmacy/service/MedicineService.java
pharmacy-admin/src/main/java/com/pharmacy/service/impl/MedicineServiceImpl.java
pharmacy-admin/src/main/java/com/pharmacy/entity/Medicine.java
pharmacy-admin/src/main/java/com/pharmacy/mapper/MedicineMapper.java
pharmacy-ui/src/views/medicine/
```

库存相关：

```text
pharmacy-admin/src/main/java/com/pharmacy/controller/StockController.java
pharmacy-admin/src/main/java/com/pharmacy/service/StockService.java
pharmacy-admin/src/main/java/com/pharmacy/service/impl/StockServiceImpl.java
pharmacy-admin/src/main/java/com/pharmacy/entity/Stock.java
pharmacy-admin/src/main/java/com/pharmacy/mapper/StockMapper.java
pharmacy-ui/src/views/stock/
```

### 10.5 导入导出注意事项

```text
导入时必须做字段校验
导入失败要返回明确错误信息
导出时注意日期、价格、状态字段的格式化
不要一次性导出过大数据量
前端上传文件要限制格式为 .xlsx 或 .xls
```

# 
