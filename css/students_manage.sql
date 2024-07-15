/*
 Navicat Premium Data Transfer

 Source Server         : greatsql
 Source Server Type    : MySQL
 Source Server Version : 80032
 Source Host           : 192.168.188.128:3306
 Source Schema         : students_manage

 Target Server Type    : MySQL
 Target Server Version : 80032
 File Encoding         : 65001

 Date: 11/07/2024 21:07:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for 作息表
-- ----------------------------
DROP TABLE IF EXISTS `作息表`;
CREATE TABLE `作息表`  (
  `节次` smallint NOT NULL,
  `首节开始时间` time NULL DEFAULT NULL,
  `首节结束时间` time NULL DEFAULT NULL,
  `次节开始时间` time NULL DEFAULT NULL,
  `次节结束时间` time NULL DEFAULT NULL,
  PRIMARY KEY (`节次`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 告知
-- ----------------------------
DROP TABLE IF EXISTS `告知`;
CREATE TABLE `告知`  (
  `被告知学号` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `预警编号` int NOT NULL,
  PRIMARY KEY (`被告知学号`, `预警编号`) USING BTREE,
  INDEX `预警编号`(`预警编号` ASC) USING BTREE,
  CONSTRAINT `告知_ibfk_1` FOREIGN KEY (`被告知学号`) REFERENCES `学生` (`学号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `告知_ibfk_2` FOREIGN KEY (`预警编号`) REFERENCES `预警` (`编号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 周数
-- ----------------------------
DROP TABLE IF EXISTS `周数`;
CREATE TABLE `周数`  (
  `周数` smallint NOT NULL,
  `编号` int NOT NULL,
  PRIMARY KEY (`周数`, `编号`) USING BTREE,
  INDEX `编号`(`编号` ASC) USING BTREE,
  CONSTRAINT `周数_ibfk_1` FOREIGN KEY (`编号`) REFERENCES `时间` (`编号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 学习
-- ----------------------------
DROP TABLE IF EXISTS `学习`;
CREATE TABLE `学习`  (
  `学号` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `课程号` int NOT NULL,
  `类型` enum('必修','选修') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '必修',
  `学期` smallint NULL DEFAULT NULL,
  `成绩` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `补考成绩` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `重修成绩` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`课程号`, `学号`) USING BTREE,
  INDEX `成绩_ibfk_2`(`学号` ASC) USING BTREE,
  CONSTRAINT `学习_ibfk_2` FOREIGN KEY (`学号`) REFERENCES `学生` (`学号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `学习_ibfk_3` FOREIGN KEY (`课程号`) REFERENCES `课程` (`课程号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 学生
-- ----------------------------
DROP TABLE IF EXISTS `学生`;
CREATE TABLE `学生`  (
  `学号` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `姓名` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `性别` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `班级` int NULL DEFAULT NULL,
  `宿舍` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `出生日期` date NULL DEFAULT NULL,
  PRIMARY KEY (`学号`) USING BTREE,
  INDEX `学生_ibfk_1`(`宿舍` ASC) USING BTREE,
  INDEX `班级`(`班级` ASC) USING BTREE,
  CONSTRAINT `学生_ibfk_1` FOREIGN KEY (`宿舍`) REFERENCES `宿舍` (`代码`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `学生_ibfk_3` FOREIGN KEY (`班级`) REFERENCES `班级` (`编号`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 学生上课
-- ----------------------------
DROP TABLE IF EXISTS `学生上课`;
CREATE TABLE `学生上课`  (
  `时间` int NOT NULL,
  `学号` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `教室号` int NOT NULL,
  INDEX `学号`(`学号` ASC) USING BTREE,
  INDEX `教室号`(`教室号` ASC) USING BTREE,
  INDEX `节次`(`时间` ASC) USING BTREE,
  CONSTRAINT `学生上课_ibfk_1` FOREIGN KEY (`学号`) REFERENCES `学生` (`学号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `学生上课_ibfk_3` FOREIGN KEY (`时间`) REFERENCES `时间` (`编号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `学生上课_ibfk_5` FOREIGN KEY (`教室号`) REFERENCES `教室` (`编号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 学生帐密
-- ----------------------------
DROP TABLE IF EXISTS `学生帐密`;
CREATE TABLE `学生帐密`  (
  `账号` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `密码` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `身份` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`账号`) USING BTREE,
  CONSTRAINT `学生帐密_ibfk_1` FOREIGN KEY (`账号`) REFERENCES `学生` (`学号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 学生接收
-- ----------------------------
DROP TABLE IF EXISTS `学生接收`;
CREATE TABLE `学生接收`  (
  `接收人学号` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `通知编号` int NOT NULL,
  PRIMARY KEY (`接收人学号`, `通知编号`) USING BTREE,
  INDEX `通知编号`(`通知编号` ASC) USING BTREE,
  CONSTRAINT `学生接收_ibfk_1` FOREIGN KEY (`接收人学号`) REFERENCES `学生` (`学号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `学生接收_ibfk_2` FOREIGN KEY (`通知编号`) REFERENCES `通知` (`编号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 宿舍
-- ----------------------------
DROP TABLE IF EXISTS `宿舍`;
CREATE TABLE `宿舍`  (
  `代码` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '园区首字母+楼号+单元号(如果有)+层数+户号(如果有)+宿舍（1-4=1，5-8=2，9-14=3）',
  `楼号` smallint NULL DEFAULT NULL COMMENT '荷园13栋楼，柳园24栋楼，菊园6栋楼，松园21栋楼',
  `单元` enum('A','B','C') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `层数` smallint NULL DEFAULT NULL COMMENT '所有园区除南宛学生公寓均不超过6层，最高8层',
  `户号` smallint NULL DEFAULT NULL,
  `房间号` smallint NULL DEFAULT NULL,
  `人数规模` smallint NULL DEFAULT NULL,
  `实际居住` smallint NULL DEFAULT NULL,
  PRIMARY KEY (`代码`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 开设
-- ----------------------------
DROP TABLE IF EXISTS `开设`;
CREATE TABLE `开设`  (
  `时间` int NOT NULL,
  `教室编号` int NOT NULL,
  `课程号` int NULL DEFAULT NULL,
  PRIMARY KEY (`时间`, `教室编号`) USING BTREE,
  INDEX `教室编号`(`教室编号` ASC) USING BTREE,
  INDEX `课程号`(`课程号` ASC) USING BTREE,
  INDEX `节次`(`时间` ASC) USING BTREE,
  CONSTRAINT `开设_ibfk_1` FOREIGN KEY (`教室编号`) REFERENCES `教室` (`编号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `开设_ibfk_3` FOREIGN KEY (`时间`) REFERENCES `时间` (`编号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `开设_ibfk_4` FOREIGN KEY (`课程号`) REFERENCES `课程` (`课程号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 教室
-- ----------------------------
DROP TABLE IF EXISTS `教室`;
CREATE TABLE `教室`  (
  `编号` int NOT NULL AUTO_INCREMENT,
  `教学区` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `教学楼` smallint NULL DEFAULT NULL,
  `楼层` smallint NULL DEFAULT NULL,
  `教室` smallint NULL DEFAULT NULL,
  `教室类型` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `教室容量` smallint NULL DEFAULT NULL,
  PRIMARY KEY (`编号`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 时间
-- ----------------------------
DROP TABLE IF EXISTS `时间`;
CREATE TABLE `时间`  (
  `编号` int NOT NULL AUTO_INCREMENT,
  `星期` enum('星期一','星期二','星期三','星期四','星期五','星期六','星期日') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `节次` smallint NULL DEFAULT NULL,
  PRIMARY KEY (`编号`) USING BTREE,
  INDEX `节次`(`节次` ASC) USING BTREE,
  CONSTRAINT `时间_ibfk_1` FOREIGN KEY (`节次`) REFERENCES `作息表` (`节次`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 月份
-- ----------------------------
DROP TABLE IF EXISTS `月份`;
CREATE TABLE `月份`  (
  `月序` smallint NOT NULL,
  `月数` smallint NULL DEFAULT NULL,
  PRIMARY KEY (`月序`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 班级
-- ----------------------------
DROP TABLE IF EXISTS `班级`;
CREATE TABLE `班级`  (
  `编号` int NOT NULL AUTO_INCREMENT,
  `院系` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `专业` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `班级` smallint NULL DEFAULT NULL,
  PRIMARY KEY (`编号`) USING BTREE,
  INDEX `院系`(`院系` ASC) USING BTREE,
  CONSTRAINT `班级_ibfk_1` FOREIGN KEY (`院系`) REFERENCES `院系` (`院系ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 327 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 班级上课
-- ----------------------------
DROP TABLE IF EXISTS `班级上课`;
CREATE TABLE `班级上课`  (
  `时间` int NOT NULL,
  `班级号` int NOT NULL,
  `教室号` int NULL DEFAULT NULL,
  PRIMARY KEY (`时间`, `班级号`) USING BTREE,
  INDEX `教室号`(`教室号` ASC) USING BTREE,
  INDEX `节次`(`时间` ASC) USING BTREE,
  INDEX `上课_ibfk_4`(`班级号` ASC) USING BTREE,
  CONSTRAINT `班级上课_ibfk_2` FOREIGN KEY (`时间`) REFERENCES `时间` (`编号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `班级上课_ibfk_3` FOREIGN KEY (`班级号`) REFERENCES `班级` (`编号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `班级上课_ibfk_4` FOREIGN KEY (`教室号`) REFERENCES `教室` (`编号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 班级接收
-- ----------------------------
DROP TABLE IF EXISTS `班级接收`;
CREATE TABLE `班级接收`  (
  `接收班级` int NOT NULL,
  `通知编号` int NOT NULL,
  PRIMARY KEY (`接收班级`, `通知编号`) USING BTREE,
  INDEX `通知编号`(`通知编号` ASC) USING BTREE,
  CONSTRAINT `班级接收_ibfk_1` FOREIGN KEY (`接收班级`) REFERENCES `班级` (`编号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `班级接收_ibfk_2` FOREIGN KEY (`通知编号`) REFERENCES `通知` (`编号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 考勤
-- ----------------------------
DROP TABLE IF EXISTS `考勤`;
CREATE TABLE `考勤`  (
  `月序` smallint NOT NULL,
  `学号` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `请假天数` smallint NULL DEFAULT NULL,
  `迟到次数` smallint NULL DEFAULT NULL,
  `早退次数` smallint NULL DEFAULT NULL,
  `旷课学时` smallint NULL DEFAULT NULL,
  PRIMARY KEY (`学号`, `月序`) USING BTREE,
  INDEX `月序`(`月序` ASC) USING BTREE,
  CONSTRAINT `考勤_ibfk_1` FOREIGN KEY (`月序`) REFERENCES `月份` (`月序`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `考勤_ibfk_2` FOREIGN KEY (`学号`) REFERENCES `学生` (`学号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 联系方式
-- ----------------------------
DROP TABLE IF EXISTS `联系方式`;
CREATE TABLE `联系方式`  (
  `联系方式` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `学号` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`联系方式`) USING BTREE,
  INDEX `联系方式_ibfk_1`(`学号` ASC) USING BTREE,
  CONSTRAINT `联系方式_ibfk_1` FOREIGN KEY (`学号`) REFERENCES `学生` (`学号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 课程
-- ----------------------------
DROP TABLE IF EXISTS `课程`;
CREATE TABLE `课程`  (
  `课程号` int NOT NULL AUTO_INCREMENT,
  `课程名` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `开设院系` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `学分` smallint NULL DEFAULT NULL,
  `学时` smallint NULL DEFAULT NULL,
  `课时类型` enum('整节','上半节','下半节') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '整节' COMMENT 'null整节/1上半节/2下半节',
  PRIMARY KEY (`课程号`) USING BTREE,
  INDEX `开设院系`(`开设院系` ASC) USING BTREE,
  CONSTRAINT `课程_ibfk_1` FOREIGN KEY (`开设院系`) REFERENCES `院系` (`院系ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `课程_chk_1` CHECK (`学时` > 0),
  CONSTRAINT `课程_chk_2` CHECK (`学分` > 0)
) ENGINE = InnoDB AUTO_INCREMENT = 280 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 通知
-- ----------------------------
DROP TABLE IF EXISTS `通知`;
CREATE TABLE `通知`  (
  `编号` int NOT NULL AUTO_INCREMENT,
  `标题` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `发布人` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `发布时间` date NULL DEFAULT NULL,
  `内容` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`编号`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 院系
-- ----------------------------
DROP TABLE IF EXISTS `院系`;
CREATE TABLE `院系`  (
  `院系ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `院系全称` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `缩写全称` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`院系ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for 预警
-- ----------------------------
DROP TABLE IF EXISTS `预警`;
CREATE TABLE `预警`  (
  `编号` int NOT NULL AUTO_INCREMENT,
  `类型` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `日期` date NULL DEFAULT NULL,
  `描述` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `状态` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`编号`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- View structure for 学习视图
-- ----------------------------
DROP VIEW IF EXISTS `学习视图`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `学习视图` AS select `学生`.`姓名` AS `姓名`,`课程`.`课程名` AS `课程名`,`学习`.`类型` AS `类型`,`学习`.`学期` AS `学期`,`学习`.`成绩` AS `成绩` from ((`学习` join `课程` on((`学习`.`课程号` = `课程`.`课程号`))) join `学生` on((`学习`.`学号` = `学生`.`学号`)));

-- ----------------------------
-- View structure for 学生上课视图
-- ----------------------------
DROP VIEW IF EXISTS `学生上课视图`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `学生上课视图` AS select `学生`.`学号` AS `学号`,`学生`.`姓名` AS `姓名`,`班级上课`.`时间` AS `时间`,`班级上课`.`教室号` AS `教室号` from ((`学生` join `班级` on((`学生`.`班级` = `班级`.`编号`))) join `班级上课` on((`班级上课`.`班级号` = `班级`.`编号`)));

-- ----------------------------
-- View structure for 学生视图
-- ----------------------------
DROP VIEW IF EXISTS `学生视图`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `学生视图` AS select `学生`.`学号` AS `学号`,`学生`.`姓名` AS `姓名`,`学生`.`性别` AS `性别`,`院系`.`院系全称` AS `院系`,`班级`.`专业` AS `专业`,`班级`.`班级` AS `班级`,concat(substr(`学生`.`学号`,1,4),'-09-01') AS `入学时间`,timestampdiff(YEAR,`学生`.`出生日期`,curdate()) AS `年龄`,`GetDormString`(`宿舍视图`.`园区`,`宿舍视图`.`楼号`,`宿舍视图`.`单元`,`宿舍视图`.`层数`,`宿舍视图`.`户号`,`宿舍视图`.`房间号`) AS `宿舍`,`学生`.`出生日期` AS `出生日期` from (((`学生` join `班级` on((`学生`.`班级` = `班级`.`编号`))) join `院系` on((`班级`.`院系` = `院系`.`院系ID`))) join `宿舍视图` on((`学生`.`宿舍` = `宿舍视图`.`代码`)));

-- ----------------------------
-- View structure for 宿舍视图
-- ----------------------------
DROP VIEW IF EXISTS `宿舍视图`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `宿舍视图` AS select `宿舍`.`代码` AS `代码`,`GetGardenName`(substr(`宿舍`.`代码`,1,1)) AS `园区`,`宿舍`.`楼号` AS `楼号`,`宿舍`.`单元` AS `单元`,`宿舍`.`层数` AS `层数`,`宿舍`.`户号` AS `户号`,`宿舍`.`房间号` AS `房间号`,`宿舍`.`人数规模` AS `人数规模`,`宿舍`.`实际居住` AS `实际居住` from `宿舍`;

-- ----------------------------
-- View structure for 成绩视图
-- ----------------------------
DROP VIEW IF EXISTS `成绩视图`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `成绩视图` AS select `学生`.`学号` AS `学号`,`学生`.`姓名` AS `姓名`,group_concat(`学习`.`成绩` order by `学习`.`学期` ASC separator ', ') AS `所有成绩`,round((sum((`GradetoGPA`(`学习`.`成绩`,`学习`.`类型`) * `课程`.`学分`)) / sum(if((`学习`.`类型` = '必修'),`课程`.`学分`,0))),2) AS `总平均绩点` from ((`学生` join `学习` on((`学生`.`学号` = `学习`.`学号`))) join `课程` on((`学习`.`课程号` = `课程`.`课程号`))) group by `学生`.`学号`;

-- ----------------------------
-- View structure for 课程视图
-- ----------------------------
DROP VIEW IF EXISTS `课程视图`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `课程视图` AS select `课程`.`课程号` AS `课程号`,`课程`.`课程名` AS `课程名`,`院系`.`院系全称` AS `院系全称`,`课程`.`学分` AS `学分`,`课程`.`学时` AS `学时`,`课程`.`课时类型` AS `课时类型` from (`课程` join `院系` on((`课程`.`开设院系` = `院系`.`院系ID`)));

-- ----------------------------
-- Function structure for GetDormString
-- ----------------------------
DROP FUNCTION IF EXISTS `GetDormString`;
delimiter ;;
CREATE FUNCTION `GetDormString`(park VARCHAR(10),
    building SMALLINT,
    unit enum('A','B','C'),
    floor SMALLINT,
    house_number SMALLINT,
    room_number SMALLINT)
 RETURNS varchar(50) CHARSET utf8mb4
  DETERMINISTIC
BEGIN
    DECLARE result VARCHAR(50);
		DECLARE room VARCHAR(50);
    -- 生成房间号的描述
CASE
    WHEN house_number IS NOT NULL THEN
        CASE room_number
            WHEN 1 THEN SET room = '1-4';
            WHEN 2 THEN SET room = '5-8';
            WHEN 3 THEN SET room = '9-14';
        END CASE;
    ELSE
        SET room = CONCAT(room_number, '号');
END CASE;
-- 生成宿舍描述
SET result = CONCAT_WS(
    '',
    park,
    building, '号楼',
    IFNULL(NULLIF(unit, ''), ''),
    IF(unit IS NOT NULL AND unit != '', '单元', ''),
    IF(house_number IS NULL OR house_number = '', CONCAT(floor, '层'), ''),
    IFNULL(NULLIF(house_number, ''), ''),
    IF(house_number IS NOT NULL AND house_number != '', '户', ''),
    room, '房'
);
RETURN result;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for GetFloorNumber
-- ----------------------------
DROP FUNCTION IF EXISTS `GetFloorNumber`;
delimiter ;;
CREATE FUNCTION `GetFloorNumber`(unit_or_floor SMALLINT, room_number SMALLINT)
 RETURNS smallint
  DETERMINISTIC
BEGIN
    DECLARE floor_number SMALLINT;
    DECLARE room_num INT;

    IF room_number IS NULL OR room_number = '' THEN
        SET floor_number = unit_or_floor;
    ELSE
        SET room_num = CAST(room_number AS UNSIGNED);
        SET floor_number = CEIL(room_num / 2);
    END IF;

    RETURN floor_number;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for GetGardenName
-- ----------------------------
DROP FUNCTION IF EXISTS `GetGardenName`;
delimiter ;;
CREATE FUNCTION `GetGardenName`(code CHAR(1))
 RETURNS varchar(10) CHARSET utf8mb4
  DETERMINISTIC
BEGIN
    DECLARE garden_name VARCHAR(10);

    CASE code
        WHEN 'H' THEN SET garden_name = '荷园';
        WHEN 'L' THEN SET garden_name = '柳园';
        WHEN 'J' THEN SET garden_name = '菊园';
        WHEN 'S' THEN SET garden_name = '松园';
        ELSE SET garden_name = '';
    END CASE;

    RETURN garden_name;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for GradetoGPA
-- ----------------------------
DROP FUNCTION IF EXISTS `GradetoGPA`;
delimiter ;;
CREATE FUNCTION `GradetoGPA`(grade VARCHAR(5), course_type VARCHAR(20))
 RETURNS decimal(3,2)
  DETERMINISTIC
BEGIN
    DECLARE numeric_grade INT;
    DECLARE gpa DECIMAL(3, 2);
    -- 对选修课程不计入绩点
    IF course_type = '选修' THEN SET gpa = null;
    ELSE
        -- 将等级制成绩转换为百分制成绩
        IF grade = 'A' OR grade = '优' THEN SET numeric_grade = 95;
        ELSEIF grade = 'B' OR grade = '良' THEN SET numeric_grade = 85;
        ELSEIF grade = 'C' OR grade = '中' THEN SET numeric_grade = 75;
        ELSEIF grade = 'D' OR grade = '及格' THEN SET numeric_grade = 65;
        ELSEIF grade = 'E' OR grade = '差' THEN SET numeric_grade = 0;
        ELSE SET numeric_grade = CAST(grade AS UNSIGNED);
        END IF;
        -- 将百分制成绩转换为绩点
        IF numeric_grade >= 90 THEN SET gpa = 4.0;
        ELSEIF numeric_grade >= 85 THEN SET gpa = 3.7;
        ELSEIF numeric_grade >= 80 THEN SET gpa = 3.2;
        ELSEIF numeric_grade >= 75 THEN SET gpa = 2.7;
        ELSEIF numeric_grade >= 70 THEN SET gpa = 2.2;
        ELSEIF numeric_grade >= 65 THEN SET gpa = 1.7;
        ELSEIF numeric_grade >= 60 THEN SET gpa = 1.2;
        ELSE SET gpa = 0;
        END IF;
    END IF;
    RETURN gpa;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;


CREATE ROLE student;
CREATE ROLE teacher;

GRANT SELECT ON 课程视图,宿舍视图,学生上课视图,学习视图,成绩视图,预警,院系,联系方式 TO student;
GRANT INSERT, UPDATE, DELETE ON 联系方式,学生

GRANT SELECT ON 学生视图,课程视图,宿舍视图,学生上课视图,学习视图,成绩视图 TO teacher;
GRANT INSERT, UPDATE, DELETE ON 学生,课程,班级,学生上课视图,学习视图,成绩视图,班级上课,学习,预警,院系,通知 TO teacher;


CREATE USER students IDENTIFIED BY 'stu123';
CREATE USER teachers IDENTIFIED BY 'tea123';

GRANT student TO students;
GRANT teacher TO teachers;

ALTER USER user_student DEFAULT ROLE student;
ALTER USER user_teacher DEFAULT ROLE teacher;