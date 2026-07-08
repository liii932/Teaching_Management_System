CREATE DATABASE IF NOT EXISTS edusystem
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE edusystem;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `sc`;
DROP TABLE IF EXISTS `course`;
DROP TABLE IF EXISTS `teacher`;
DROP TABLE IF EXISTS `student`;
DROP TABLE IF EXISTS `class`;
DROP TABLE IF EXISTS `major`;
DROP TABLE IF EXISTS `department`;

CREATE TABLE `department` (
  `DeptNo` char(4) NOT NULL COMMENT '系编号',
  `DeptName` varchar(30) NOT NULL COMMENT '系名称',
  PRIMARY KEY (`DeptNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系信息表';

CREATE TABLE `major` (
  `MajorNo` char(6) NOT NULL COMMENT '专业编号',
  `MajorName` varchar(30) NOT NULL COMMENT '专业名称',
  `DeptNo` char(4) NOT NULL COMMENT '所属系编号',
  PRIMARY KEY (`MajorNo`),
  KEY `DeptNo` (`DeptNo`),
  CONSTRAINT `major_ibfk_1` FOREIGN KEY (`DeptNo`) REFERENCES `department` (`DeptNo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='专业信息表';

CREATE TABLE `class` (
  `ClassNo` char(8) NOT NULL COMMENT '班级号',
  `ClassName` varchar(30) DEFAULT NULL COMMENT '班级名称',
  `MajorNo` char(6) NOT NULL COMMENT '所属专业编号',
  PRIMARY KEY (`ClassNo`),
  KEY `MajorNo` (`MajorNo`),
  CONSTRAINT `class_ibfk_1` FOREIGN KEY (`MajorNo`) REFERENCES `major` (`MajorNo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='班级信息表';

CREATE TABLE `student` (
  `Sno` char(10) NOT NULL COMMENT '学号',
  `Sname` varchar(20) NOT NULL COMMENT '姓名',
  `Ssex` char(2) DEFAULT '男' COMMENT '性别',
  `Sage` smallint DEFAULT NULL COMMENT '年龄',
  `ClassNo` char(8) DEFAULT NULL COMMENT '班级号',
  PRIMARY KEY (`Sno`),
  KEY `ClassNo` (`ClassNo`),
  CONSTRAINT `student_ibfk_1` FOREIGN KEY (`ClassNo`) REFERENCES `class` (`ClassNo`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生信息表';

CREATE TABLE `teacher` (
  `Tno` char(8) NOT NULL COMMENT '教职工号',
  `Tname` varchar(20) NOT NULL COMMENT '姓名',
  `Tsex` char(2) DEFAULT '男' COMMENT '性别',
  `Ttitle` varchar(10) DEFAULT NULL COMMENT '职称',
  `ClassNo` char(8) DEFAULT NULL COMMENT '负责班级号',
  PRIMARY KEY (`Tno`),
  KEY `ClassNo` (`ClassNo`),
  CONSTRAINT `teacher_ibfk_1` FOREIGN KEY (`ClassNo`) REFERENCES `class` (`ClassNo`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教职工信息表';

CREATE TABLE `course` (
  `Cno` char(6) NOT NULL COMMENT '课程号',
  `Cname` varchar(30) NOT NULL COMMENT '课程名称',
  `Credit` decimal(3,1) DEFAULT 0.0 COMMENT '学分',
  `Tno` char(8) DEFAULT NULL COMMENT '授课教师号',
  PRIMARY KEY (`Cno`),
  KEY `Tno` (`Tno`),
  CONSTRAINT `course_ibfk_1` FOREIGN KEY (`Tno`) REFERENCES `teacher` (`Tno`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课程信息表';

CREATE TABLE `sc` (
  `Sno` char(10) NOT NULL COMMENT '学号',
  `Cno` char(6) NOT NULL COMMENT '课程号',
  `Grade` decimal(4,1) DEFAULT NULL COMMENT '成绩',
  PRIMARY KEY (`Sno`,`Cno`),
  KEY `Cno` (`Cno`),
  CONSTRAINT `sc_ibfk_1` FOREIGN KEY (`Sno`) REFERENCES `student` (`Sno`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sc_ibfk_2` FOREIGN KEY (`Cno`) REFERENCES `course` (`Cno`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成绩信息表';

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO department (DeptNo, DeptName) VALUES
('CS01', '计算机科学与技术系'),
('MA02', '数学与应用数学系');

INSERT INTO major (MajorNo, MajorName, DeptNo) VALUES
('CS0101', '软件工程', 'CS01'),
('CS0102', '人工智能', 'CS01'),
('MA0201', '信息与计算科学', 'MA02');

INSERT INTO class (ClassNo, ClassName, MajorNo) VALUES
('CS230101', '软件2301班', 'CS0101'),
('CS230102', '软件2302班', 'CS0101'),
('CS240201', '智能2401班', 'CS0102'),
('MA230101', '信计2301班', 'MA0201');

INSERT INTO student (Sno, Sname, Ssex, Sage, ClassNo) VALUES
('2023010101', '张三', '男', 20, 'CS230101'),
('2023010102', '李四', '女', 19, 'CS230101'),
('2023010201', '王五', '男', 21, 'CS230102'),
('2023010202', '赵六', '女', 20, 'CS230102'),
('2024020101', '孙七', '男', 18, 'CS240201'),
('2024020102', '周八', '男', 19, 'CS240201'),
('2023020101', '吴九', '女', 20, 'MA230101'),
('2023020102', '郑十', '男', 22, 'MA230101'),
('2025000001', '钱十一', '男', 18, NULL);

INSERT INTO teacher (Tno, Tname, Tsex, Ttitle, ClassNo) VALUES
('T2023001', '陈教授', '男', '教授', 'CS230101'),
('T2023002', '林副教授', '女', '副教授', 'CS230102'),
('T2024001', '高讲师', '男', '讲师', 'CS240201'),
('T2023003', '黄老师', '女', '副教授', 'MA230101'),
('T2025001', '刘助教', '男', '助教', NULL);

INSERT INTO course (Cno, Cname, Credit, Tno) VALUES
('C10001', '数据库原理及应用', 3.5, 'T2023001'),
('C10002', '高级数据结构', 4.0, 'T2023002'),
('C10003', '机器学习导论', 3.0, 'T2024001'),
('C20001', '高等代数', 5.0, 'T2023003'),
('C30001', '大学生职业规划', 1.0, NULL);

INSERT INTO sc (Sno, Cno, Grade) VALUES
('2023010101', 'C10001', 92.5),
('2023010101', 'C10002', 85.0),
('2023010102', 'C10001', 100.0),
('2023010102', 'C10002', 90.5),
('2023010201', 'C10001', 78.0),
('2023010201', 'C10002', 60.0),
('2023010202', 'C10001', 45.5),
('2023010202', 'C10002', NULL),
('2024020101', 'C10003', 88.0),
('2024020102', 'C10003', 0.0),
('2023020101', 'C20001', 95.0),
('2023020102', 'C20001', 82.5);
