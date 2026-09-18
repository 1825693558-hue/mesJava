-- =====================================================
-- MES 生产执行演示系统 - 初始化数据脚本
-- =====================================================
USE mes_demo;

SET NAMES utf8mb4;

-- =====================================================
-- 一、系统管理数据
-- =====================================================

-- 角色
INSERT INTO `sys_role` (`role_code`, `role_name`, `remark`) VALUES
('ADMIN',     '系统管理员', '全部权限'),
('PLANNER',   '生产计划员', '订单管理+基础数据查看'),
('SUPERVISOR','车间主管',   '车间执行管理'),
('OPERATOR',  '一线操作工', '工序报工'),
('QC',        '质检员',     '质量管理');

-- 用户（密码均为 123456 的 BCrypt 加密）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `department`, `phone`, `status`) VALUES
('admin',     '$2a$10$wM6U2WK1YTXsmU6uznnkNup9saG9s.M04HMibA1Hjwj8CtqyrPNbG', '系统管理员', 'IT部',   '13800000001', 1),
('planner01', '$2a$10$wM6U2WK1YTXsmU6uznnkNup9saG9s.M04HMibA1Hjwj8CtqyrPNbG', '张计划',     'PMC部',  '13800000002', 1),
('supervisor01','$2a$10$wM6U2WK1YTXsmU6uznnkNup9saG9s.M04HMibA1Hjwj8CtqyrPNbG','李主管',    '生产部', '13800000003', 1),
('operator01','$2a$10$wM6U2WK1YTXsmU6uznnkNup9saG9s.M04HMibA1Hjwj8CtqyrPNbG','王工',      '生产部', '13800000004', 1),
('operator02','$2a$10$wM6U2WK1YTXsmU6uznnkNup9saG9s.M04HMibA1Hjwj8CtqyrPNbG','赵工',      '生产部', '13800000005', 1),
('qc01',      '$2a$10$wM6U2WK1YTXsmU6uznnkNup9saG9s.M04HMibA1Hjwj8CtqyrPNbG','钱质检',     '品质部', '13800000006', 1);

-- 用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 4), (6, 5);

