# 用户端第三阶段开发记录

## 本阶段目标

完成用户端药店信息与会员中心接入，让用户端具备药品查询、门店信息、会员资料和消费记录查询的基础闭环。

## 完成内容

- 后端新增药店信息接口：`GET /api/client/store-info`。
- 后端新增会员手机号模拟登录接口：`POST /api/client/member/login`。
- 后端新增会员分页浏览接口：`GET /api/client/member/page`。
- 后端新增会员资料接口：`GET /api/client/member/profile`。
- 后端新增会员消费记录接口：`GET /api/client/member/orders`。
- 会员手机号在用户端返回时做脱敏展示。
- 会员资料返回 `remark` 字段，用于展示药物过敏等特殊备注。
- 消费记录返回订单基础信息、支付状态、订单摘要和药品明细。
- 前端新增 `src/api/client-store.js`。
- 前端新增 `src/api/client-member.js`。
- 药店信息页改为调用后端接口，并保留本地兜底展示数据。
- 会员中心改为分页浏览会员资料，一页展示一位会员，联动展示该会员消费记录和订单详情弹窗。

## 新增和修改文件

- `pharmacy-admin/src/main/java/com/pharmacy/controller/ClientStoreController.java`
- `pharmacy-admin/src/main/java/com/pharmacy/controller/ClientMemberController.java`
- `pharmacy-admin/src/main/java/com/pharmacy/dto/ClientMemberLoginDTO.java`
- `pharmacy-admin/src/main/java/com/pharmacy/vo/ClientStoreInfoVO.java`
- `pharmacy-admin/src/main/java/com/pharmacy/vo/ClientMemberProfileVO.java`
- `pharmacy-admin/src/main/java/com/pharmacy/vo/ClientMemberOrderVO.java`
- `pharmacy-admin/src/main/java/com/pharmacy/vo/ClientMemberOrderItemVO.java`
- `pharmacy-client/src/api/client-store.js`
- `pharmacy-client/src/api/client-member.js`
- `pharmacy-client/src/views/store/index.vue`
- `pharmacy-client/src/views/member/index.vue`
- `docs/client-dev-notes/phase-03.md`

## 接口说明

### GET `/api/client/store-info`

返回药店名称、营业状态、营业时间、联系电话、地址、经营范围、公告、位置文案、路线说明和服务说明。

### POST `/api/client/member/login`

请求体：

```json
{
  "phone": "13800000000"
}
```

根据手机号查询正常状态的会员。返回会员资料，其中手机号为脱敏展示。

### GET `/api/client/member/page`

查询参数：

- `current`：页码，默认 1。
- `size`：每页数量，用户端固定为 1。

返回正常状态会员分页数据，会员资料包含脱敏手机号、会员等级、累计消费、地址、生日和 `remark` 备注。

### GET `/api/client/member/profile`

查询参数：

- `customerId`：会员顾客 ID。

### GET `/api/client/member/orders`

查询参数：

- `customerId`：会员顾客 ID。

返回字段包含订单 ID、下单时间、订单金额、支付方式、订单类型、支付状态、订单摘要和订单明细。

## 当前限制

- 当前会员中心采用分页浏览模式，手机号模拟登录接口保留为后续登录方案备用。
- 药店信息先由后端配置式返回，未新增独立门店信息表。
- 订单接口按会员顾客 ID 查询，依赖销售订单中已关联 `cust_id`。
