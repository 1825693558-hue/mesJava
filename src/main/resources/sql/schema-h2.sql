-- H2-compatible schema (MODE=MySQL)
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  username    VARCHAR(64)  NOT NULL,
  password    VARCHAR(128) NOT NULL,
  real_name   VARCHAR(64),
  department  VARCHAR(128),
  phone       VARCHAR(20),
  status      TINYINT      NOT NULL DEFAULT 1,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT uk_username UNIQUE (username)
);

DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  role_code   VARCHAR(64)  NOT NULL,
  role_name   VARCHAR(128) NOT NULL,
  remark      VARCHAR(256),
  status      TINYINT      NOT NULL DEFAULT 1,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT uk_role_code UNIQUE (role_code)
);

DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
  id       BIGINT NOT NULL AUTO_INCREMENT,
  user_id  BIGINT NOT NULL,
  role_id  BIGINT NOT NULL,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
  id              BIGINT       NOT NULL AUTO_INCREMENT,
  parent_id       BIGINT       NOT NULL DEFAULT 0,
  permission_code VARCHAR(64)  NOT NULL,
  permission_name VARCHAR(128) NOT NULL,
  type            TINYINT      NOT NULL,
  path            VARCHAR(255),
  component       VARCHAR(255),
  icon            VARCHAR(64),
  sort            INT          NOT NULL DEFAULT 0,
  status          TINYINT      NOT NULL DEFAULT 1,
  create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted         TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT uk_perm_code UNIQUE (permission_code)
);

DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  role_id       BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  user_id     BIGINT,
  username    VARCHAR(64),
  module      VARCHAR(64),
  operation   VARCHAR(128),
  method      VARCHAR(16),
  params      TEXT,
  ip          VARCHAR(64),
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  dict_type   VARCHAR(64)  NOT NULL,
  dict_label  VARCHAR(128) NOT NULL,
  dict_value  VARCHAR(128) NOT NULL,
  sort        INT          NOT NULL DEFAULT 0,
  status      TINYINT      NOT NULL DEFAULT 1,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS product;
CREATE TABLE product (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  product_code  VARCHAR(32)  NOT NULL,
  product_name  VARCHAR(128) NOT NULL,
  specification VARCHAR(128),
  unit          VARCHAR(16)  NOT NULL,
  product_type  TINYINT      NOT NULL DEFAULT 1,
  route_id      BIGINT,
  status        TINYINT      NOT NULL DEFAULT 1,
  create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted       TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT uk_product_code UNIQUE (product_code)
);

DROP TABLE IF EXISTS material;
CREATE TABLE material (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  material_code VARCHAR(32)  NOT NULL,
  material_name VARCHAR(128) NOT NULL,
  specification VARCHAR(128),
  unit          VARCHAR(16)  NOT NULL,
  material_type TINYINT      NOT NULL DEFAULT 1,
  status        TINYINT      NOT NULL DEFAULT 1,
  create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted       TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT uk_material_code UNIQUE (material_code)
);

