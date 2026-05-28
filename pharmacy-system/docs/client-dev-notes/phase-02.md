# 用户端第二阶段开发记录

## 本阶段目标

让用户端药品查询页和药品详情页读取后端真实数据，新增面向顾客的药品查询与详情接口，避免暴露进价、供应商、内部备注等敏感字段。

## 完成内容

- 后端新增 `ClientMedicineController`。
- 后端新增用户端药品查询接口：`GET /api/client/medicines`。
- 后端新增用户端药品详情接口：`GET /api/client/medicines/{id}`。
- 新增 `ClientMedicineVO`，只返回用户可见字段。
- 查询接口支持药品名称/别名、类型、剂型、处方药、库存状态筛选。
- 根据有效库存汇总数量计算库存状态：有货、库存较少、无货。
- 前端新增 `src/api/client-medicine.js`。
- 药品查询页改为调用真实接口，保留分页、筛选和首页搜索跳转。
- 药品详情页改为调用真实接口。
- Vite 开发服务新增 `/api` 代理到后端 `http://127.0.0.1:8080`。

## 新增和修改文件

- `pharmacy-admin/src/main/java/com/pharmacy/controller/ClientMedicineController.java`
- `pharmacy-admin/src/main/java/com/pharmacy/vo/ClientMedicineVO.java`
- `pharmacy-admin/src/main/java/com/pharmacy/mapper/MedicineMapper.java`
- `pharmacy-client/src/api/client-medicine.js`
- `pharmacy-client/src/views/medicines/index.vue`
- `pharmacy-client/src/views/medicine-detail/index.vue`
- `pharmacy-client/vite.config.js`
- `docs/client-dev-notes/phase-02.md`

## 接口说明

### GET `/api/client/medicines`

查询参数：

- `current`：页码，默认 1。
- `size`：每页数量，默认 6。
- `keyword`：药品名称或别名。
- `type`：药品类型。
- `dosageForm`：剂型。
- `isRx`：是否处方药，`1` 为处方药，`0` 为非处方药。
- `stockStatus`：库存状态，可选 `有货`、`库存较少`、`无货`。

### GET `/api/client/medicines/{id}`

根据药品 ID 查询用户端详情。

## 返回字段说明

返回字段包含：

- `id`
- `name`
- `medAlias`
- `medType`
- `spec`
- `unit`
- `dosageForm`
- `manufacturer`
- `approvalNo`
- `retailPrice`
- `isRx`
- `stockCount`
- `stockStatus`

不返回 `purchasePrice`、`supplierName`、`remark` 等内部字段。

## 当前限制

- 本阶段只接入药品查询与详情，会员、AI 客服和门店信息仍沿用第一阶段静态数据。
- 库存状态按当前有效库存总数与药品最低库存阈值计算。
- 前端筛选项暂使用固定选项，后续可改为由字典接口或后端聚合接口提供。

## 自测结果

- 已执行 `npm run build`，用户端生产构建通过。
- 已执行 `mvn -q -DskipTests package`，后端编译打包通过。
- 未执行真实数据库接口联调；需本地 MySQL `pms_db` 和后端服务启动后进一步验证接口数据。

## 下一阶段建议

- 启动后端和用户端，使用真实数据库验证查询条件、分页和详情返回。
- 为药品类型、剂型等筛选项增加后端字典或聚合接口。
- 继续将门店信息、会员中心或 AI 客服按优先级接入真实服务。
