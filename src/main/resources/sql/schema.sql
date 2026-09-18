-- =====================================================
-- MES 生产执行演示系统 - 数据库建表脚本
-- MySQL 8.0+
-- =====================================================

CREATE DATABASE IF NOT EXISTS mes_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE mes_demo;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 一、系统管理层
-- =====================================================

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`    VARCHAR(64)  NOT NULL                COMMENT '用户名',
  `password`    VARCHAR(128) NOT NULL                COMMENT '密码（BCrypt加密）',
  `real_name`   VARCHAR(64)  DEFAULT NULL            COMMENT '真实姓名',
  `department`  VARCHAR(128) DEFAULT NULL            COMMENT '部门',
  `phone`       VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
  `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1启用 0停用',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0未删 1已删',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_code`   VARCHAR(64)  NOT NULL                COMMENT '角色编码',
  `role_name`   VARCHAR(128) NOT NULL                COMMENT '角色名称',
  `remark`      VARCHAR(256) DEFAULT NULL            COMMENT '备注',
  `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1启用 0停用',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id`       BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`  BIGINT NOT NULL COMMENT '用户ID',
  `role_id`  BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 权限表（菜单+按钮）
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id`    BIGINT       NOT NULL DEFAULT 0      COMMENT '父权限ID',
  `permission_code` VARCHAR(64) NOT NULL             COMMENT '权限编码',
  `permission_name` VARCHAR(128) NOT NULL            COMMENT '权限名称',
  `type`         TINYINT      NOT NULL                COMMENT '类型：1菜单 2按钮',
  `path`         VARCHAR(255) DEFAULT NULL            COMMENT '前端路由路径',
  `component`    VARCHAR(255) DEFAULT NULL            COMMENT '前端组件路径',
  `icon`         VARCHAR(64)  DEFAULT NULL            COMMENT '菜单图标',
  `sort`         INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `status`       TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1启用 0停用',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`      TINYINT     NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id`            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id`       BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 操作日志表
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`     BIGINT       DEFAULT NULL            COMMENT '操作用户ID',
  `username`    VARCHAR(64)  DEFAULT NULL            COMMENT '操作用户名',
  `module`      VARCHAR(64)  DEFAULT NULL            COMMENT '操作模块',
  `operation`   VARCHAR(128) DEFAULT NULL            COMMENT '操作内容',
  `method`      VARCHAR(16)  DEFAULT NULL            COMMENT '请求方法',
  `params`      TEXT         DEFAULT NULL            COMMENT '请求参数',
  `ip`          VARCHAR(64)  DEFAULT NULL            COMMENT 'IP地址',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 数据字典表
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dict_type`   VARCHAR(64)  NOT NULL                COMMENT '字典类型',
  `dict_label`  VARCHAR(128) NOT NULL                COMMENT '字典标签',
  `dict_value`  VARCHAR(128) NOT NULL                COMMENT '字典值',
  `sort`        INT          NOT NULL DEFAULT 0      COMMENT '排序',
  `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1启用 0停用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT     NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典表';

-- =====================================================
-- 二、基础数据层
-- =====================================================

-- 产品表
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_code`  VARCHAR(32)  NOT NULL                COMMENT '产品编码',
  `product_name`  VARCHAR(128) NOT NULL                COMMENT '产品名称',
  `specification` VARCHAR(128) DEFAULT NULL            COMMENT '规格型号',
  `unit`          VARCHAR(16)  NOT NULL                COMMENT '单位',
  `product_type`  TINYINT      NOT NULL DEFAULT 1      COMMENT '类型：1成品 2半成品',
  `route_id`      BIGINT       DEFAULT NULL            COMMENT '工艺路线ID',
  `status`        TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1启用 0停用',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`       TINYINT     NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_code` (`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- 物料表
DROP TABLE IF EXISTS `material`;
CREATE TABLE `material` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_code` VARCHAR(32)  NOT NULL                COMMENT '物料编码',
  `material_name` VARCHAR(128) NOT NULL                COMMENT '物料名称',
  `specification` VARCHAR(128) DEFAULT NULL            COMMENT '规格型号',
  `unit`          VARCHAR(16)  NOT NULL                COMMENT '单位',
  `material_type` TINYINT      NOT NULL DEFAULT 1      COMMENT '类型：1原材料 2半成品 3成品',
  `status`        TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1启用 0停用',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`       TINYINT     NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_material_code` (`material_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料表';

