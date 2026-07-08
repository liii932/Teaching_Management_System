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