-- 权限（菜单+按钮）
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_code`, `permission_name`, `type`, `path`, `component`, `icon`, `sort`, `status`) VALUES
(1,  0, 'system',       '系统管理', 1, '/system',       'Layout',                      'Setting',    90, 1),
(2,  1, 'system:user',  '用户管理', 1, '/system/user',  'system/user/index',           'User',       1,  1),
(3,  2, 'system:user:add','新增用户',2, NULL,            NULL,                           NULL,         1,  1),
(4,  2, 'system:user:edit','编辑用户',2,NULL,           NULL,                           NULL,         2,  1),
(5,  2, 'system:user:del','删除用户',2,NULL,            NULL,                           NULL,         3,  1),
(6,  1, 'system:role',  '角色管理', 1, '/system/role',  'system/role/index',           'UserFilled', 2,  1),
(7,  1, 'system:permission','权限管理',1,'/system/permission','system/permission/index','Key',     3,  1),
(8,  1, 'system:log',   '操作日志', 1, '/system/log',   'system/log/index',            'Document',   4,  1),
(9,  1, 'system:dict',  '数据字典', 1, '/system/dict',  'system/dict/index',           'Collection', 5,  1),

(10, 0, 'base',         '基础数据', 1, '/base',         'Layout',                      'Folder',     10, 1),
(11, 10, 'base:product','产品管理', 1, '/base/product', 'base/product/index',          'Box',        1,  1),
(12, 10, 'base:material','物料管理',1, '/base/material','base/material/index',         'Files',      2,  1),
(13, 10, 'base:bom',    'BOM管理',  1, '/base/bom',     'base/bom/index',              'Connection', 3,  1),
(14, 10, 'base:route',  '工艺路线', 1, '/base/route',   'base/route/index',            'Guide',      4,  1),
(15, 10, 'base:workcenter','工作中心',1,'/base/workcenter','base/workcenter/index',   'HomeFilled', 5,  1),

(16, 0, 'order',        '生产订单', 1, '/order',        'Layout',                      'Document',   20, 1),
(17, 16, 'order:list',  '订单管理', 1, '/order/list',   'order/list/index',            'List',       1,  1),
(18, 16, 'order:progress','订单进度',1,'/order/progress','order/progress/index',       'TrendCharts', 2, 1),

(19, 0, 'execution',    '生产执行', 1, '/execution',    'Layout',                      'Tools',      30, 1),
(20, 19, 'execution:dispatch','派工管理',1,'/execution/dispatch','execution/dispatch/index','Tickets', 1, 1),
(21, 19, 'execution:report','工序报工',1,'/execution/report','execution/report/index','EditPen',   2,  1),

(22, 0, 'quality',      '质量管理', 1, '/quality',      'Layout',                      'Checked',    40, 1),
(23, 22, 'quality:record','质检记录',1,'/quality/record','quality/record/index',       'Document',   1,  1),

(24, 0, 'equipment',    '设备管理', 1, '/equipment',    'Layout',                      'Monitor',    50, 1),
(25, 24, 'equipment:list','设备台账',1,'/equipment/list','equipment/list/index',        'Cpu',        1,  1),

(26, 0, 'dashboard',     '车间看板', 1, '/dashboard',   'Layout',                      'DataLine',   1,  1),
(27, 26, 'dashboard:realtime','实时看板',1,'/dashboard/realtime','dashboard/realtime/index','View',  1, 1),
(28, 26, 'dashboard:report','统计报表',1,'/dashboard/report','dashboard/report/index','Histogram',2, 1);

-- 角色权限关联（ADMIN拥有全部权限，其他角色按需分配）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `sys_permission`;

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(2, 10),(2, 11),(2, 12),(2, 13),(2, 14),(2, 15),(2, 16),(2, 17),(2, 18),(2, 26),(2, 27),(2, 28),
(3, 16),(3, 17),(3, 18),(3, 19),(3, 20),(3, 21),(3, 22),(3, 23),(3, 24),(3, 25),(3, 26),(3, 27),(3, 28),
(4, 19),(4, 20),(4, 21),(4, 26),(4, 27),
(5, 22),(5, 23),(5, 26),(5, 27),(5, 28);

-- 数据字典
INSERT INTO `sys_dict` (`dict_type`, `dict_label`, `dict_value`, `sort`) VALUES
('product_type',    '成品',     '1', 1),
('product_type',    '半成品',   '2', 2),
('material_type',   '原材料',   '1', 1),
('material_type',   '半成品',   '2', 2),
('material_type',   '成品',     '3', 3),
('center_type',     '加工',     '1', 1),
('center_type',     '装配',     '2', 2),
('center_type',     '检验',     '3', 3),
('center_type',     '包装',     '4', 4),
('equipment_type',  'CNC',      '1', 1),
('equipment_type',  '注塑机',   '2', 2),
('equipment_type',  '检测仪',   '3', 3),
('equipment_type',  '传输带',   '4', 4),
('equipment_status','空闲',     '0', 1),
('equipment_status','运行',     '1', 2),
('equipment_status','停机',     '2', 3),
('equipment_status','维修',     '3', 4),
('order_status',    '已创建',   '0', 1),
('order_status',    '已下发',   '1', 2),
('order_status',    '执行中',   '2', 3),
('order_status',    '已完成',   '3', 4),
('order_status',    '已关闭',   '4', 5),
('dispatch_status', '待开工',   '0', 1),
('dispatch_status', '进行中',   '1', 2),
('dispatch_status', '已暂停',   '2', 3),
('dispatch_status', '已完成',   '3', 4),
('check_type',      '首检',     '1', 1),
('check_type',      '巡检',     '2', 2),
('check_type',      '末检',     '3', 3),
('check_result',    '合格',     '1', 1),
('check_result',    '不合格',   '2', 2),
('check_result',    '让步接收', '3', 3),
('scrap_reason',    '尺寸超差', '1', 1),
('scrap_reason',    '外观缺陷', '2', 2),
('scrap_reason',    '功能异常', '3', 3),
('scrap_reason',    '材料缺陷', '4', 4),
('scrap_reason',    '操作失误', '5', 5);

-- =====================================================
-- 二、基础数据
-- =====================================================

-- 工作中心
INSERT INTO `work_center` (`center_code`, `center_name`, `center_type`, `capacity_per_hour`, `status`) VALUES
('WC-01', '浇铸车间',     1, 30, 1),
('WC-02', 'CNC加工中心',   1, 20, 1),
('WC-03', '表面处理车间',   1, 25, 1),
('WC-04', '装配车间',     2, 40, 1),
('WC-05', '质量检验室',     3, 50, 1),
('WC-06', '包装车间',     4, 60, 1);

-- 工艺路线
INSERT INTO `process_route` (`route_code`, `route_name`, `description`) VALUES
('RT-001', '电机外壳标准工艺', '铝锭浇铸→精加工→表面处理→装配→检验→包装'),
('RT-002', '电路板标准工艺',   '贴片→焊接→清洗→测试→装配→包装'),
('RT-003', '金属支架标准工艺', '切割→冲压→焊接→表面处理→检验→包装');

-- 工序（RT-001 电机外壳）
INSERT INTO `process_step` (`route_id`, `step_no`, `step_name`, `work_center_id`, `standard_time`, `need_qc`, `description`) VALUES
(1, 10, '铝锭浇铸',  1, 5.0,  1, '铝锭熔炼浇铸成型'),
(1, 20, 'CNC精加工', 2, 8.0,  1, '数控机床精加工外形'),
(1, 30, '表面处理',  3, 4.0,  0, '阳极氧化处理'),
(1, 40, '装配',      4, 3.0,  0, '安装密封件和螺丝'),
(1, 50, '成品检验',  5, 2.0,  1, '全项质量检验'),
(1, 60, '包装',      6, 1.5,  0, '产品包装入箱');

-- 工序（RT-002 电路板）
INSERT INTO `process_step` (`route_id`, `step_no`, `step_name`, `work_center_id`, `standard_time`, `need_qc`, `description`) VALUES
(2, 10, '贴片',      4, 3.0,  0, 'SMT贴片'),
(2, 20, '焊接',      2, 4.0,  1, '波峰焊接'),
(2, 30, '清洗',      3, 2.0,  0, '清洗助焊剂残留'),
(2, 40, '功能测试',  5, 3.0,  1, '通电功能测试'),
(2, 50, '包装',      6, 1.5,  0, '防静电包装');

-- 工序（RT-003 金属支架）
INSERT INTO `process_step` (`route_id`, `step_no`, `step_name`, `work_center_id`, `standard_time`, `need_qc`, `description`) VALUES
(3, 10, '激光切割',  2, 4.0,  0, '钢板激光切割'),
(3, 20, '冲压成型',  1, 3.0,  1, '冲压弯曲成型'),
(3, 30, '焊接',      4, 5.0,  1, '点焊组装'),
(3, 40, '表面处理',  3, 3.0,  0, '喷涂防锈'),
(3, 50, '检验',      5, 2.0,  1, '尺寸和外观检验'),
(3, 60, '包装',      6, 1.0,  0, '打包入箱');

-- 产品
INSERT INTO `product` (`product_code`, `product_name`, `specification`, `unit`, `product_type`, `route_id`, `status`) VALUES
('P2024-001', '电机外壳A型', '380V/50Hz',  '个', 1, 1, 1),
('P2024-002', '电路板B型',   'PCB-120x80', '块', 1, 2, 1),
('P2024-003', '金属支架C型', '300x200mm',  '件', 1, 3, 1);

-- 物料
INSERT INTO `material` (`material_code`, `material_name`, `specification`, `unit`, `material_type`, `status`) VALUES
('M0001', '铝锭',       'A356',       'kg', 1, 1),
('M0002', '螺栓',       'M8x30',      '个', 1, 1),
('M0003', '密封圈',     'O型40x3.5',  '个', 1, 1),
('M0004', 'PCB空板',    'FR4-120x80', '块', 1, 1),
('M0005', '焊锡丝',     'Sn63/Pb37',  'kg', 1, 1),
('M0006', '钢板',       'Q235-3mm',   'kg', 1, 1);

-- BOM（电机外壳A型 = P2024-001）
-- 注意：product 和 material 是分开的表，BOM 以 material 为基础
-- 这里将产品对应的成品物料也作为 material 处理（简化演示）
INSERT INTO `material` (`material_code`, `material_name`, `specification`, `unit`, `material_type`, `status`) VALUES
('M0007', '电机外壳A型成品', '380V/50Hz',  '个', 3, 1),
('M0008', '电路板B型成品',   'PCB-120x80', '块', 3, 1),
('M0009', '金属支架C型成品', '300x200mm',  '件', 3, 1);

-- BOM 关系
INSERT INTO `bom` (`parent_material_id`, `child_material_id`, `quantity`, `unit`) VALUES
(7, 1, 2.000, 'kg'),
(7, 2, 4.000, '个'),
(7, 3, 2.000, '个'),
(8, 4, 1.000, '块'),
(8, 5, 0.050, 'kg'),
(9, 6, 3.500, 'kg');

-- 设备
INSERT INTO `equipment` (`equipment_code`, `equipment_name`, `equipment_type`, `work_center_id`, `status`, `purchase_date`) VALUES
('EQ-001', 'CNC加工中心-01',  1, 2, 1, '2023-03-15'),
('EQ-002', 'CNC加工中心-02',  1, 2, 0, '2023-03-15'),
('EQ-003', '浇铸炉-01',       2, 1, 1, '2022-06-20'),
('EQ-004', '阳极氧化槽-01',   2, 3, 0, '2022-08-10'),
('EQ-005', 'SMT贴片机-01',    1, 4, 1, '2023-01-25'),
('EQ-006', '波峰焊机-01',     1, 4, 0, '2023-01-25'),
('EQ-007', '激光切割机-01',   1, 2, 1, '2023-05-18'),
('EQ-008', '冲压机-01',       2, 1, 1, '2022-12-03'),
('EQ-009', '三坐标测量仪-01', 3, 5, 0, '2023-04-10'),
('EQ-010', '包装机-01',      4, 6, 0, '2023-02-28');

-- =====================================================
-- 三、业务数据（演示用生产订单和报工记录）
-- =====================================================

-- 生产订单
INSERT INTO `production_order` (`order_no`, `product_id`, `planned_qty`, `completed_qty`, `scrap_qty`, `priority`, `status`, `planned_start_time`, `planned_end_time`, `actual_start_time`, `actual_end_time`) VALUES
('MO20260916001', 1, 100, 100, 2,  1, 3, '2026-09-14 08:00:00', '2026-09-15 18:00:00', '2026-09-14 08:30:00', '2026-09-15 17:20:00'),
('MO20260916002', 2, 200, 120, 3,  2, 2, '2026-09-15 08:00:00', '2026-09-17 18:00:00', '2026-09-15 08:15:00', NULL),
('MO20260916003', 3, 150, 0,   0,  3, 1, '2026-09-16 08:00:00', '2026-09-18 18:00:00', NULL, NULL),
('MO20260916004', 1, 80,  0,   0,  2, 0, '2026-09-17 08:00:00', '2026-09-19 18:00:00', NULL, NULL),
('MO20260916005', 3, 120, 120, 1,  3, 3, '2026-09-13 08:00:00', '2026-09-14 18:00:00', '2026-09-13 09:00:00', '2026-09-14 16:30:00');

-- 派工单（订单1已完成）
INSERT INTO `dispatch` (`dispatch_no`, `order_id`, `step_id`, `work_center_id`, `operator_id`, `dispatch_qty`, `completed_qty`, `scrap_qty`, `status`, `planned_start_time`, `planned_end_time`, `actual_start_time`, `actual_end_time`) VALUES
('D20260916001-10', 1, 1, 1, 4, 100, 100, 1, 3, '2026-09-14 08:00:00', '2026-09-14 12:00:00', '2026-09-14 08:30:00', '2026-09-14 11:45:00'),
('D20260916001-20', 1, 2, 2, 4, 100, 100, 1, 3, '2026-09-14 13:00:00', '2026-09-14 18:00:00', '2026-09-14 13:10:00', '2026-09-14 17:30:00'),
('D20260916001-30', 1, 3, 3, 5, 100, 100, 0, 3, '2026-09-15 08:00:00', '2026-09-15 10:00:00', '2026-09-15 08:05:00', '2026-09-15 09:50:00'),
('D20260916001-40', 1, 4, 4, 5, 100, 100, 0, 3, '2026-09-15 10:00:00', '2026-09-15 12:00:00', '2026-09-15 10:10:00', '2026-09-15 11:50:00'),
('D20260916001-50', 1, 5, 5, 6, 100, 100, 0, 3, '2026-09-15 13:00:00', '2026-09-15 15:00:00', '2026-09-15 13:05:00', '2026-09-15 14:40:00'),
('D20260916001-60', 1, 6, 6, 4, 100, 100, 0, 3, '2026-09-15 15:00:00', '2026-09-15 17:00:00', '2026-09-15 15:10:00', '2026-09-15 16:50:00');

-- 派工单（订单2 执行中）
INSERT INTO `dispatch` (`dispatch_no`, `order_id`, `step_id`, `work_center_id`, `operator_id`, `dispatch_qty`, `completed_qty`, `scrap_qty`, `status`, `planned_start_time`, `planned_end_time`, `actual_start_time`, `actual_end_time`) VALUES
('D20260916002-10', 2, 7,  4, 5, 200, 200, 0, 3, '2026-09-15 08:00:00', '2026-09-15 14:00:00', '2026-09-15 08:15:00', '2026-09-15 13:40:00'),
('D20260916002-20', 2, 8,  2, 4, 200, 120, 3, 1, '2026-09-15 14:00:00', '2026-09-16 18:00:00', '2026-09-15 14:30:00', NULL),
('D20260916002-30', 2, 9,  3, NULL, 200, 0, 0, 0, '2026-09-16 08:00:00', '2026-09-16 14:00:00', NULL, NULL),
('D20260916002-40', 2, 10, 5, NULL, 200, 0, 0, 0, '2026-09-16 14:00:00', '2026-09-17 12:00:00', NULL, NULL),
('D20260916002-50', 2, 11, 6, NULL, 200, 0, 0, 0, '2026-09-17 13:00:00', '2026-09-17 18:00:00', NULL, NULL);

-- 派工单（订单3 已下发）
INSERT INTO `dispatch` (`dispatch_no`, `order_id`, `step_id`, `work_center_id`, `operator_id`, `dispatch_qty`, `completed_qty`, `scrap_qty`, `status`, `planned_start_time`, `planned_end_time`) VALUES
('D20260916003-10', 3, 13, 2, 4, 150, 0, 0, 0, '2026-09-16 08:00:00', '2026-09-16 12:00:00'),
('D20260916003-20', 3, 14, 1, 5, 150, 0, 0, 0, '2026-09-16 13:00:00', '2026-09-16 17:00:00'),
('D20260916003-30', 3, 15, 4, NULL, 150, 0, 0, 0, '2026-09-17 08:00:00', '2026-09-17 14:00:00'),
('D20260916003-40', 3, 16, 3, NULL, 150, 0, 0, 0, '2026-09-17 14:00:00', '2026-09-17 18:00:00'),
('D20260916003-50', 3, 17, 5, NULL, 150, 0, 0, 0, '2026-09-18 08:00:00', '2026-09-18 10:00:00'),
('D20260916003-60', 3, 18, 6, NULL, 150, 0, 0, 0, '2026-09-18 10:00:00', '2026-09-18 14:00:00');

-- 报工记录（订单1）
INSERT INTO `work_report` (`dispatch_id`, `operator_id`, `good_qty`, `scrap_qty`, `rework_qty`, `scrap_reason`, `report_time`, `remark`) VALUES
(1, 4, 50, 1, 0, '1', '2026-09-14 10:00:00', '第1批，铝锭温度偏高导致1件变形'),
(1, 4, 50, 0, 0, NULL,'2026-09-14 11:45:00', '第2批，正常'),
(2, 4, 50, 1, 0, '1', '2026-09-14 15:00:00', '第1批，1件尺寸超差'),
(2, 4, 50, 0, 0, NULL,'2026-09-14 17:30:00', '第2批，正常'),
(3, 5, 100,0, 0, NULL,'2026-09-15 09:50:00', '表面处理全部合格'),
(4, 5, 100,0, 0, NULL,'2026-09-15 11:50:00', '装配完成'),
(5, 6, 100,0, 0, NULL,'2026-09-15 14:40:00', '全项检验合格'),
(6, 4, 100,0, 0, NULL,'2026-09-15 16:50:00', '包装完成');

-- 报工记录（订单2 执行中）
INSERT INTO `work_report` (`dispatch_id`, `operator_id`, `good_qty`, `scrap_qty`, `rework_qty`, `scrap_reason`, `report_time`, `remark`) VALUES
(7, 5, 200, 0, 0, NULL, '2026-09-15 13:40:00', '贴片全部完成'),
(8, 4, 60,  1, 0, '2', '2026-09-15 16:00:00', '第1批，1件外观缺陷'),
(8, 4, 60,  2, 0, '3', '2026-09-16 09:00:00', '第2批，2件功能异常');

-- 质检记录
INSERT INTO `quality_record` (`dispatch_id`, `inspector_id`, `check_type`, `result`, `sample_qty`, `defect_qty`, `defect_desc`, `check_time`) VALUES
(1, 6, 1, 1, 5,  1, '1件浇铸气泡',           '2026-09-14 10:30:00'),
(1, 6, 2, 1, 3,  0, NULL,                     '2026-09-14 11:00:00'),
(2, 6, 1, 1, 5,  1, '1件尺寸超差',           '2026-09-14 15:30:00'),
(2, 6, 3, 1, 10, 0, NULL,                     '2026-09-14 17:45:00'),
(5, 6, 3, 1, 20, 0, NULL,                     '2026-09-15 14:45:00'),
(7, 6, 1, 1, 10, 0, NULL,                     '2026-09-15 14:00:00'),
(8, 6, 1, 2, 5,  3, '3件功能异常',           '2026-09-15 16:30:00');

-- 设备状态日志
INSERT INTO `equipment_status_log` (`equipment_id`, `status`, `start_time`, `end_time`, `duration`) VALUES
(1, 1, '2026-09-16 08:00:00', '2026-09-16 12:00:00', 240),
(1, 0, '2026-09-16 12:00:00', '2026-09-16 13:00:00', 60),
(1, 1, '2026-09-16 13:00:00', NULL, NULL),
(2, 1, '2026-09-16 08:00:00', '2026-09-16 10:30:00', 150),
(2, 2, '2026-09-16 10:30:00', '2026-09-16 11:00:00', 30),
(2, 1, '2026-09-16 11:00:00', NULL, NULL),
(3, 1, '2026-09-16 08:00:00', NULL, NULL),
(5, 1, '2026-09-16 08:00:00', '2026-09-16 12:00:00', 240),
(5, 0, '2026-09-16 12:00:00', NULL, NULL),
(7, 1, '2026-09-16 08:00:00', NULL, NULL),
(8, 1, '2026-09-16 08:00:00', NULL, NULL);
