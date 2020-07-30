/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : mq_basic

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2019-10-15 15:15:45
*/
create database mq_basic;
use mq_basic;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for audit_log
-- ----------------------------
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tb_name` varchar(100) NOT NULL COMMENT '名称',
  `ref_id` bigint(20) NOT NULL COMMENT '外键id',
  `content` text NOT NULL COMMENT '内容',
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `tbname_refid_idx` (`tb_name`,`ref_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='主题';

-- ----------------------------
-- Table structure for consumer
-- ----------------------------
DROP TABLE IF EXISTS `consumer`;
CREATE TABLE `consumer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) NOT NULL,
  `name` varchar(50) NOT NULL COMMENT '客户端唯一标识采用ip+进程号',
  `consumer_group_names` text COMMENT '组名称',
  `sdk_version` varchar(50) DEFAULT NULL COMMENT '客户端版本号',
  `lan` varchar(45) DEFAULT NULL COMMENT '客户端编程语言',
  `heart_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `consumer_name_index` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订阅者信息';

-- ----------------------------
-- Table structure for consumer_group
-- ----------------------------
DROP TABLE IF EXISTS `consumer_group`;
CREATE TABLE `consumer_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '订阅者集合名称，唯一',
  `dpt_name` varchar(100) DEFAULT NULL,
  `topic_names` text COMMENT '指的是正常topic的集合',
  `owner_ids` varchar(200) NOT NULL COMMENT '负责人ids',
  `owner_names` varchar(200) NOT NULL COMMENT '负责人名称',
  `alarm_emails` varchar(1000) DEFAULT NULL COMMENT '延迟告警邮件，英文逗号隔开',
  `tels` varchar(1000) DEFAULT NULL COMMENT '手机号码集合，后续可能通过这个发送钉钉消息或者邮件，英文逗号隔开',
  `ip_white_list` varchar(200) DEFAULT NULL COMMENT 'ip白名单，英文逗号隔开',
  `ip_black_list` varchar(200) DEFAULT NULL COMMENT 'ip黑名单，英文逗号隔开',
  `alarm_flag` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否消息堆积告警',
  `trace_flag` tinyint(4) DEFAULT '0' COMMENT '是否开启消息追踪功能，1开启，0不开启',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `rb_version` bigint(20) NOT NULL COMMENT '重平衡版本号，每次发生重平衡的时候版本会进行升级，同时客户端提交的时候需要进行版本比对如果版本比对不成功说明发生重平衡了',
  `meta_version` bigint(20) DEFAULT '0' COMMENT '元数据信息变更版本号',
  `version` bigint(20) DEFAULT '0' COMMENT '为了操作方便，引入一个总的version 版本，eb_version 和meta_version发送变更 都会引发version版本变更，拿到相关信息后自行判断是发生了什么类型版本变化',
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `consumer_count` tinyint(4) DEFAULT '0' COMMENT '消费者机器数量此字段是为了兼容消息2，在mq3中不用此字段。',
  `app_id` varchar(45) DEFAULT NULL COMMENT '应用id 一个消费者组只能属于一个appid，但是一个appid 可以有多个消费者组',
  `consumer_quality` tinyint(4) DEFAULT '0' COMMENT '指定消费者数量，比如consumer_quality为2 但是有3个客户端，此时依然只能有2个能消费',
  `meta_update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '元数据更新时间',
  `mode` int(11) NOT NULL DEFAULT '1' COMMENT '1，为集群模式，2，为广播模式,3，为代理模式',
  `origin_name` varchar(50) DEFAULT NULL COMMENT '原始的消费者组名',
  `sub_env` varchar(45) NOT NULL DEFAULT 'default'  COMMENT '子环境名称',
  `push_flag` int(11) NOT NULL DEFAULT '0' COMMENT '1，表示实时推送，0，表示非实时推送',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_index` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for consumer_group_consumer
-- ----------------------------
DROP TABLE IF EXISTS `consumer_group_consumer`;
CREATE TABLE `consumer_group_consumer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consumer_id` bigint(20) NOT NULL COMMENT '消费者Id',
  `consumer_name` varchar(100) DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `consumer_group_id` bigint(20) NOT NULL COMMENT '组id',
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `consumer_group_consumer_uq` (`consumer_id`,`consumer_group_id`),
  KEY `consumer_group_id_idx` (`consumer_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订阅者信息';