-- BOM 物料清单表
DROP TABLE IF EXISTS `bom`;
CREATE TABLE `bom` (
  `id`                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_material_id` BIGINT        NOT NULL                COMMENT '父物料ID',
  `child_material_id`  BIGINT        NOT NULL                COMMENT '子物料ID',
  `quantity`            DECIMAL(10,3) NOT NULL                COMMENT '用量',
  `unit`                VARCHAR(16)   NOT NULL                COMMENT '单位',
  `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`            TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_material_id`),
  KEY `idx_child` (`child_material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BOM物料清单表';

-- 工作中心表
DROP TABLE IF EXISTS `work_center`;
CREATE TABLE `work_center` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `center_code`     VARCHAR(32)   NOT NULL                COMMENT '中心编码',
  `center_name`     VARCHAR(128)  NOT NULL                COMMENT '中心名称',
  `center_type`     TINYINT       NOT NULL DEFAULT 1      COMMENT '类型：1加工 2装配 3检验 4包装',
  `capacity_per_hour` DECIMAL(10,2) NOT NULL DEFAULT 0    COMMENT '小时产能（件/小时）',
  `status`          TINYINT       NOT NULL DEFAULT 1      COMMENT '状态：1运行中 0停机',
  `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`         TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_center_code` (`center_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作中心表';

-- 工艺路线表
DROP TABLE IF EXISTS `process_route`;
CREATE TABLE `process_route` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `route_code`  VARCHAR(32)  NOT NULL                COMMENT '路线编码',
  `route_name`  VARCHAR(128) NOT NULL                COMMENT '路线名称',
  `description` VARCHAR(256) DEFAULT NULL            COMMENT '描述',
  `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1启用 0停用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT     NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_route_code` (`route_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工艺路线表';

-- 工序表
DROP TABLE IF EXISTS `process_step`;
CREATE TABLE `process_step` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `route_id`      BIGINT        NOT NULL                COMMENT '工艺路线ID',
  `step_no`      INT           NOT NULL                COMMENT '工序序号',
  `step_name`    VARCHAR(128)  NOT NULL                COMMENT '工序名称',
  `work_center_id` BIGINT      NOT NULL                COMMENT '工作中心ID',
  `standard_time` DECIMAL(10,2) NOT NULL DEFAULT 0    COMMENT '标准工时（分钟）',
  `need_qc`      TINYINT       NOT NULL DEFAULT 0      COMMENT '是否需要质检：1是 0否',
  `description`  VARCHAR(256)  DEFAULT NULL            COMMENT '工序描述',
  `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`      TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_route_id` (`route_id`),
  KEY `idx_work_center_id` (`work_center_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工序表';

-- =====================================================
-- 三、业务数据层
-- =====================================================

-- 生产订单表
DROP TABLE IF EXISTS `production_order`;
CREATE TABLE `production_order` (
  `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no`         VARCHAR(32)  NOT NULL                COMMENT '订单编号',
  `product_id`       BIGINT       NOT NULL                COMMENT '产品ID',
  `planned_qty`      INT          NOT NULL                COMMENT '计划数量',
  `completed_qty`    INT          NOT NULL DEFAULT 0      COMMENT '已完成数量',
  `scrap_qty`        INT          NOT NULL DEFAULT 0      COMMENT '不良数量',
  `priority`         TINYINT      NOT NULL DEFAULT 3      COMMENT '优先级：1最高 5最低',
  `status`           TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0已创建 1已下发 2执行中 3已完成 4已关闭',
  `planned_start_time` DATETIME  DEFAULT NULL            COMMENT '计划开始时间',
  `planned_end_time` DATETIME     DEFAULT NULL            COMMENT '计划结束时间',
  `actual_start_time` DATETIME    DEFAULT NULL            COMMENT '实际开始时间',
  `actual_end_time`  DATETIME     DEFAULT NULL            COMMENT '实际结束时间',
  `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`          TINYINT     NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产订单表';

-- 派工单表
DROP TABLE IF EXISTS `dispatch`;
CREATE TABLE `dispatch` (
  `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dispatch_no`      VARCHAR(32)  NOT NULL                COMMENT '派工单号',
  `order_id`         BIGINT       NOT NULL                COMMENT '生产订单ID',
  `step_id`          BIGINT       NOT NULL                COMMENT '工序ID',
  `work_center_id`   BIGINT       NOT NULL                COMMENT '工作中心ID',
  `operator_id`      BIGINT       DEFAULT NULL            COMMENT '派工操作工ID',
  `dispatch_qty`     INT          NOT NULL                COMMENT '派工数量',
  `completed_qty`    INT          NOT NULL DEFAULT 0      COMMENT '已报工合格数量',
  `scrap_qty`        INT          NOT NULL DEFAULT 0      COMMENT '已报工不良数量',
  `status`           TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0待开工 1进行中 2已暂停 3已完成',
  `planned_start_time` DATETIME  DEFAULT NULL            COMMENT '计划开始时间',
  `planned_end_time` DATETIME     DEFAULT NULL            COMMENT '计划结束时间',
  `actual_start_time` DATETIME    DEFAULT NULL            COMMENT '实际开始时间',
  `actual_end_time`  DATETIME     DEFAULT NULL            COMMENT '实际结束时间',
  `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`          TINYINT     NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dispatch_no` (`dispatch_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_work_center_id` (`work_center_id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='派工单表';

-- 报工记录表
DROP TABLE IF EXISTS `work_report`;
CREATE TABLE `work_report` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dispatch_id`  BIGINT       NOT NULL                COMMENT '派工单ID',
  `operator_id`  BIGINT       NOT NULL                COMMENT '报工人ID',
  `good_qty`     INT          NOT NULL                COMMENT '合格数量',
  `scrap_qty`    INT          NOT NULL DEFAULT 0      COMMENT '不良数量',
  `rework_qty`   INT          NOT NULL DEFAULT 0      COMMENT '返修数量',
  `scrap_reason` VARCHAR(64)  DEFAULT NULL            COMMENT '不良原因',
  `report_time`  DATETIME     NOT NULL                COMMENT '报工时间',
  `remark`       TEXT         DEFAULT NULL            COMMENT '备注',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      TINYINT     NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_dispatch_id` (`dispatch_id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_report_time` (`report_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报工记录表';

-- 质检记录表
DROP TABLE IF EXISTS `quality_record`;
CREATE TABLE `quality_record` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dispatch_id`   BIGINT       NOT NULL                COMMENT '派工单ID',
  `inspector_id`  BIGINT       NOT NULL                COMMENT '质检员ID',
  `check_type`    TINYINT      NOT NULL                COMMENT '质检类型：1首检 2巡检 3末检',
  `result`        TINYINT      NOT NULL                COMMENT '检验结果：1合格 2不合格 3让步接收',
  `sample_qty`    INT          NOT NULL                COMMENT '抽检数量',
  `defect_qty`    INT          NOT NULL DEFAULT 0      COMMENT '不良数量',
  `defect_desc`   TEXT         DEFAULT NULL            COMMENT '不良描述',
  `check_time`    DATETIME     NOT NULL                COMMENT '检验时间',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       TINYINT     NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_dispatch_id` (`dispatch_id`),
  KEY `idx_check_type` (`check_type`),
  KEY `idx_check_time` (`check_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质检记录表';

-- 设备表
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `equipment_code` VARCHAR(32)  NOT NULL                COMMENT '设备编码',
  `equipment_name` VARCHAR(128) NOT NULL                COMMENT '设备名称',
  `equipment_type`  TINYINT     NOT NULL DEFAULT 1      COMMENT '设备类型：1CNC 2注塑机 3检测仪 4传输带',
  `work_center_id` BIGINT       DEFAULT NULL            COMMENT '所属工作中心ID',
  `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0空闲 1运行 2停机 3维修',
  `purchase_date`  DATE         DEFAULT NULL            COMMENT '购入日期',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`        TINYINT     NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_equipment_code` (`equipment_code`),
  KEY `idx_work_center_id` (`work_center_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- 设备状态日志表
DROP TABLE IF EXISTS `equipment_status_log`;
CREATE TABLE `equipment_status_log` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `equipment_id` BIGINT       NOT NULL                COMMENT '设备ID',
  `status`       TINYINT      NOT NULL                COMMENT '状态：0空闲 1运行 2停机 3维修',
  `start_time`   DATETIME     NOT NULL                COMMENT '开始时间',
  `end_time`     DATETIME     DEFAULT NULL            COMMENT '结束时间',
  `duration`     INT          DEFAULT NULL            COMMENT '持续时长（分钟）',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_equipment_id` (`equipment_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备状态日志表';

SET FOREIGN_KEY_CHECKS = 1;
