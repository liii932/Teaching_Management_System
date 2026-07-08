# 教学管理系统开发工作流

## 1. 项目定位与复用边界

### 1.1 目标

在 `D:\DataBase\Teaching_Management_System` 下实现一个 Java 教学管理系统，数据库使用 MySQL，数据库结构参考 `D:\DataBase\sql\edusystem.sql`。

系统应覆盖以下教学管理业务：

- 系信息管理：`department`
- 专业信息管理：`major`
- 班级信息管理：`class`
- 学生信息管理：`student`
- 教师信息管理：`teacher`
- 课程信息管理：`course`
- 选课与成绩管理：`sc`

### 1.2 框架参考

参考 `D:\DataBase\desktop-doc-share` 的工程组织方式，而不是直接复用文件共享业务代码。

可复用的框架思想：

- Maven 项目结构：`pom.xml`、`src/main/java`、`src/main/resources`、`src/test/java`
- Java 21 编译配置
- `common`、`server`、`web` 分层思想
- DAO 层使用 JDBC 和 `PreparedStatement`
- Service 层封装业务校验
- Web 层使用 JDK 内置 `HttpServer` 暴露 REST API
- 使用 Gson 处理 JSON 请求和响应
- 使用 slf4j + logback 记录日志
- 使用 JUnit 进行单元测试和集成测试

需要调整的地方：

- `desktop-doc-share` 当前使用 SQLite，本系统改为 MySQL。
- 原 `BaseDao` 假设表都有自增 `id` 字段，本系统多数表使用业务主键，例如 `DeptNo`、`MajorNo`、`Sno`、`Tno`、`Cno`，`sc` 还是复合主键，因此不能原样照搬通用 `findById/deleteById`。
- 文件共享、聊天、好友、桌面共享等业务不进入本系统。
- Swing 客户端不是本系统首要目标，优先实现 Web 管理界面。

## 2. 工程初始化

### 2.1 创建 Maven 项目骨架

在 `Teaching_Management_System` 中建立以下目录：

- `src/main/java/com/teachmanage`
- `src/main/resources`
- `src/main/resources/db`
- `src/main/resources/web`
- `src/test/java/com/teachmanage`

### 2.2 编写 Maven 配置

参考 `desktop-doc-share/pom.xml`，新项目建议依赖：

- `mysql:mysql-connector-j` 或 `com.mysql:mysql-connector-j`
- `com.google.code.gson:gson`
- `org.slf4j:slf4j-api`
- `ch.qos.logback:logback-classic`
- `org.junit.jupiter:junit-jupiter`

保留 Java 21：

- `maven.compiler.source=21`
- `maven.compiler.target=21`
- `maven-compiler-plugin` 使用 `release=21`

### 2.3 配置资源文件

建议添加：

- `src/main/resources/application.properties`
- `src/main/resources/logback.xml`
- `src/main/resources/db/edusystem.sql`
- `src/main/resources/web/index.html`

`application.properties` 规划字段：

- `server.port=8080`
- `db.url=jdbc:mysql://localhost:3306/edusystem?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false`
- `db.username=root`
- `db.password=本机密码`
- `db.init-schema=false`

注意：数据库密码不要硬编码到提交说明或公开文档中，后续可支持环境变量覆盖。

## 3. 数据库工作流

### 3.1 梳理原始建表脚本

参考 `D:\DataBase\sql\edusystem.sql`，当前表关系如下：

- `department(DeptNo)` 是系表。
- `major(MajorNo)` 通过 `DeptNo` 关联 `department`。
- `class(ClassNo)` 通过 `MajorNo` 关联 `major`。
- `student(Sno)` 通过 `ClassNo` 关联 `class`。
- `teacher(Tno)` 通过 `ClassNo` 关联 `class`。
- `course(Cno)` 通过 `Tno` 关联 `teacher`。
- `sc(Sno, Cno)` 同时关联 `student` 和 `course`，记录成绩。

### 3.2 初始化数据库

开发前先确认 MySQL 中存在数据库：

