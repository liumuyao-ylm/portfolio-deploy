# 乡镇药店管理系统项目背景

## 项目概述

乡镇药店管理系统是一个面向乡镇单体药店和小型药房的前后端分离管理系统，主要用于药店日常进销存、顾客会员、库存盘点、系统日志和用户端药品查询等业务场景。

项目围绕药店常见经营流程设计，覆盖药品档案维护、供应商管理、采购入库、销售出库、库存预警、会员消费记录、Excel 导入导出和规则型 AI 客服等功能，适合作为课程设计、毕业设计、学习实践、简历作品和药店数字化业务演示项目。

## 项目路径

```text
E:\traeproject\pharmacy-system
```

## 仓库地址

```text
git@github.com:liumuyao-ylm/pharmacy-system.git
```

## 技术栈

后端技术：

- Spring Boot 3.2.5
- Spring Security
- Spring AOP
- MyBatis-Plus
- EasyExcel
- MySQL 8.x
- Maven
- JDK 17

前端技术：

- Vue 3
- Vue Router
- Element Plus
- Axios
- Vite

## 项目模块

```text
pharmacy-system/
├─ pharmacy-admin/      后端服务，Spring Boot 项目
├─ pharmacy-ui/         管理端前端，Vue 3 项目
├─ pharmacy-client/     用户端前端，Vue 3 项目
├─ docs/                数据库脚本、迁移脚本、截图和开发文档
└─ README.md            项目说明文档
```

## 管理端定位

管理端面向药店工作人员使用，主要服务于药店日常经营管理，包括：

- 药品管理
- 供应商管理
- 顾客/会员管理
- 采购订单与采购明细
- 销售订单与销售明细
- 库存管理
- 库存盘点
- 用户管理
- 系统日志
- Excel 导入导出

## 用户端定位

用户端面向普通顾客使用，主要提供药店基础信息展示和轻量查询能力，包括：

- 首页
- 药品查询
- 药品详情
- 会员中心
- 药店信息
- 规则型 AI 客服

## 数据库说明

数据库名称：

```text
pms_db
```

初始化脚本：

```text
docs/pms_db.sql
```

当前数据库脚本包含完整表结构和测试数据。核心数据表包括：

- user：系统用户/操作员
- customer：顾客/会员
- supplier：供应商
- medicine：药品基础档案
- purchase_order：采购订单
- purchase_item：采购明细
- stock：库存批次记录
- sale_order：销售订单
- sale_item：销售明细
- stock_check：库存盘点
- sys_log：系统操作日志

当前库存预警下限字段位于 `stock` 表中，药品表不再维护最低库存字段。

## 当前完成情况

项目已完成管理端基础 CRUD、用户端展示与查询、采购入库和库存自动增加、销售出库和库存自动扣减、库存不足拦截、Excel 导入导出、规则型 AI 客服、数据库初始化脚本和 README 文档整理。

后端、管理端和用户端均已通过基础构建验证。
