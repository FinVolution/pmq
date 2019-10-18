/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : mq_message_node_01

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2019-10-15 15:16:04
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for message_01
-- ----------------------------
DROP TABLE IF EXISTS `message_01`;
CREATE TABLE `message_01` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_02
-- ----------------------------
DROP TABLE IF EXISTS `message_02`;
CREATE TABLE `message_02` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_03
-- ----------------------------
DROP TABLE IF EXISTS `message_03`;
CREATE TABLE `message_03` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_04
-- ----------------------------
DROP TABLE IF EXISTS `message_04`;
CREATE TABLE `message_04` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_05
-- ----------------------------
DROP TABLE IF EXISTS `message_05`;
CREATE TABLE `message_05` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_06
-- ----------------------------
DROP TABLE IF EXISTS `message_06`;
CREATE TABLE `message_06` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_07
-- ----------------------------
DROP TABLE IF EXISTS `message_07`;
CREATE TABLE `message_07` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_08
-- ----------------------------
DROP TABLE IF EXISTS `message_08`;
CREATE TABLE `message_08` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_09
-- ----------------------------
DROP TABLE IF EXISTS `message_09`;
CREATE TABLE `message_09` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_10
-- ----------------------------
DROP TABLE IF EXISTS `message_10`;
CREATE TABLE `message_10` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_11
-- ----------------------------
DROP TABLE IF EXISTS `message_11`;
CREATE TABLE `message_11` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_12
-- ----------------------------
DROP TABLE IF EXISTS `message_12`;
CREATE TABLE `message_12` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_13
-- ----------------------------
DROP TABLE IF EXISTS `message_13`;
CREATE TABLE `message_13` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_14
-- ----------------------------
DROP TABLE IF EXISTS `message_14`;
CREATE TABLE `message_14` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_15
-- ----------------------------
DROP TABLE IF EXISTS `message_15`;
CREATE TABLE `message_15` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_16
-- ----------------------------
DROP TABLE IF EXISTS `message_16`;
CREATE TABLE `message_16` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_17
-- ----------------------------
DROP TABLE IF EXISTS `message_17`;
CREATE TABLE `message_17` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_18
-- ----------------------------
DROP TABLE IF EXISTS `message_18`;
CREATE TABLE `message_18` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_19
-- ----------------------------
DROP TABLE IF EXISTS `message_19`;
CREATE TABLE `message_19` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_20
-- ----------------------------
DROP TABLE IF EXISTS `message_20`;
CREATE TABLE `message_20` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_21
-- ----------------------------
DROP TABLE IF EXISTS `message_21`;
CREATE TABLE `message_21` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_22
-- ----------------------------
DROP TABLE IF EXISTS `message_22`;
CREATE TABLE `message_22` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_23
-- ----------------------------
DROP TABLE IF EXISTS `message_23`;
CREATE TABLE `message_23` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_24
-- ----------------------------
DROP TABLE IF EXISTS `message_24`;
CREATE TABLE `message_24` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_25
-- ----------------------------
DROP TABLE IF EXISTS `message_25`;
CREATE TABLE `message_25` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_26
-- ----------------------------
DROP TABLE IF EXISTS `message_26`;
CREATE TABLE `message_26` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_27
-- ----------------------------
DROP TABLE IF EXISTS `message_27`;
CREATE TABLE `message_27` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_28
-- ----------------------------
DROP TABLE IF EXISTS `message_28`;
CREATE TABLE `message_28` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_29
-- ----------------------------
DROP TABLE IF EXISTS `message_29`;
CREATE TABLE `message_29` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_30
-- ----------------------------
DROP TABLE IF EXISTS `message_30`;
CREATE TABLE `message_30` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_31
-- ----------------------------
DROP TABLE IF EXISTS `message_31`;
CREATE TABLE `message_31` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_32
-- ----------------------------
DROP TABLE IF EXISTS `message_32`;
CREATE TABLE `message_32` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_33
-- ----------------------------
DROP TABLE IF EXISTS `message_33`;
CREATE TABLE `message_33` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_34
-- ----------------------------
DROP TABLE IF EXISTS `message_34`;
CREATE TABLE `message_34` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_35
-- ----------------------------
DROP TABLE IF EXISTS `message_35`;
CREATE TABLE `message_35` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_36
-- ----------------------------
DROP TABLE IF EXISTS `message_36`;
CREATE TABLE `message_36` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_37
-- ----------------------------
DROP TABLE IF EXISTS `message_37`;
CREATE TABLE `message_37` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_38
-- ----------------------------
DROP TABLE IF EXISTS `message_38`;
CREATE TABLE `message_38` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_39
-- ----------------------------
DROP TABLE IF EXISTS `message_39`;
CREATE TABLE `message_39` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_40
-- ----------------------------
DROP TABLE IF EXISTS `message_40`;
CREATE TABLE `message_40` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_41
-- ----------------------------
DROP TABLE IF EXISTS `message_41`;
CREATE TABLE `message_41` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_42
-- ----------------------------
DROP TABLE IF EXISTS `message_42`;
CREATE TABLE `message_42` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_43
-- ----------------------------
DROP TABLE IF EXISTS `message_43`;
CREATE TABLE `message_43` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_44
-- ----------------------------
DROP TABLE IF EXISTS `message_44`;
CREATE TABLE `message_44` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_45
-- ----------------------------
DROP TABLE IF EXISTS `message_45`;
CREATE TABLE `message_45` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_46
-- ----------------------------
DROP TABLE IF EXISTS `message_46`;
CREATE TABLE `message_46` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_47
-- ----------------------------
DROP TABLE IF EXISTS `message_47`;
CREATE TABLE `message_47` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_48
-- ----------------------------
DROP TABLE IF EXISTS `message_48`;
CREATE TABLE `message_48` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_49
-- ----------------------------
DROP TABLE IF EXISTS `message_49`;
CREATE TABLE `message_49` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_50
-- ----------------------------
DROP TABLE IF EXISTS `message_50`;
CREATE TABLE `message_50` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_51
-- ----------------------------
DROP TABLE IF EXISTS `message_51`;
CREATE TABLE `message_51` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_52
-- ----------------------------
DROP TABLE IF EXISTS `message_52`;
CREATE TABLE `message_52` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_53
-- ----------------------------
DROP TABLE IF EXISTS `message_53`;
CREATE TABLE `message_53` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_54
-- ----------------------------
DROP TABLE IF EXISTS `message_54`;
CREATE TABLE `message_54` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_55
-- ----------------------------
DROP TABLE IF EXISTS `message_55`;
CREATE TABLE `message_55` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_56
-- ----------------------------
DROP TABLE IF EXISTS `message_56`;
CREATE TABLE `message_56` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_57
-- ----------------------------
DROP TABLE IF EXISTS `message_57`;
CREATE TABLE `message_57` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_58
-- ----------------------------
DROP TABLE IF EXISTS `message_58`;
CREATE TABLE `message_58` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_59
-- ----------------------------
DROP TABLE IF EXISTS `message_59`;
CREATE TABLE `message_59` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_60
-- ----------------------------
DROP TABLE IF EXISTS `message_60`;
CREATE TABLE `message_60` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_61
-- ----------------------------
DROP TABLE IF EXISTS `message_61`;
CREATE TABLE `message_61` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_62
-- ----------------------------
DROP TABLE IF EXISTS `message_62`;
CREATE TABLE `message_62` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_63
-- ----------------------------
DROP TABLE IF EXISTS `message_63`;
CREATE TABLE `message_63` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_64
-- ----------------------------
DROP TABLE IF EXISTS `message_64`;
CREATE TABLE `message_64` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_65
-- ----------------------------
DROP TABLE IF EXISTS `message_65`;
CREATE TABLE `message_65` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_66
-- ----------------------------
DROP TABLE IF EXISTS `message_66`;
CREATE TABLE `message_66` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_67
-- ----------------------------
DROP TABLE IF EXISTS `message_67`;
CREATE TABLE `message_67` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_68
-- ----------------------------
DROP TABLE IF EXISTS `message_68`;
CREATE TABLE `message_68` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_69
-- ----------------------------
DROP TABLE IF EXISTS `message_69`;
CREATE TABLE `message_69` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_70
-- ----------------------------
DROP TABLE IF EXISTS `message_70`;
CREATE TABLE `message_70` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_71
-- ----------------------------
DROP TABLE IF EXISTS `message_71`;
CREATE TABLE `message_71` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_72
-- ----------------------------
DROP TABLE IF EXISTS `message_72`;
CREATE TABLE `message_72` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_73
-- ----------------------------
DROP TABLE IF EXISTS `message_73`;
CREATE TABLE `message_73` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_74
-- ----------------------------
DROP TABLE IF EXISTS `message_74`;
CREATE TABLE `message_74` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_75
-- ----------------------------
DROP TABLE IF EXISTS `message_75`;
CREATE TABLE `message_75` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_76
-- ----------------------------
DROP TABLE IF EXISTS `message_76`;
CREATE TABLE `message_76` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_77
-- ----------------------------
DROP TABLE IF EXISTS `message_77`;
CREATE TABLE `message_77` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_78
-- ----------------------------
DROP TABLE IF EXISTS `message_78`;
CREATE TABLE `message_78` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_79
-- ----------------------------
DROP TABLE IF EXISTS `message_79`;
CREATE TABLE `message_79` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_80
-- ----------------------------
DROP TABLE IF EXISTS `message_80`;
CREATE TABLE `message_80` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_81
-- ----------------------------
DROP TABLE IF EXISTS `message_81`;
CREATE TABLE `message_81` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_82
-- ----------------------------
DROP TABLE IF EXISTS `message_82`;
CREATE TABLE `message_82` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_83
-- ----------------------------
DROP TABLE IF EXISTS `message_83`;
CREATE TABLE `message_83` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_84
-- ----------------------------
DROP TABLE IF EXISTS `message_84`;
CREATE TABLE `message_84` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_85
-- ----------------------------
DROP TABLE IF EXISTS `message_85`;
CREATE TABLE `message_85` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_86
-- ----------------------------
DROP TABLE IF EXISTS `message_86`;
CREATE TABLE `message_86` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_87
-- ----------------------------
DROP TABLE IF EXISTS `message_87`;
CREATE TABLE `message_87` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_88
-- ----------------------------
DROP TABLE IF EXISTS `message_88`;
CREATE TABLE `message_88` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_89
-- ----------------------------
DROP TABLE IF EXISTS `message_89`;
CREATE TABLE `message_89` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_90
-- ----------------------------
DROP TABLE IF EXISTS `message_90`;
CREATE TABLE `message_90` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_91
-- ----------------------------
DROP TABLE IF EXISTS `message_91`;
CREATE TABLE `message_91` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_92
-- ----------------------------
DROP TABLE IF EXISTS `message_92`;
CREATE TABLE `message_92` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_93
-- ----------------------------
DROP TABLE IF EXISTS `message_93`;
CREATE TABLE `message_93` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_94
-- ----------------------------
DROP TABLE IF EXISTS `message_94`;
CREATE TABLE `message_94` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_95
-- ----------------------------
DROP TABLE IF EXISTS `message_95`;
CREATE TABLE `message_95` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_96
-- ----------------------------
DROP TABLE IF EXISTS `message_96`;
CREATE TABLE `message_96` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_97
-- ----------------------------
DROP TABLE IF EXISTS `message_97`;
CREATE TABLE `message_97` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_98
-- ----------------------------
DROP TABLE IF EXISTS `message_98`;
CREATE TABLE `message_98` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

-- ----------------------------
-- Table structure for message_99
-- ----------------------------
DROP TABLE IF EXISTS `message_99`;
CREATE TABLE `message_99` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务id',
  `head` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息头',
  `body` text COLLATE utf8mb4_unicode_ci COMMENT '消息体',
  `send_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的ip',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  `retry_count` int(11) DEFAULT '0',
  `trace_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';