```sql
CREATE DATABASE IF NOT EXISTS edusystem
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;
```

然后导入 `edusystem.sql`。

### 3.3 建议补充的数据约束

在不破坏原教学脚本结构的前提下，Service 层需要补充校验：

- 编号长度校验：
  - `DeptNo` 长度 4
  - `MajorNo` 长度 6
  - `ClassNo` 长度 8
  - `Sno` 长度 10
  - `Tno` 长度 8
  - `Cno` 长度 6
- 性别只允许 `男`、`女` 或空值。
- 年龄应为合理正整数。
- 学分不能为负数。
- 成绩为空表示未录入，非空时建议范围为 `0.0` 到 `100.0`。
- 删除系、专业、班级、学生、课程前明确外键级联或置空影响。

### 3.4 数据访问策略

不直接照搬 `desktop-doc-share` 的 `BaseDao<T>` 自增主键设计。

建议建立新的数据库基础设施：

- `DbConfig`：读取数据库配置。
- `DbManager`：创建 MySQL `Connection`。
- `SqlExecutor` 或轻量 `BaseDao`：只封装连接、参数绑定、查询列表、查询单行、更新语句，不假设主键名称。

每个 DAO 自己明确业务主键：

- `DepartmentDao.findByDeptNo(String deptNo)`
- `MajorDao.findByMajorNo(String majorNo)`
- `ClassDao.findByClassNo(String classNo)`
- `StudentDao.findBySno(String sno)`
- `TeacherDao.findByTno(String tno)`
- `CourseDao.findByCno(String cno)`
- `ScoreDao.findByKey(String sno, String cno)`

## 4. 后端分层设计

### 4.1 包结构

建议包结构：

- `com.teachmanage.common.model`
- `com.teachmanage.common.dto`
- `com.teachmanage.common.exception`
- `com.teachmanage.common.util`
- `com.teachmanage.server.core`
- `com.teachmanage.server.dao`
- `com.teachmanage.server.service`
- `com.teachmanage.web`

### 4.2 Model 实体

建立与数据表对应的实体类：

- `Department`
- `Major`
- `Clazz`，避免使用 Java 关键语义附近的 `Class`
- `Student`
- `Teacher`
- `Course`
- `Score`

字段命名建议使用 Java 小驼峰：

- `DeptNo` -> `deptNo`
- `DeptName` -> `deptName`
- `MajorNo` -> `majorNo`
- `ClassNo` -> `classNo`
- `Sno` -> `sno`
- `Tno` -> `tno`
- `Cno` -> `cno`

JSON 输出可保持小驼峰，也可以在前端表头显示中文字段名。

### 4.3 DAO 层

每张表建立一个 DAO：

- `DepartmentDao`
- `MajorDao`
- `ClassDao`
- `StudentDao`
- `TeacherDao`
- `CourseDao`
- `ScoreDao`

每个 DAO 至少实现：

- 新增
- 按主键查询
- 条件分页查询或全部查询
- 更新
- 删除
- 计数或存在性检查

重点查询：

- 按系查询专业。
- 按专业查询班级。
- 按班级查询学生。
- 按班级查询班主任或负责教师。
- 按教师查询课程。
- 按学生查询成绩单。
- 按课程查询选课学生和成绩。
- 查询学生平均分、总学分、已修课程数。

### 4.4 Service 层

建立接口和实现类：

- `IDepartmentService` / `DepartmentServiceImpl`
- `IMajorService` / `MajorServiceImpl`
- `IClassService` / `ClassServiceImpl`
- `IStudentService` / `StudentServiceImpl`
- `ITeacherService` / `TeacherServiceImpl`
- `ICourseService` / `CourseServiceImpl`
- `IScoreService` / `ScoreServiceImpl`
- `IStatisticsService` / `StatisticsServiceImpl`

Service 层职责：

- 参数校验。
- 重复主键校验。
- 外键存在性校验。
- 删除前影响检查。
- 统一业务异常。
- 组合查询和统计查询。