-- ----------------------------
-- Table structure for consumer_group_topic
-- ----------------------------
DROP TABLE IF EXISTS `consumer_group_topic`;
CREATE TABLE `consumer_group_topic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consumer_group_id` bigint(20) NOT NULL,
  `consumer_group_name` varchar(100) NOT NULL,
  `topic_id` bigint(20) DEFAULT NULL,
  `topic_name` varchar(100) NOT NULL COMMENT 'topic name',
  `origin_topic_name` varchar(100) DEFAULT NULL COMMENT '与失败topic对应的topic',
  `topic_type` tinyint(4) DEFAULT '1' COMMENT '1,表示正常队列，2，表示失败队列',
  `retry_count` int(11) DEFAULT '100' COMMENT '每个topic在某个consumergroup底下对应的失败队列尝试的次数',
  `thread_size` int(11) DEFAULT '10',
  `max_lag` int(11) DEFAULT '1000' COMMENT '可以自定义此topic下topic 告警条数',
  `tag` varchar(100) DEFAULT NULL COMMENT '用来做消息tag 过滤,规则是只要包含在消息体tag中就算符合',
  `delay_process_time` int(11) DEFAULT '0' COMMENT '默认为0，秒为单位。延迟处理时间，相对于发送时间的延迟，例如希望发送一条消息后10秒后被订阅，就需要设置该参数为10000。',
  `pull_batch_size` int(11) DEFAULT '50' COMMENT '批量拉取条数',
  `consumer_batch_size` int(11) DEFAULT '1' COMMENT '批量消费条数',
  `max_pull_time` int(11) DEFAULT '5' COMMENT '最大拉取等待时间，单位是秒默认5秒，最小值1秒',
  `alarm_emails` text,
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `meta_update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '元数据更新时间',
  `time_out` int(11) DEFAULT '0' COMMENT '客户端消费超时熔断时间单位秒,0表示不熔断',
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_topic_id_uq` (`consumer_group_id`,`topic_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for db_node
-- ----------------------------
DROP TABLE IF EXISTS `db_node`;
CREATE TABLE `db_node` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) DEFAULT NULL COMMENT '表示数据对应的ip',
  `port` smallint(11) DEFAULT '0',
  `db_name` varchar(100) DEFAULT NULL,
  `db_user_name` varchar(100) DEFAULT NULL,
  `db_pass` varchar(100) DEFAULT NULL,
  `ip_bak` varchar(45) DEFAULT NULL COMMENT '读写分离',
  `port_bak` smallint(11) DEFAULT NULL COMMENT '读写分离',
  `db_user_name_bak` varchar(100) DEFAULT NULL COMMENT '读写分离',
  `db_pass_bak` varchar(100) DEFAULT NULL COMMENT '读写分离',
  `con_str` varchar(500) NOT NULL COMMENT '数据库链接字符串',
  `read_only` tinyint(4) DEFAULT '1' COMMENT '读写状态： 1读写 2只读 3不可读不可写',
  `node_type` tinyint(4) DEFAULT '1' COMMENT '1,表示存储正常队列消息，2，表示存储失败队列消息',
  `normal_flag` tinyint(4) DEFAULT '1' COMMENT '所有topic自动分配的节点 全部分配到普通节点上，只有手动分配的时候可以分配到特殊节点。1，表示普通节点，0，表示特殊节点',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `con_str_UNIQUE` (`con_str`),
  KEY `update_time_idx` (`update_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='数据库节点';

-- ----------------------------
-- Table structure for dic
-- ----------------------------
DROP TABLE IF EXISTS `dic`;
CREATE TABLE `dic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `key1` varchar(50) NOT NULL COMMENT '键',
  `value1` varchar(100) NOT NULL COMMENT '值',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_index` (`key1`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for message_01
-- ----------------------------
DROP TABLE IF EXISTS `message_01`;
CREATE TABLE `message_01` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `busi_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `inserttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for mq_lock
-- ----------------------------
DROP TABLE IF EXISTS `mq_lock`;
CREATE TABLE `mq_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) DEFAULT NULL,
  `key1` varchar(45) DEFAULT NULL COMMENT '需要加锁的key',
  `heart_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_UNIQUE` (`key1`),
  KEY `key_ip_idx` (`key1`,`ip`),
  KEY `key_idx` (`key1`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for notify_message
-- ----------------------------
DROP TABLE IF EXISTS `notify_message`;
CREATE TABLE `notify_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consumer_group_id` bigint(20) NOT NULL,
  `message_type` tinyint(4) DEFAULT NULL COMMENT '1,表示触发重平衡，2，表示同步数据',
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用来记录需要重平衡的consumer_group';

-- ----------------------------
-- Table structure for notify_message_stat
-- ----------------------------
DROP TABLE IF EXISTS `notify_message_stat`;
CREATE TABLE `notify_message_stat` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key1` varchar(45) NOT NULL COMMENT 'notifymessage标识，防止重复插入',
  `notify_message_id` bigint(20) NOT NULL,
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key1_UNIQUE` (`key1`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用来记录需要重平衡的消息id';

-- ----------------------------
-- Table structure for queue
-- ----------------------------
DROP TABLE IF EXISTS `queue`;
CREATE TABLE `queue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'topic id',
  `topic_name` varchar(100) NOT NULL COMMENT 'topic名称',
  `db_node_id` bigint(20) NOT NULL COMMENT '分区id',
  `node_type` tinyint(4) DEFAULT NULL COMMENT '1,表示存储正常队列消息，2，表示存储失败队列消息',
  `ip` varchar(100) DEFAULT NULL,
  `db_name` varchar(100) NOT NULL COMMENT '数据库名称',
  `tb_name` varchar(100) NOT NULL COMMENT '数据库表名',
  `read_only` tinyint(4) NOT NULL DEFAULT '1' COMMENT '读写状态：1读写 2只读',
  `min_id` bigint(20) NOT NULL DEFAULT '0',
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `lock_version` bigint(20) NOT NULL DEFAULT '1',
  `meta_update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '元数据更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `topic_db_uq` (`topic_name`,`tb_name`,`ip`,`db_name`),
  KEY `topic_id_idx` (`topic_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='分区';

-- ----------------------------
-- Table structure for queue_offset
-- ----------------------------
DROP TABLE IF EXISTS `queue_offset`;
CREATE TABLE `queue_offset` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consumer_group_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '订阅者组id',
  `consumer_group_name` varchar(100) NOT NULL COMMENT '订阅者组主键',
  `consumer_name` varchar(100) DEFAULT NULL COMMENT '客户端消费者name',
  `consumer_id` bigint(20) DEFAULT '0',
  `topic_id` bigint(20) NOT NULL COMMENT '主题id',
  `topic_name` varchar(100) NOT NULL COMMENT '主题名称,如果',
  `origin_topic_name` varchar(100) DEFAULT NULL COMMENT '如果是失败队列此字段名称表示原始的topic名称，topic_name为consumer_group_name+原始的topic_name+"_fail"，否则topic_name和origin_topic_name一致',
  `topic_type` tinyint(4) DEFAULT '1' COMMENT '1,表示正常队列，2，表示失败队列',
  `queue_id` bigint(20) NOT NULL COMMENT '分区id',
  `offset` bigint(20) NOT NULL DEFAULT '0' COMMENT '消费者提交的偏移量',
  `start_offset` bigint(20) DEFAULT '0' COMMENT '订阅时的起始偏移量',
  `offset_version` bigint(20) NOT NULL DEFAULT '0' COMMENT '偏移版本号，当手动修改偏移时，会升级版本号，如果客户端提交更新便宜的时候，只能按照版本号相同，偏移量大的值更新',
  `stop_flag` tinyint(4) DEFAULT '0' COMMENT '1,表示客户端此queue停止消费，0，表示正常消费',
  `db_info` varchar(100) DEFAULT NULL COMMENT 'ip+db_name +tb_name',
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `meta_update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '元数据更新时间',
  `origin_consumer_group_name` varchar(50) DEFAULT NULL COMMENT '原始的消费者组名',
  `consumer_group_mode` int(11) NOT NULL DEFAULT '1' COMMENT '1，为集群模式，2，为广播模式,3，为代理模式',
  `sub_env` varchar(45) NOT NULL DEFAULT 'default' COMMENT '子环境名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_group_id_topic_id` (`consumer_group_id`,`topic_id`,`queue_id`),
  KEY `consumer_group_id_idx` (`consumer_group_id`),
  KEY `consumer_idx` (`consumer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='客户端的topic信息';

-- ----------------------------
-- Table structure for server
-- ----------------------------
DROP TABLE IF EXISTS `server`;
CREATE TABLE `server` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) DEFAULT NULL,
  `port` int(11) DEFAULT '0' COMMENT '端口号',
  `heart_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `server_type` tinyint(4) DEFAULT '1' COMMENT '1 表示broker，0 表示portal。当值为0时，这个是用在做批量清理时使用。',
  `status_flag` tinyint(4) DEFAULT '1' COMMENT '1 表示状态为up,0 表示状态为down，此状态在系统灰度平滑发布时使用。默认是1 表示up',
  `server_version` varchar(100) DEFAULT NULL COMMENT 'broker 版本号',
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`), 
  UNIQUE KEY `ip_port_uq` (`ip`,`port`),
  KEY `ip_port_idx` (`ip`,`port`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for topic
-- ----------------------------
DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `origin_name` varchar(100) DEFAULT NULL COMMENT '如果此topic是失败topic则orgin_name为原始的topic，同时name为consumer_group_name+origin_name+"_fail"，否则name和origin_name一致',
  `dpt_name` varchar(45) DEFAULT NULL COMMENT '部门名称',
  `owner_ids` varchar(200) NOT NULL COMMENT '负责人ids',
  `owner_names` varchar(200) NOT NULL COMMENT '负责人名称',
  `emails` varchar(1000) DEFAULT NULL COMMENT '延迟告警邮件',
  `tels` varchar(1000) DEFAULT NULL COMMENT '手机号码集合，后续可能通过这个发送钉钉消息或者邮件',
  `expect_day_count` int(20) NOT NULL COMMENT '预期每天的数据量单位是万',
  `business_type` varchar(200) DEFAULT NULL COMMENT '业务类型',
  `save_day_num` tinyint(4) NOT NULL DEFAULT '7' COMMENT '消息保留天数',
  `remark` varchar(100) NOT NULL COMMENT '备注',
  `token` varchar(45) DEFAULT NULL COMMENT '如果为空表示不需要验证token，否则消息发送需要匹配token，token可以重新生成，直接简单用guid即可',
  `normal_flag` tinyint(4) DEFAULT '1' COMMENT '所有topic自动分配的节点 全部分配到普通节点上，只有手动分配的时候可以分配到特殊节点。1，表示普通节点，0，表示特殊节点',
  `topic_type` tinyint(4) DEFAULT NULL COMMENT '1,表示正常队列，2，表示失败队列',
  `max_lag` int(11) DEFAULT '10000' COMMENT '默认topic 堆积告警条数',
  `consumer_flag` tinyint(4) DEFAULT '1' COMMENT '是否允许所有人消费订阅，1是，0否。如果是0，则只能允许的消费者组才能订阅此topic消息',
  `consumer_group_names` varchar(1000) DEFAULT NULL COMMENT '允许订阅消费者组名称列表逗号隔开',
  `insert_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '操作人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_active` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `meta_update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '元数据更新时间',
  `app_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `name_idx` (`name`),
  KEY `update_time_idex` (`update_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='主题';
