-- MySQL dump 10.13  Distrib 5.7.23, for Linux (x86_64)
--
-- Host: localhost    Database: securitydb
-- ------------------------------------------------------
-- Server version	5.7.23-0ubuntu0.16.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `operationlog`
--

DROP TABLE IF EXISTS `operationlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operationlog` (
  `id` int(65) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `logtype` varchar(255) DEFAULT NULL COMMENT '日志类型',
  `logname` varchar(255) DEFAULT NULL COMMENT '日志名称',
  `userid` int(65) DEFAULT NULL COMMENT '用户id',
  `operationObj` varchar(100) DEFAULT NULL COMMENT '操作对象',
  `classname` varchar(255) DEFAULT NULL COMMENT '类名称',
  `method` text COMMENT '方法名称',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `succeed` varchar(255) DEFAULT NULL COMMENT '是否成功',
  `message` text COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2535 DEFAULT CHARSET=utf8 COMMENT='操作日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `poi`
--

DROP TABLE IF EXISTS `poi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `poi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lng` varchar(255) DEFAULT NULL,
  `lat` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `tel` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `city_code` varchar(20) DEFAULT '110000',
  `insert_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=649359 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_camera`
--

DROP TABLE IF EXISTS `tb_camera`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_camera` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sdk_id` varchar(64) DEFAULT NULL,
  `stream_address` varchar(96) DEFAULT NULL,
  `play_address` varchar(96) DEFAULT NULL,
  `camera_status` int(11) DEFAULT '0' COMMENT '0表示未激活,1表示激活',
  `camera_type` varchar(8) DEFAULT '视频流' COMMENT '0表示视频流,1表示抓拍机',
  `server_label` varchar(32) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL,
  `group_id` int(11) DEFAULT NULL COMMENT '组id',
  `location` varchar(64) DEFAULT NULL,
  `group_name` varchar(32) DEFAULT NULL,
  `remarks` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_camera_sdk_id_uindex` (`sdk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_group_camera`
--

DROP TABLE IF EXISTS `tb_group_camera`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_group_camera` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `group_status` int(11) DEFAULT '0' COMMENT '0表示失效,1表示激活',
  `area` varchar(32) DEFAULT NULL COMMENT '摄像头所属区域',
  `remarks` varchar(64) NOT NULL DEFAULT '',
  `total_number` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_group_person`
--

DROP TABLE IF EXISTS `tb_group_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_group_person` (
  `name` varchar(16) DEFAULT NULL,
  `color_label` varchar(16) DEFAULT NULL,
  `remarks` varchar(64) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `total_number` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8 COMMENT='底库组';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_person`
--

DROP TABLE IF EXISTS `tb_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_person` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `id_number` varchar(64) DEFAULT NULL,
  `sdk_id` varchar(64) DEFAULT NULL,
  `gender` varchar(2) DEFAULT '',
  `CAPTURECAMERAS_OFFLINE` datetime DEFAULT NULL,
  `normalized` varchar(128) DEFAULT NULL,
  `thumbnail` varchar(128) DEFAULT NULL,
  `photo` varchar(128) DEFAULT NULL,
  `remarks` varchar(128) DEFAULT '',
  `group_id` int(11) DEFAULT NULL,
  `group_name` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_person_id_number_uindex` (`id_number`)
) ENGINE=InnoDB AUTO_INCREMENT=757 DEFAULT CHARSET=utf8 COMMENT='静态人脸库';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_user`
--

DROP TABLE IF EXISTS `tb_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_user` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `avatar` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户头像(URL,程序给出默认头像)',
  `account` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `uname` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '用户姓名',
  `upass` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '用户密码',
  `contact` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '联系方式(手机号/邮箱地址)',
  `role` int(11) NOT NULL DEFAULT '0' COMMENT '角色(0-普通用户,1-超级管理员,default=0)',
  `status` int(11) DEFAULT '0' COMMENT '用户状态(0-禁用,1-启用;default=0)',
  `notes` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='记录系统用户信息';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-09-14  9:15:42
