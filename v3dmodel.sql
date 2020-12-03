/*
SQLyog Ultimate v12.5.0 (64 bit)
MySQL - 5.7.25 : Database - v3dmodel
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`v3dmodel` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `v3dmodel`;

/*Table structure for table `tb_comment` */

DROP TABLE IF EXISTS `tb_comment`;

CREATE TABLE `tb_comment` (
  `cid` int(11) NOT NULL AUTO_INCREMENT,
  `text` varchar(200) NOT NULL,
  `sid` varchar(32) NOT NULL,
  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`cid`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

/*Table structure for table `tb_file` */

DROP TABLE IF EXISTS `tb_file`;

CREATE TABLE `tb_file` (
  `fid` varchar(32) NOT NULL,
  `uid` int(11) NOT NULL,
  `sid` varchar(32) NOT NULL,
  `type` varchar(50) NOT NULL,
  `state` int(11) NOT NULL DEFAULT '0' COMMENT '0正常；',
  `uploadTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `path` varchar(200) NOT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`fid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_scheme` */

DROP TABLE IF EXISTS `tb_scheme`;

CREATE TABLE `tb_scheme` (
  `sid` varchar(32) NOT NULL,
  `uid` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `cover` varchar(200) NOT NULL DEFAULT '/img/default.jpg',
  `state` int(11) NOT NULL DEFAULT '0' COMMENT '0正常；1删除',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `data` text,
  `camera` text,
  `share` int(11) NOT NULL DEFAULT '0' COMMENT '0不分享；1无密码分享；2密码分享',
  `password` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`sid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_user` */

DROP TABLE IF EXISTS `tb_user`;

CREATE TABLE `tb_user` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `phoneNumber` varchar(11) NOT NULL,
  `userName` varchar(20) NOT NULL,
  `password` varchar(18) NOT NULL,
  `state` int(11) NOT NULL DEFAULT '0' COMMENT '0正常；',
  `regTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `avatar` varchar(100) NOT NULL DEFAULT 'https://randomuser.me/api/portraits/men/85.jpg',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
