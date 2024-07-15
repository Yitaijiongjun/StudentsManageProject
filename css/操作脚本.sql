DELIMITER //

CREATE FUNCTION GetGardenName(code CHAR(1)) RETURNS VARCHAR(10)
BEGIN
    DECLARE garden_name VARCHAR(10);
    CASE code
        WHEN 'H' THEN SET garden_name = '荷园';
        WHEN 'L' THEN SET garden_name = '柳园';
        WHEN 'J' THEN SET garden_name = '菊园';
        WHEN 'S' THEN SET garden_name = '松园';
        ELSE SET garden_name = '未知园区';
    END CASE;
    RETURN garden_name;
END //

DELIMITER ;


CREATE VIEW DormitoryView AS
SELECT 
    dorm_code,
    GetGardenName(SUBSTRING(dorm_code, 1, 1)) AS garden,
    SUBSTRING(dorm_code, 2, 2) AS building_number,
    SUBSTRING(dorm_code, 4, 1) AS unit_or_floor,
    SUBSTRING(dorm_code, 5) AS room_number
FROM Dormitory;

DELIMITER //


DELIMITER $$

CREATE FUNCTION calculate_course_gpa(课程号 VARCHAR(20), 成绩 VARCHAR(5), 类型 VARCHAR(20), 学分 SMALLINT)
RETURNS DECIMAL(5, 2)
DETERMINISTIC
BEGIN
    DECLARE 绩点 DECIMAL(3, 2);
    DECLARE 学分绩点 DECIMAL(5, 2);

    -- 调用上一个函数计算绩点
    SET 绩点 = calculate_gpa(课程号, 成绩, 类型);

    -- 计算某门课程的学分绩点数
    SET 学分绩点 = 绩点 * 学分;

    RETURN 学分绩点;
END$$

DELIMITER ;


DELIMITER $$

CREATE FUNCTION calculate_total_gpa(学号 VARCHAR(20))
RETURNS DECIMAL(5, 2)
DETERMINISTIC
BEGIN
    DECLARE 总学分绩点 DECIMAL(10, 2) DEFAULT 0;
    DECLARE 总学分 DECIMAL(10, 2) DEFAULT 0;
    DECLARE 当前学分绩点 DECIMAL(5, 2);
    DECLARE 当前学分 SMALLINT;
    DECLARE 课程_cursor CURSOR FOR 
        SELECT c.课程号, l.成绩, c.类型, c.学分 
        FROM 学习 l 
        JOIN 课程 c ON l.课程号 = c.课程号 
        WHERE l.学号 = 学号;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN 课程_cursor;
    read_loop: LOOP
        FETCH 课程_cursor INTO 当前课程号, 当前成绩, 当前类型, 当前学分;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- 计算当前课程的学分绩点数
        SET 当前学分绩点 = calculate_course_gpa(当前课程号, 当前成绩, 当前类型, 当前学分);

        -- 累加到总学分绩点和总学分
        SET 总学分绩点 = 总学分绩点 + 当前学分绩点;
        SET 总学分 = 总学分 + 当前学分;
    END LOOP;

    CLOSE 课程_cursor;

    -- 计算并返回平均学分绩点
    IF 总学分 = 0 THEN
        RETURN 0;
    ELSE
        RETURN 总学分绩点 / 总学分;
    END IF;
END$$

DELIMITER ;


DELIMITER //
CREATE DEFINER=`root`@`%` FUNCTION `GradetoGPA`(grade VARCHAR(5), course_type VARCHAR(20)) RETURNS decimal(3,2)
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
        ELSE SET numeric_grade = CAST(grade AS UNSIGNED INT);
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
END//
DELIMITER ;



CREATE VIEW student_gpa_view AS
SELECT 
    学生.学号,
    学生.姓名,
    GROUP_CONCAT(学习.成绩 ORDER BY 学期 SEPARATOR ', ') AS 所有成绩,
    ROUND(SUM(GradetoGPA(学习.成绩, 课程.类型) * 课程.学分) / SUM(IF(课程.类型 = '必修', 课程.学分, 0)), 2) AS 总平均绩点
FROM 
    学生
JOIN 
    学习 ON 学生.学号 = 学习.学号
JOIN 
    课程 ON 学习.课程号 = 课程.课程号
GROUP BY 
    学生.学号;


CREATE TABLE `课程` (
  `课程号` varchar(20) NOT NULL,
  `课程名` varchar(20) DEFAULT NULL,
  `开设院系` varchar(20) DEFAULT NULL,
  `学分` smallint DEFAULT NULL,
  `学时` smallint DEFAULT NULL,
  `类型` varchar(20) DEFAULT NULL COMMENT '选修/必修',
  `课时类型` varchar(20) DEFAULT NULL COMMENT '整节/上半节/下半节',
  PRIMARY KEY (`课程号`) USING BTREE,
  KEY `开设院系` (`开设院系`) USING BTREE,
  CONSTRAINT `课程_ibfk_1` FOREIGN KEY (`开设院系`) REFERENCES `院系` (`院系ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `课程_chk_1` CHECK ((`学时` > 0)),
  CONSTRAINT `课程_chk_2` CHECK ((`学分` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;


concat(`宿舍视图`.`园区`,`宿舍视图`.`楼号`,'号楼',`宿舍视图`.`单元`,'单元',`宿舍视图`.`层数`,'层',`宿舍视图`.`户号`,'户',`宿舍视图`.`房间号`,'房' ) AS `宿舍`
concat( substr( `学生`.`学号`, 1, 4 ), '-09-01' ) AS `入学时间`,timestampdiff(YEAR,`学生`.`出生日期`,curdate()) AS `年龄` 


DELIMITER //
CREATE FUNCTION GetDormString(
    park VARCHAR(10),
    building SMALLINT,
    unit enum('A','B','C'),
    floor SMALLINT,
    house_number SMALLINT,
    room_number SMALLINT
) RETURNS VARCHAR(50)
DETERMINISTIC
BEGIN
    DECLARE result VARCHAR(50);
    -- 生成房间号的描述
CASE
    WHEN house_number IS NOT NULL THEN
        CASE room_number
            WHEN 1 THEN SET room_number = '1-4';
            WHEN 2 THEN SET room_number = '5-8';
            WHEN 3 THEN SET room_number = '9-14';
        END CASE;
    ELSE
        SET room_number = CONCAT(room_number, '号');
END CASE;
    -- 生成宿舍描述
    SET result = CONCAT_WS('',
        park,
        building, '号楼',
        IFNULL(NULLIF(unit, ''), ''),
        IF(unit IS NOT NULL AND unit != '', '单元', ''),
        floor, '层',
        IFNULL(NULLIF(house_number, ''), ''),
        IF(house_number IS NOT NULL AND house_number != '', '户', ''),
        room_number, '房');
    RETURN result;
END //
DELIMITER ;


为遵循数据库的规范化设计，最大可能减少冗余和储存异常，提高数据一致性和完整性。我在存储信息时为不同的属性
创建了必要的实体集，这些实体集使用自动递增的整型主码。所以在插入数据时需要先根据属性实体的属性进行查表，
得到主码编号后再进行数据插入。比如学生实体集中有两个属性：班级和院系，在插入学生数据时，需要先查表得到班级的主码
编号，再查表得到院系的主码编号，然后再插入学生数据。这样做可以保证数据的一致性和完整性。而当出现数据缺失时则
直接进行插入，这也符合实际使用，即在使用过程中不断完善数据，实现操作的高效化。
