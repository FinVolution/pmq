/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : mq_fail_message_node_01

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2019-10-15 15:16:19
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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';

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
-- Table structure for message_100
-- ----------------------------
DROP TABLE IF EXISTS `message_100`;
CREATE TABLE `message_100` (
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
-- Table structure for message_101
-- ----------------------------
DROP TABLE IF EXISTS `message_101`;
CREATE TABLE `message_101` (
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
-- Table structure for message_102
-- ----------------------------
DROP TABLE IF EXISTS `message_102`;
CREATE TABLE `message_102` (
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
-- Table structure for message_103
-- ----------------------------
DROP TABLE IF EXISTS `message_103`;
CREATE TABLE `message_103` (
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
-- Table structure for message_104
-- ----------------------------
DROP TABLE IF EXISTS `message_104`;
CREATE TABLE `message_104` (
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
-- Table structure for message_105
-- ----------------------------
DROP TABLE IF EXISTS `message_105`;
CREATE TABLE `message_105` (
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
-- Table structure for message_106
-- ----------------------------
DROP TABLE IF EXISTS `message_106`;
CREATE TABLE `message_106` (
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
-- Table structure for message_107
-- ----------------------------
DROP TABLE IF EXISTS `message_107`;
CREATE TABLE `message_107` (
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
-- Table structure for message_108
-- ----------------------------
DROP TABLE IF EXISTS `message_108`;
CREATE TABLE `message_108` (
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
-- Table structure for message_109
-- ----------------------------
DROP TABLE IF EXISTS `message_109`;
CREATE TABLE `message_109` (
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
-- Table structure for message_110
-- ----------------------------
DROP TABLE IF EXISTS `message_110`;
CREATE TABLE `message_110` (
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
-- Table structure for message_111
-- ----------------------------
DROP TABLE IF EXISTS `message_111`;
CREATE TABLE `message_111` (
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
-- Table structure for message_112
-- ----------------------------
DROP TABLE IF EXISTS `message_112`;
CREATE TABLE `message_112` (
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
-- Table structure for message_113
-- ----------------------------
DROP TABLE IF EXISTS `message_113`;
CREATE TABLE `message_113` (
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
-- Table structure for message_114
-- ----------------------------
DROP TABLE IF EXISTS `message_114`;
CREATE TABLE `message_114` (
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
-- Table structure for message_115
-- ----------------------------
DROP TABLE IF EXISTS `message_115`;
CREATE TABLE `message_115` (
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
-- Table structure for message_116
-- ----------------------------
DROP TABLE IF EXISTS `message_116`;
CREATE TABLE `message_116` (
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
-- Table structure for message_117
-- ----------------------------
DROP TABLE IF EXISTS `message_117`;
CREATE TABLE `message_117` (
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
-- Table structure for message_118
-- ----------------------------
DROP TABLE IF EXISTS `message_118`;
CREATE TABLE `message_118` (
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
-- Table structure for message_119
-- ----------------------------
DROP TABLE IF EXISTS `message_119`;
CREATE TABLE `message_119` (
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
-- Table structure for message_120
-- ----------------------------
DROP TABLE IF EXISTS `message_120`;
CREATE TABLE `message_120` (
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
-- Table structure for message_121
-- ----------------------------
DROP TABLE IF EXISTS `message_121`;
CREATE TABLE `message_121` (
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
-- Table structure for message_122
-- ----------------------------
DROP TABLE IF EXISTS `message_122`;
CREATE TABLE `message_122` (
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
-- Table structure for message_123
-- ----------------------------
DROP TABLE IF EXISTS `message_123`;
CREATE TABLE `message_123` (
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
-- Table structure for message_124
-- ----------------------------
DROP TABLE IF EXISTS `message_124`;
CREATE TABLE `message_124` (
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
-- Table structure for message_125
-- ----------------------------
DROP TABLE IF EXISTS `message_125`;
CREATE TABLE `message_125` (
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
-- Table structure for message_126
-- ----------------------------
DROP TABLE IF EXISTS `message_126`;
CREATE TABLE `message_126` (
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
-- Table structure for message_127
-- ----------------------------
DROP TABLE IF EXISTS `message_127`;
CREATE TABLE `message_127` (
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
-- Table structure for message_128
-- ----------------------------
DROP TABLE IF EXISTS `message_128`;
CREATE TABLE `message_128` (
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
-- Table structure for message_129
-- ----------------------------
DROP TABLE IF EXISTS `message_129`;
CREATE TABLE `message_129` (
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
-- Table structure for message_130
-- ----------------------------
DROP TABLE IF EXISTS `message_130`;
CREATE TABLE `message_130` (
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
-- Table structure for message_131
-- ----------------------------
DROP TABLE IF EXISTS `message_131`;
CREATE TABLE `message_131` (
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
-- Table structure for message_132
-- ----------------------------
DROP TABLE IF EXISTS `message_132`;
CREATE TABLE `message_132` (
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
-- Table structure for message_133
-- ----------------------------
DROP TABLE IF EXISTS `message_133`;
CREATE TABLE `message_133` (
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
-- Table structure for message_134
-- ----------------------------
DROP TABLE IF EXISTS `message_134`;
CREATE TABLE `message_134` (
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
-- Table structure for message_135
-- ----------------------------
DROP TABLE IF EXISTS `message_135`;
CREATE TABLE `message_135` (
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
-- Table structure for message_136
-- ----------------------------
DROP TABLE IF EXISTS `message_136`;
CREATE TABLE `message_136` (
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
-- Table structure for message_137
-- ----------------------------
DROP TABLE IF EXISTS `message_137`;
CREATE TABLE `message_137` (
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
-- Table structure for message_138
-- ----------------------------
DROP TABLE IF EXISTS `message_138`;
CREATE TABLE `message_138` (
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
-- Table structure for message_139
-- ----------------------------
DROP TABLE IF EXISTS `message_139`;
CREATE TABLE `message_139` (
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
-- Table structure for message_140
-- ----------------------------
DROP TABLE IF EXISTS `message_140`;
CREATE TABLE `message_140` (
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
-- Table structure for message_141
-- ----------------------------
DROP TABLE IF EXISTS `message_141`;
CREATE TABLE `message_141` (
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
-- Table structure for message_142
-- ----------------------------
DROP TABLE IF EXISTS `message_142`;
CREATE TABLE `message_142` (
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
-- Table structure for message_143
-- ----------------------------
DROP TABLE IF EXISTS `message_143`;
CREATE TABLE `message_143` (
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
-- Table structure for message_144
-- ----------------------------
DROP TABLE IF EXISTS `message_144`;
CREATE TABLE `message_144` (
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
-- Table structure for message_145
-- ----------------------------
DROP TABLE IF EXISTS `message_145`;
CREATE TABLE `message_145` (
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
-- Table structure for message_146
-- ----------------------------
DROP TABLE IF EXISTS `message_146`;
CREATE TABLE `message_146` (
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
-- Table structure for message_147
-- ----------------------------
DROP TABLE IF EXISTS `message_147`;
CREATE TABLE `message_147` (
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
-- Table structure for message_148
-- ----------------------------
DROP TABLE IF EXISTS `message_148`;
CREATE TABLE `message_148` (
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
-- Table structure for message_149
-- ----------------------------
DROP TABLE IF EXISTS `message_149`;
CREATE TABLE `message_149` (
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
-- Table structure for message_150
-- ----------------------------
DROP TABLE IF EXISTS `message_150`;
CREATE TABLE `message_150` (
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
-- Table structure for message_151
-- ----------------------------
DROP TABLE IF EXISTS `message_151`;
CREATE TABLE `message_151` (
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
-- Table structure for message_152
-- ----------------------------
DROP TABLE IF EXISTS `message_152`;
CREATE TABLE `message_152` (
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
-- Table structure for message_153
-- ----------------------------
DROP TABLE IF EXISTS `message_153`;
CREATE TABLE `message_153` (
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
-- Table structure for message_154
-- ----------------------------
DROP TABLE IF EXISTS `message_154`;
CREATE TABLE `message_154` (
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
-- Table structure for message_155
-- ----------------------------
DROP TABLE IF EXISTS `message_155`;
CREATE TABLE `message_155` (
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
-- Table structure for message_156
-- ----------------------------
DROP TABLE IF EXISTS `message_156`;
CREATE TABLE `message_156` (
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
-- Table structure for message_157
-- ----------------------------
DROP TABLE IF EXISTS `message_157`;
CREATE TABLE `message_157` (
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
-- Table structure for message_158
-- ----------------------------
DROP TABLE IF EXISTS `message_158`;
CREATE TABLE `message_158` (
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
-- Table structure for message_159
-- ----------------------------
DROP TABLE IF EXISTS `message_159`;
CREATE TABLE `message_159` (
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
-- Table structure for message_160
-- ----------------------------
DROP TABLE IF EXISTS `message_160`;
CREATE TABLE `message_160` (
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
-- Table structure for message_161
-- ----------------------------
DROP TABLE IF EXISTS `message_161`;
CREATE TABLE `message_161` (
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
-- Table structure for message_162
-- ----------------------------
DROP TABLE IF EXISTS `message_162`;
CREATE TABLE `message_162` (
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
-- Table structure for message_163
-- ----------------------------
DROP TABLE IF EXISTS `message_163`;
CREATE TABLE `message_163` (
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
-- Table structure for message_164
-- ----------------------------
DROP TABLE IF EXISTS `message_164`;
CREATE TABLE `message_164` (
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
-- Table structure for message_165
-- ----------------------------
DROP TABLE IF EXISTS `message_165`;
CREATE TABLE `message_165` (
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
-- Table structure for message_166
-- ----------------------------
DROP TABLE IF EXISTS `message_166`;
CREATE TABLE `message_166` (
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
-- Table structure for message_167
-- ----------------------------
DROP TABLE IF EXISTS `message_167`;
CREATE TABLE `message_167` (
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
-- Table structure for message_168
-- ----------------------------
DROP TABLE IF EXISTS `message_168`;
CREATE TABLE `message_168` (
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
-- Table structure for message_169
-- ----------------------------
DROP TABLE IF EXISTS `message_169`;
CREATE TABLE `message_169` (
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
-- Table structure for message_170
-- ----------------------------
DROP TABLE IF EXISTS `message_170`;
CREATE TABLE `message_170` (
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
-- Table structure for message_171
-- ----------------------------
DROP TABLE IF EXISTS `message_171`;
CREATE TABLE `message_171` (
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
-- Table structure for message_172
-- ----------------------------
DROP TABLE IF EXISTS `message_172`;
CREATE TABLE `message_172` (
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
-- Table structure for message_173
-- ----------------------------
DROP TABLE IF EXISTS `message_173`;
CREATE TABLE `message_173` (
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
-- Table structure for message_174
-- ----------------------------
DROP TABLE IF EXISTS `message_174`;
CREATE TABLE `message_174` (
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
-- Table structure for message_175
-- ----------------------------
DROP TABLE IF EXISTS `message_175`;
CREATE TABLE `message_175` (
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
-- Table structure for message_176
-- ----------------------------
DROP TABLE IF EXISTS `message_176`;
CREATE TABLE `message_176` (
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
-- Table structure for message_177
-- ----------------------------
DROP TABLE IF EXISTS `message_177`;
CREATE TABLE `message_177` (
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
-- Table structure for message_178
-- ----------------------------
DROP TABLE IF EXISTS `message_178`;
CREATE TABLE `message_178` (
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
-- Table structure for message_179
-- ----------------------------
DROP TABLE IF EXISTS `message_179`;
CREATE TABLE `message_179` (
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
-- Table structure for message_180
-- ----------------------------
DROP TABLE IF EXISTS `message_180`;
CREATE TABLE `message_180` (
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
-- Table structure for message_181
-- ----------------------------
DROP TABLE IF EXISTS `message_181`;
CREATE TABLE `message_181` (
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
-- Table structure for message_182
-- ----------------------------
DROP TABLE IF EXISTS `message_182`;
CREATE TABLE `message_182` (
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
-- Table structure for message_183
-- ----------------------------
DROP TABLE IF EXISTS `message_183`;
CREATE TABLE `message_183` (
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
-- Table structure for message_184
-- ----------------------------
DROP TABLE IF EXISTS `message_184`;
CREATE TABLE `message_184` (
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
-- Table structure for message_185
-- ----------------------------
DROP TABLE IF EXISTS `message_185`;
CREATE TABLE `message_185` (
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
-- Table structure for message_186
-- ----------------------------
DROP TABLE IF EXISTS `message_186`;
CREATE TABLE `message_186` (
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
-- Table structure for message_187
-- ----------------------------
DROP TABLE IF EXISTS `message_187`;
CREATE TABLE `message_187` (
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
-- Table structure for message_188
-- ----------------------------
DROP TABLE IF EXISTS `message_188`;
CREATE TABLE `message_188` (
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
-- Table structure for message_189
-- ----------------------------
DROP TABLE IF EXISTS `message_189`;
CREATE TABLE `message_189` (
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
-- Table structure for message_190
-- ----------------------------
DROP TABLE IF EXISTS `message_190`;
CREATE TABLE `message_190` (
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
-- Table structure for message_191
-- ----------------------------
DROP TABLE IF EXISTS `message_191`;
CREATE TABLE `message_191` (
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
-- Table structure for message_192
-- ----------------------------
DROP TABLE IF EXISTS `message_192`;
CREATE TABLE `message_192` (
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
-- Table structure for message_193
-- ----------------------------
DROP TABLE IF EXISTS `message_193`;
CREATE TABLE `message_193` (
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
-- Table structure for message_194
-- ----------------------------
DROP TABLE IF EXISTS `message_194`;
CREATE TABLE `message_194` (
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
-- Table structure for message_195
-- ----------------------------
DROP TABLE IF EXISTS `message_195`;
CREATE TABLE `message_195` (
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
-- Table structure for message_196
-- ----------------------------
DROP TABLE IF EXISTS `message_196`;
CREATE TABLE `message_196` (
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
-- Table structure for message_197
-- ----------------------------
DROP TABLE IF EXISTS `message_197`;
CREATE TABLE `message_197` (
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
-- Table structure for message_198
-- ----------------------------
DROP TABLE IF EXISTS `message_198`;
CREATE TABLE `message_198` (
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
-- Table structure for message_199
-- ----------------------------
DROP TABLE IF EXISTS `message_199`;
CREATE TABLE `message_199` (
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
-- Table structure for message_200
-- ----------------------------
DROP TABLE IF EXISTS `message_200`;
CREATE TABLE `message_200` (
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