典型业务规则：

- 新增专业前必须确认系存在。
- 新增班级前必须确认专业存在。
- 新增学生时，班级可以为空；不为空时必须存在。
- 新增教师时，负责班级可以为空；不为空时必须存在。
- 新增课程时，教师可以为空；不为空时必须存在。
- 录入成绩前必须确认学生和课程都存在。
- 同一个学生同一门课只能有一条成绩记录。
- 删除学生时，成绩表按外键级联删除。
- 删除课程时，成绩表按外键级联删除。

### 4.5 ServerMain

参考 `desktop-doc-share.server.core.ServerMain`，但本系统不需要 Socket 长连接和命令分发器。

建议职责：

- 读取配置。
- 初始化 MySQL 连接。
- 创建 DAO。
- 创建 Service。
- 创建并启动 WebServer。
- 关闭时释放连接和 HttpServer。

## 5. Web API 设计

### 5.1 通用约定

参考 `desktop-doc-share.web.WebServer` 的 JDK `HttpServer` 路由方式。

统一响应：

```json
{
  "ok": true,
  "data": {}
}
```

错误响应：

```json
{
  "ok": false,
  "error": "错误信息"
}
```

HTTP 状态码：

- `200`：成功
- `201`：新增成功
- `400`：参数错误
- `404`：资源不存在
- `409`：主键重复或业务冲突
- `500`：服务器内部错误

### 5.2 路由规划

系管理：

- `GET /api/departments`
- `GET /api/departments/{deptNo}`
- `POST /api/departments`
- `PUT /api/departments/{deptNo}`
- `DELETE /api/departments/{deptNo}`

专业管理：

- `GET /api/majors`
- `GET /api/majors?deptNo=xxxx`
- `GET /api/majors/{majorNo}`
- `POST /api/majors`
- `PUT /api/majors/{majorNo}`
- `DELETE /api/majors/{majorNo}`

班级管理：

- `GET /api/classes`
- `GET /api/classes?majorNo=xxxxxx`
- `GET /api/classes/{classNo}`
- `POST /api/classes`
- `PUT /api/classes/{classNo}`
- `DELETE /api/classes/{classNo}`

学生管理：

- `GET /api/students`
- `GET /api/students?classNo=xxxxxxxx&keyword=xxx`
- `GET /api/students/{sno}`
- `POST /api/students`
- `PUT /api/students/{sno}`
- `DELETE /api/students/{sno}`

教师管理：

- `GET /api/teachers`
- `GET /api/teachers?classNo=xxxxxxxx&keyword=xxx`
- `GET /api/teachers/{tno}`
- `POST /api/teachers`
- `PUT /api/teachers/{tno}`
- `DELETE /api/teachers/{tno}`

课程管理：

- `GET /api/courses`
- `GET /api/courses?teacherNo=xxxxxxxx&keyword=xxx`
- `GET /api/courses/{cno}`
- `POST /api/courses`
- `PUT /api/courses/{cno}`
- `DELETE /api/courses/{cno}`

成绩管理：

- `GET /api/scores`
- `GET /api/scores?studentNo=xxxxxxxxxx`
- `GET /api/scores?courseNo=xxxxxx`
- `POST /api/scores`
- `PUT /api/scores/{sno}/{cno}`
- `DELETE /api/scores/{sno}/{cno}`

统计查询：

- `GET /api/statistics/overview`
- `GET /api/statistics/student/{sno}`
- `GET /api/statistics/course/{cno}`
- `GET /api/statistics/class/{classNo}`

### 5.3 Handler 拆分

Web 层建议拆分为：

- `WebServer`
- `BaseWebHandler`
- `DepartmentWebHandler`
- `MajorWebHandler`
- `ClassWebHandler`
- `StudentWebHandler`
- `TeacherWebHandler`
- `CourseWebHandler`
- `ScoreWebHandler`
- `StatisticsWebHandler`
- `StaticFileHandler`

`BaseWebHandler` 负责：

