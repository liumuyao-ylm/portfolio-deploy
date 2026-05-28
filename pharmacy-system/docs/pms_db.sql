/*
  涔￠晣鑽簵绠＄悊绯荤粺鏁版嵁搴撳垵濮嬪寲鑴氭湰
  Schema: pms_db
  MySQL: 8.x

  璇存槑锛?  1. 鏈剼鏈凡鍚堝苟 migrate-stock-min-to-stock.sql 鐨勭粨鏋勮皟鏁淬€?  2. medicine 琛ㄤ笉鍐嶅寘鍚?stock_min 瀛楁銆?  3. stock 琛ㄥ寘鍚?stock_min 瀛楁锛岀敤浜庡簱瀛橀璀︿笅闄愩€?  4. 姣忓紶涓氬姟琛ㄥ潎鍐呯疆 20 鏉℃祴璇曟暟鎹紝渚夸簬涓婄嚎鍓嶈仈璋冦€?*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `sys_log`;
DROP TABLE IF EXISTS `stock_check`;
DROP TABLE IF EXISTS `sale_item`;
DROP TABLE IF EXISTS `sale_order`;
DROP TABLE IF EXISTS `stock`;
DROP TABLE IF EXISTS `purchase_item`;
DROP TABLE IF EXISTS `purchase_order`;
DROP TABLE IF EXISTS `medicine`;
DROP TABLE IF EXISTS `supplier`;
DROP TABLE IF EXISTS `customer`;
DROP TABLE IF EXISTS `user`;

-- ----------------------------
-- 鐢ㄦ埛/鎿嶄綔鍛樿〃
-- ----------------------------
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '涓婚敭锛岃嚜澧烇紝鐢ㄦ埛鍞竴缂栧彿',
  `username` varchar(50) NOT NULL COMMENT '鐧诲綍璐﹀彿',
  `password` varchar(100) NOT NULL COMMENT '鐧诲綍瀵嗙爜',
  `real_name` varchar(30) NOT NULL COMMENT '鐪熷疄濮撳悕',
  `role` enum('admin','staff','demo') NOT NULL COMMENT '瑙掕壊锛歛dmin=绠＄悊鍛橈紝staff=搴楀憳锛宒emo=婕旂ず鍙璐﹀彿',
  `phone` varchar(20) DEFAULT NULL COMMENT '鑱旂郴鐢佃瘽',
  `status` tinyint DEFAULT 1 COMMENT '1=姝ｅ父锛?=绂佺敤/绂昏亴',
  `remark` varchar(255) DEFAULT NULL COMMENT '澶囨敞',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏈€鍚庢洿鏂版椂闂?,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐢ㄦ埛/鎿嶄綔鍛樿〃';

-- ----------------------------
-- 椤惧/浼氬憳淇℃伅琛?-- ----------------------------
CREATE TABLE `customer` (
  `cust_id` int NOT NULL AUTO_INCREMENT COMMENT '涓婚敭锛岃嚜澧烇紝椤惧鍞竴缂栧彿',
  `cust_name` varchar(30) NOT NULL COMMENT '椤惧濮撳悕',
  `phone` varchar(20) NOT NULL COMMENT '鑱旂郴鐢佃瘽',
  `is_member` tinyint NOT NULL DEFAULT 0 COMMENT '1=浼氬憳锛?=闈炰細鍛?,
  `member_level` varchar(20) DEFAULT NULL COMMENT '浼氬憳绛夌骇',
  `total_consume` decimal(10,2) DEFAULT 0.00 COMMENT '绱娑堣垂鎬婚噾棰?,
  `birthday` date DEFAULT NULL COMMENT '鐢熸棩',
  `address` varchar(255) DEFAULT NULL COMMENT '鍦板潃',
  `status` tinyint DEFAULT 1 COMMENT '1=姝ｅ父锛?=鎷夐粦/绂佺敤',
  `remark` varchar(255) DEFAULT NULL COMMENT '澶囨敞',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏈€鍚庢洿鏂版椂闂?,
  PRIMARY KEY (`cust_id`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='椤惧/浼氬憳淇℃伅琛?;

-- ----------------------------
-- 渚涘簲鍟嗕俊鎭〃
-- ----------------------------
CREATE TABLE `supplier` (
  `supplier_id` int NOT NULL AUTO_INCREMENT COMMENT '涓婚敭锛岃嚜澧烇紝渚涘簲鍟嗗敮涓€缂栧彿',
  `supplier_name` varchar(100) NOT NULL COMMENT '渚涘簲鍟嗗叏绉?,
  `short_name` varchar(50) DEFAULT NULL COMMENT '绠€绉?,
  `contact` varchar(30) DEFAULT NULL COMMENT '鑱旂郴浜哄鍚?,
  `phone` varchar(20) DEFAULT NULL COMMENT '鑱旂郴鐢佃瘽',
  `telephone` varchar(20) DEFAULT NULL COMMENT '鍥哄畾鐢佃瘽',
  `address` varchar(255) DEFAULT NULL COMMENT '鍏徃鍦板潃',
  `business_license` varchar(50) DEFAULT NULL COMMENT '钀ヤ笟鎵х収鍙?,
  `supply_type` varchar(50) DEFAULT NULL COMMENT '渚涘簲绫诲瀷',
  `bank_info` varchar(100) DEFAULT NULL COMMENT '寮€鎴疯鍙婅处鍙?,
  `status` tinyint DEFAULT 1 COMMENT '1=姝ｅ父鍚堜綔锛?=鏆傚仠/鍋滅敤',
  `remark` varchar(255) DEFAULT NULL COMMENT '澶囨敞淇℃伅',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`supplier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='渚涘簲鍟嗕俊鎭〃';

-- ----------------------------
-- 鑽搧鍩虹淇℃伅琛?-- ----------------------------
CREATE TABLE `medicine` (
  `med_id` int NOT NULL AUTO_INCREMENT COMMENT '涓婚敭锛岃嚜澧烇紝鑽搧鍞竴缂栧彿',
  `med_name` varchar(100) NOT NULL COMMENT '鑽搧閫氱敤鍚嶇О',
  `med_alias` varchar(100) DEFAULT NULL COMMENT '鍟嗗搧鍚?鍒悕',
  `med_type` enum('涓嵂','瑗胯嵂','鍣ㄦ') NOT NULL COMMENT '绫诲瀷锛氫腑鑽?瑗胯嵂/鍣ㄦ',
  `spec` varchar(50) DEFAULT NULL COMMENT '瑙勬牸',
  `unit` varchar(20) DEFAULT NULL COMMENT '璁￠噺鍗曚綅',
  `dosage_form` varchar(30) DEFAULT NULL COMMENT '鍓傚瀷',
  `manufacturer` varchar(100) DEFAULT NULL COMMENT '鐢熶骇鍘傚',
  `approval_no` varchar(50) DEFAULT NULL COMMENT '鎵瑰噯鏂囧彿',
  `retail_price` decimal(10,2) NOT NULL COMMENT '闆跺敭鍞环',
  `purchase_price` decimal(10,2) DEFAULT NULL COMMENT '鍙傝€冭繘浠?,
  `is_rx` tinyint DEFAULT 0 COMMENT '1=澶勬柟鑽紝0=闈炲鏂硅嵂',
  `status` tinyint DEFAULT 1 COMMENT '1=姝ｅ父鍦ㄥ敭锛?=鍋滃敭/涓嬫灦',
  `remark` varchar(255) DEFAULT NULL COMMENT '澶囨敞',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`med_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鑽搧鍩虹淇℃伅琛?;

-- ----------------------------
-- 閲囪喘璁㈠崟涓昏〃
-- ----------------------------
CREATE TABLE `purchase_order` (
  `purchase_id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭锛岄噰璐崟鍙?,
  `supplier_id` int NOT NULL COMMENT '鍏宠仈渚涘簲鍟咺D',
  `user_id` int NOT NULL COMMENT '鍒跺崟/閲囪喘鎿嶄綔鍛?,
  `purchase_time` datetime NOT NULL COMMENT '閲囪喘鏃堕棿',
  `total_amount` decimal(10,2) NOT NULL COMMENT '閲囪喘鎬婚噾棰?,
  `pay_type` varchar(20) DEFAULT NULL COMMENT '鏀粯鏂瑰紡',
  `purchase_status` tinyint DEFAULT 0 COMMENT '1=宸插叆搴擄紝0=寰呭叆搴擄紝2=宸蹭綔搴?,
  `remark` varchar(255) DEFAULT NULL COMMENT '澶囨敞',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`purchase_id`),
  KEY `fk_purchase_supplier` (`supplier_id`),
  KEY `fk_purchase_user` (`user_id`),
  CONSTRAINT `fk_purchase_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`),
  CONSTRAINT `fk_purchase_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閲囪喘璁㈠崟涓昏〃';

-- ----------------------------
-- 閲囪喘鏄庣粏琛?-- ----------------------------
CREATE TABLE `purchase_item` (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭锛屾槑缁嗗敮涓€ID',
  `purchase_id` bigint NOT NULL COMMENT '鍏宠仈閲囪喘涓昏〃',
  `med_id` int NOT NULL COMMENT '鍏宠仈鑽搧ID',
  `batch_no` varchar(50) NOT NULL COMMENT '鑽搧鎵瑰彿',
  `production_date` date DEFAULT NULL COMMENT '鐢熶骇鏃ユ湡',
  `expire_date` date DEFAULT NULL COMMENT '鏈夋晥鏈熻嚦',
  `purchase_num` int NOT NULL COMMENT '閲囪喘鏁伴噺',
  `purchase_price` decimal(10,2) NOT NULL COMMENT '閲囪喘鍗曚环',
  `total_price` decimal(10,2) NOT NULL COMMENT '灏忚閲戦',
  `cabinet` varchar(30) DEFAULT NULL COMMENT '寤鸿瀛樻斁鑽煖',
  `remark` varchar(255) DEFAULT NULL COMMENT '澶囨敞',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`item_id`),
  KEY `fk_purchase_item_order` (`purchase_id`),
  KEY `fk_purchase_item_med` (`med_id`),
  CONSTRAINT `fk_purchase_item_med` FOREIGN KEY (`med_id`) REFERENCES `medicine` (`med_id`),
  CONSTRAINT `fk_purchase_item_order` FOREIGN KEY (`purchase_id`) REFERENCES `purchase_order` (`purchase_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閲囪喘鏄庣粏琛?;

-- ----------------------------
-- 搴撳瓨琛?-- ----------------------------
CREATE TABLE `stock` (
  `stock_id` int NOT NULL AUTO_INCREMENT COMMENT '涓婚敭锛岃嚜澧烇紝搴撳瓨璁板綍鍞竴ID',
  `med_id` int NOT NULL COMMENT '鍏宠仈鑽搧ID',
  `batch_no` varchar(50) NOT NULL COMMENT '鑽搧鎵瑰彿',
  `expire_date` date DEFAULT NULL COMMENT '鏈夋晥鏈熻嚦',
  `stock_num` int NOT NULL DEFAULT 0 COMMENT '褰撳墠搴撳瓨鏁伴噺',
  `stock_min` int DEFAULT 0 COMMENT '搴撳瓨棰勮涓嬮檺',
  `purchase_price` decimal(10,2) DEFAULT NULL COMMENT '鏈鍏ュ簱鍗曚环',
  `cabinet` varchar(30) DEFAULT NULL COMMENT '鑽煖浣嶇疆',
  `production_date` date DEFAULT NULL COMMENT '鐢熶骇鏃ユ湡',
  `supplier_id` int DEFAULT NULL COMMENT '鏉ユ簮渚涘簲鍟?,
  `purchase_id` bigint DEFAULT NULL COMMENT '鏉ユ簮閲囪喘璁㈠崟ID',
  `purchase_item_id` bigint DEFAULT NULL COMMENT '鏉ユ簮閲囪喘鏄庣粏ID',
  `status` tinyint DEFAULT 1 COMMENT '1=姝ｅ父锛?=绂佺敤/娓呭簱',
  `remark` varchar(255) DEFAULT NULL COMMENT '澶囨敞',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍏ュ簱鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏈€鍚庢洿鏂版椂闂?,
  PRIMARY KEY (`stock_id`),
  KEY `fk_stock_medicine` (`med_id`),
  KEY `fk_stock_supplier` (`supplier_id`),
  KEY `idx_stock_purchase_id` (`purchase_id`),
  KEY `idx_stock_purchase_item_id` (`purchase_item_id`),
  CONSTRAINT `fk_stock_medicine` FOREIGN KEY (`med_id`) REFERENCES `medicine` (`med_id`),
  CONSTRAINT `fk_stock_purchase_item` FOREIGN KEY (`purchase_item_id`) REFERENCES `purchase_item` (`item_id`),
  CONSTRAINT `fk_stock_purchase_order` FOREIGN KEY (`purchase_id`) REFERENCES `purchase_order` (`purchase_id`),
  CONSTRAINT `fk_stock_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='搴撳瓨琛紙鎸夋壒鍙风鐞嗭級';

-- ----------------------------
-- 閿€鍞鍗曚富琛?-- ----------------------------
CREATE TABLE `sale_order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '閿€鍞崟ID',
  `cust_id` int DEFAULT NULL COMMENT '鍏宠仈椤惧ID',
  `user_id` int NOT NULL COMMENT '鎿嶄綔鍛?搴楀憳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '璁㈠崟鍒涘缓鏃堕棿',
  `total_price` decimal(10,2) NOT NULL COMMENT '璁㈠崟鎬讳环',
  `pay_type` varchar(20) DEFAULT NULL COMMENT '鏀粯鏂瑰紡',
  `order_type` tinyint DEFAULT 1 COMMENT '1=绾夸笅锛?=绾夸笂',
  `pay_status` tinyint DEFAULT 0 COMMENT '0=鏈敮浠橈紝1=宸叉敮浠橈紝2=宸查€€娆?,
  `remark` varchar(255) DEFAULT NULL COMMENT '璁㈠崟澶囨敞',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏈€鍚庢洿鏂版椂闂?,
  PRIMARY KEY (`order_id`),
  KEY `fk_sale_customer` (`cust_id`),
  KEY `fk_sale_user` (`user_id`),
  CONSTRAINT `fk_sale_customer` FOREIGN KEY (`cust_id`) REFERENCES `customer` (`cust_id`),
  CONSTRAINT `fk_sale_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閿€鍞鍗曚富琛?;

-- ----------------------------
-- 閿€鍞鍗曟槑缁嗚〃
-- ----------------------------
CREATE TABLE `sale_item` (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭锛屾槑缁嗗敮涓€ID',
  `order_id` bigint NOT NULL COMMENT '鍏宠仈閿€鍞鍗旾D',
  `med_id` int NOT NULL COMMENT '鍏宠仈鑽搧ID',
  `batch_no` varchar(50) NOT NULL COMMENT '鑽搧鎵瑰彿',
  `quantity` int NOT NULL COMMENT '閿€鍞暟閲?,
  `unit_price` decimal(10,2) NOT NULL COMMENT '閿€鍞崟浠?,
  `total_price` decimal(10,2) NOT NULL COMMENT '灏忚閲戦',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`item_id`),
  KEY `fk_item_order` (`order_id`),
  KEY `fk_item_medicine` (`med_id`),
  CONSTRAINT `fk_item_medicine` FOREIGN KEY (`med_id`) REFERENCES `medicine` (`med_id`),
  CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `sale_order` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閿€鍞鍗曟槑缁嗚〃';

-- ----------------------------
-- 搴撳瓨鐩樼偣琛?-- ----------------------------
CREATE TABLE `stock_check` (
  `check_id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭锛岃嚜澧烇紝鐩樼偣鏄庣粏鍞竴ID',
  `check_no` varchar(50) NOT NULL COMMENT '鐩樼偣鍗曞彿',
  `med_id` int NOT NULL COMMENT '鍏宠仈鑽搧ID',
  `batch_no` varchar(50) NOT NULL COMMENT '鑽搧鎵瑰彿',
  `system_stock` int NOT NULL COMMENT '璐﹂潰搴撳瓨',
  `actual_stock` int NOT NULL COMMENT '瀹為檯搴撳瓨',
  `profit_loss` int NOT NULL COMMENT '鐩堜簭鏁伴噺',
  `unit_price` decimal(10,2) DEFAULT NULL COMMENT '鎴愭湰鍗曚环',
  `profit_loss_amount` decimal(10,2) DEFAULT NULL COMMENT '鐩堜簭閲戦',
  `check_user` int NOT NULL COMMENT '鐩樼偣鎿嶄綔鍛?,
  `check_time` datetime NOT NULL COMMENT '鐩樼偣鏃堕棿',
  `reason` varchar(255) DEFAULT NULL COMMENT '鐩堜簭鍘熷洜璇存槑',
  `remark` varchar(255) DEFAULT NULL COMMENT '澶囨敞',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '璁板綍鍒涘缓鏃堕棿',
  PRIMARY KEY (`check_id`),
  KEY `fk_check_medicine` (`med_id`),
  KEY `fk_check_user` (`check_user`),
  CONSTRAINT `fk_check_medicine` FOREIGN KEY (`med_id`) REFERENCES `medicine` (`med_id`),
  CONSTRAINT `fk_check_user` FOREIGN KEY (`check_user`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='搴撳瓨鐩樼偣琛?;

-- ----------------------------
-- 绯荤粺鎿嶄綔鏃ュ織琛?-- ----------------------------
CREATE TABLE `sys_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭锛岃嚜澧烇紝鏃ュ織鍞竴ID',
  `user_id` int DEFAULT NULL COMMENT '鍏宠仈鐢ㄦ埛ID',
  `username` varchar(50) DEFAULT NULL COMMENT '鎿嶄綔璐﹀彿',
  `real_name` varchar(30) DEFAULT NULL COMMENT '鎿嶄綔浜哄鍚?,
  `oper_module` varchar(50) DEFAULT NULL COMMENT '鎿嶄綔妯″潡',
  `oper_type` varchar(30) DEFAULT NULL COMMENT '鎿嶄綔绫诲瀷',
  `oper_content` varchar(500) DEFAULT NULL COMMENT '鎿嶄綔璇︽儏',
  `oper_ip` varchar(50) DEFAULT NULL COMMENT '鎿嶄綔IP鍦板潃',
  `oper_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鎿嶄綔鏃堕棿',
  `remark` varchar(255) DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`log_id`),
  KEY `fk_log_user` (`user_id`),
  CONSTRAINT `fk_log_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='绯荤粺鎿嶄綔鏃ュ織琛?;

-- ----------------------------
-- Test data for user
-- ----------------------------
-- ----------------------------
-- Initial users only
-- Password hashes use the project-compatible MD5 fallback in PasswordUtil.matches().
-- ----------------------------
INSERT INTO `user` (`user_id`, `username`, `password`, `real_name`, `role`, `phone`, `status`, `remark`, `create_time`, `update_time`) VALUES
(1,'admin','d630274734afe3e7cb966fb89f805f42','admin','admin',NULL,1,'谣666',NOW(),NOW()),
(2,'demo','fe01ce2a7fbac8fafaed7c982a04e229','demo','demo',NULL,1,'demo',NOW(),NOW());

SET FOREIGN_KEY_CHECKS = 1;