DROP TABLE IF EXISTS bom;
CREATE TABLE bom (
  id                  BIGINT        NOT NULL AUTO_INCREMENT,
  parent_material_id  BIGINT        NOT NULL,
  child_material_id   BIGINT        NOT NULL,
  quantity            DECIMAL(10,3) NOT NULL,
  unit                VARCHAR(16)   NOT NULL,
  create_time         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted             TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS work_center;
CREATE TABLE work_center (
  id               BIGINT        NOT NULL AUTO_INCREMENT,
  center_code      VARCHAR(32)   NOT NULL,
  center_name      VARCHAR(128)  NOT NULL,
  center_type      TINYINT       NOT NULL DEFAULT 1,
  capacity_per_hour DECIMAL(10,2) NOT NULL DEFAULT 0,
  status           TINYINT       NOT NULL DEFAULT 1,
  create_time      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted          TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT uk_center_code UNIQUE (center_code)
);

DROP TABLE IF EXISTS process_route;
CREATE TABLE process_route (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  route_code  VARCHAR(32)  NOT NULL,
  route_name  VARCHAR(128) NOT NULL,
  description VARCHAR(256),
  status      TINYINT      NOT NULL DEFAULT 1,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted     TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT uk_route_code UNIQUE (route_code)
);

DROP TABLE IF EXISTS process_step;
CREATE TABLE process_step (
  id             BIGINT        NOT NULL AUTO_INCREMENT,
  route_id       BIGINT        NOT NULL,
  step_no        INT           NOT NULL,
  step_name      VARCHAR(128)  NOT NULL,
  work_center_id BIGINT        NOT NULL,
  standard_time  DECIMAL(10,2) NOT NULL DEFAULT 0,
  need_qc        TINYINT       NOT NULL DEFAULT 0,
  description    VARCHAR(256),
  create_time    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted        TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS production_order;
CREATE TABLE production_order (
  id                 BIGINT       NOT NULL AUTO_INCREMENT,
  order_no           VARCHAR(32)  NOT NULL,
  product_id         BIGINT       NOT NULL,
  planned_qty        INT          NOT NULL,
  completed_qty      INT          NOT NULL DEFAULT 0,
  scrap_qty          INT          NOT NULL DEFAULT 0,
  priority           TINYINT      NOT NULL DEFAULT 3,
  status             TINYINT      NOT NULL DEFAULT 0,
  planned_start_time TIMESTAMP,
  planned_end_time   TIMESTAMP,
  actual_start_time  TIMESTAMP,
  actual_end_time    TIMESTAMP,
  create_time        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted            TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT uk_order_no UNIQUE (order_no)
);

DROP TABLE IF EXISTS dispatch;
CREATE TABLE dispatch (
  id                BIGINT       NOT NULL AUTO_INCREMENT,
  dispatch_no       VARCHAR(32)  NOT NULL,
  order_id          BIGINT       NOT NULL,
  step_id           BIGINT       NOT NULL,
  work_center_id    BIGINT       NOT NULL,
  operator_id       BIGINT,
  dispatch_qty      INT          NOT NULL,
  completed_qty     INT          NOT NULL DEFAULT 0,
  scrap_qty         INT          NOT NULL DEFAULT 0,
  status            TINYINT      NOT NULL DEFAULT 0,
  planned_start_time TIMESTAMP,
  planned_end_time   TIMESTAMP,
  actual_start_time  TIMESTAMP,
  actual_end_time    TIMESTAMP,
  create_time        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted            TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT uk_dispatch_no UNIQUE (dispatch_no)
);

DROP TABLE IF EXISTS work_report;
CREATE TABLE work_report (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  dispatch_id  BIGINT       NOT NULL,
  operator_id  BIGINT       NOT NULL,
  good_qty     INT          NOT NULL,
  scrap_qty    INT          NOT NULL DEFAULT 0,
  rework_qty   INT          NOT NULL DEFAULT 0,
  scrap_reason VARCHAR(64),
  report_time  TIMESTAMP    NOT NULL,
  remark       TEXT,
  create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted      TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS quality_record;
CREATE TABLE quality_record (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  dispatch_id   BIGINT       NOT NULL,
  inspector_id  BIGINT       NOT NULL,
  check_type    TINYINT      NOT NULL,
  result        TINYINT      NOT NULL,
  sample_qty    INT          NOT NULL,
  defect_qty    INT          NOT NULL DEFAULT 0,
  defect_desc   TEXT,
  check_time    TIMESTAMP    NOT NULL,
  create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted       TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS equipment;
CREATE TABLE equipment (
  id             BIGINT       NOT NULL AUTO_INCREMENT,
  equipment_code VARCHAR(32)  NOT NULL,
  equipment_name VARCHAR(128) NOT NULL,
  equipment_type TINYINT      NOT NULL DEFAULT 1,
  work_center_id BIGINT,
  status         TINYINT      NOT NULL DEFAULT 0,
  purchase_date  DATE,
  create_time    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted        TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT uk_eq_code UNIQUE (equipment_code)
);

DROP TABLE IF EXISTS equipment_status_log;
CREATE TABLE equipment_status_log (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  equipment_id BIGINT       NOT NULL,
  status       TINYINT      NOT NULL,
  start_time   TIMESTAMP    NOT NULL,
  end_time     TIMESTAMP,
  duration     INT,
  create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

SET FOREIGN_KEY_CHECKS = 1;