- 解析 JSON 请求体。
- 解析 query string。
- 输出 JSON。
- CORS 预检。
- 统一异常转响应。

## 6. Web 前端工作流

### 6.1 首屏结构

`src/main/resources/web/index.html` 建议实现一个可直接使用的后台管理界面，而不是介绍页。

首屏布局：

- 顶部栏：系统名称、数据库连接状态。
- 左侧导航：系、专业、班级、学生、教师、课程、成绩、统计。
- 主区域：当前模块的数据表格、筛选区、操作按钮、编辑表单。

### 6.2 页面模块

系管理页：

- 展示系编号、系名称。
- 支持新增、编辑、删除。

专业管理页：

- 展示专业编号、专业名称、所属系。
- 支持按系筛选。
- 新增或编辑时使用下拉框选择系。

班级管理页：

- 展示班级号、班级名称、所属专业。
- 支持按专业筛选。
- 新增或编辑时使用下拉框选择专业。

学生管理页：

- 展示学号、姓名、性别、年龄、班级。
- 支持按班级和姓名关键字筛选。
- 支持新增、编辑、删除。

教师管理页：

- 展示教职工号、姓名、性别、职称、负责班级。
- 支持按班级和姓名关键字筛选。
- 支持新增、编辑、删除。

课程管理页：

- 展示课程号、课程名称、学分、授课教师。
- 支持按教师和课程名筛选。
- 新增或编辑时使用下拉框选择教师。

成绩管理页：

- 展示学号、学生姓名、课程号、课程名、成绩。
- 支持按学生、课程筛选。
- 支持录入、修改、删除成绩。

统计页：

- 总览：系数量、专业数量、班级数量、学生数量、教师数量、课程数量、成绩记录数量。
- 学生成绩单：课程、学分、成绩、平均分。
- 课程成绩分析：人数、最高分、最低分、平均分。
- 班级成绩分析：班级学生数、课程平均分。

### 6.3 前端交互要求

- 所有表单提交前做基础校验。
- 删除操作需要二次确认。
- 外键字段用下拉选择，避免手工输入错误。
- 网络错误和业务错误显示在页面固定状态区域。
- 表格空数据时显示明确提示。
- 编辑时复用弹窗或右侧表单。
- 保存成功后刷新当前列表。

## 7. 测试工作流

### 7.1 DAO 测试

为每个 DAO 编写测试：

- 新增后可查询。
- 更新后字段正确变化。
- 删除后不可查询。
- 外键约束按预期生效。
- `sc` 复合主键不能重复。

### 7.2 Service 测试

重点覆盖：

- 主键重复时报错。
- 外键不存在时报错。
- 成绩范围非法时报错。
- 删除存在关联数据时行为符合预期。
- 查询统计结果正确。

### 7.3 Web API 测试

覆盖每类资源的：

- `GET list`
- `GET detail`
- `POST create`
- `PUT update`
- `DELETE delete`
- 错误参数响应
- 不存在资源响应

### 7.4 手工验收流程

建议按以下顺序验收：

1. 启动 MySQL。
2. 创建 `edusystem` 数据库。
3. 导入 `edusystem.sql`。
4. 启动 Java 服务。
5. 浏览器打开 `http://localhost:8080`。
6. 新增一个系。
7. 在该系下新增专业。
8. 在该专业下新增班级。
9. 新增学生并分配到班级。
10. 新增教师并指定负责班级。
11. 新增课程并指定授课教师。
12. 给学生录入课程成绩。
13. 查看学生成绩单和课程统计。
14. 修改成绩并确认统计变化。
15. 删除测试数据并确认外键行为符合预期。

## 8. 实施阶段拆分

### 阶段一：项目基础

- 创建 Maven 项目。
- 配置依赖。
- 添加日志配置。
- 添加数据库配置读取。
- 建立 MySQL 连接管理。
- 复制并整理 `edusystem.sql` 到资源目录。

阶段验收：

- `mvn test` 可以运行。
- 程序可以连接 MySQL。
- 服务启动后能返回健康检查接口，例如 `GET /api/health`。

### 阶段二：核心实体与 DAO

- 建立 7 个实体类。
- 建立 DAO 基础工具。
- 完成 7 个 DAO。
- 编写 DAO 测试。

阶段验收：

- 所有表可以完成增删改查。
- 复合主键成绩表可以正确操作。

### 阶段三：Service 业务层

- 建立 Service 接口。
- 实现参数校验。
- 实现外键存在性校验。
- 实现统计查询。
- 编写 Service 测试。

阶段验收：

- 非法数据不能进入数据库。
- 统计结果与手工 SQL 查询一致。

### 阶段四：REST API

- 建立 WebServer。
- 建立通用 WebHandler。
- 完成各模块 REST 路由。
- 统一 JSON 响应格式。
- 编写 API 测试。

阶段验收：

- 使用浏览器或 HTTP 客户端可以完成所有业务操作。
- 错误响应清晰且状态码合理。

### 阶段五：Web 管理界面

- 编写 `index.html`。
- 实现导航和模块切换。
- 实现所有列表页。
- 实现新增、编辑、删除表单。
- 实现成绩录入和统计页面。

阶段验收：

- 不借助命令行即可完成教学管理主流程。
- 页面刷新后数据仍来自 MySQL。

### 阶段六：整理与交付

- 编写 `README.md`。
- 写清数据库初始化方式。
- 写清启动方式。
- 写清默认端口和配置项。
- 整理测试说明。
- 检查无硬编码本机密码。

阶段验收：

- 新环境按文档可以完成部署。
- `mvn test` 通过。
- 主要业务流程手工验收通过。

## 9. 风险点与处理方案

### 9.1 MySQL 连接配置风险

风险：

- 本机 MySQL 账号、密码、端口不一致。

处理：

- 使用 `application.properties` 管理配置。
- 支持环境变量覆盖数据库地址和密码。
- 启动时输出清晰的连接失败原因。

### 9.2 原框架 SQLite 假设风险

风险：

- 原 `BaseDao` 和 schema 初始化方式偏 SQLite，不适合 MySQL。

处理：

- 只复用分层思想。
- 新写 MySQL 连接管理和 DAO 基类。
- 不假设所有表都有自增 `id`。

### 9.3 `class` 表名风险

风险：

- `class` 是 Java 语义中的特殊词，虽然不是变量名关键字，但容易造成阅读混淆。

处理：

- Java 实体命名为 `Clazz`。
- SQL 语句中使用反引号包裹表名：`` `class` ``。

### 9.4 外键级联风险

风险：

- 删除系、专业、学生、课程时可能级联删除大量数据。

处理：

- Web 删除前显示影响提示。
- Service 层提供影响检查。
- 测试覆盖级联删除和置空行为。

### 9.5 中文编码风险

风险：

- SQL、页面、JSON 中文乱码。

处理：

- Maven、源文件、HTML、SQL 统一 UTF-8。
- MySQL 使用 `utf8mb4`。
- HTTP 响应设置 `Content-Type: application/json; charset=utf-8`。

## 10. 建议最终文件清单

最终项目大致应包含：

- `pom.xml`
- `README.md`
- `to-do-list.md`
- `src/main/resources/application.properties`
- `src/main/resources/logback.xml`
- `src/main/resources/db/edusystem.sql`
- `src/main/resources/web/index.html`
- `src/main/java/com/teachmanage/AppMain.java`
- `src/main/java/com/teachmanage/common/model/*.java`
- `src/main/java/com/teachmanage/common/dto/*.java`
- `src/main/java/com/teachmanage/common/exception/*.java`
- `src/main/java/com/teachmanage/common/util/*.java`
- `src/main/java/com/teachmanage/server/core/*.java`
- `src/main/java/com/teachmanage/server/dao/*.java`
- `src/main/java/com/teachmanage/server/service/*.java`
- `src/main/java/com/teachmanage/web/*.java`
- `src/test/java/com/teachmanage/**/*Test.java`
