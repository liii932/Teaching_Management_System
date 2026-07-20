# 教学管理系统

基于 Java 21、JDK HttpServer 和 MySQL 的轻量级教学管理系统。应用以单进程运行，同时提供浏览器管理界面和 REST API。

## 功能

- 系、专业、班级、学生、教师、课程和成绩信息的增删改查
- 按院系、专业、班级、教师及关键字筛选数据
- 学生成绩单、课程成绩分析、班级成绩分析和数据总览
- 服务健康检查、输入校验和数据库约束错误提示
- 内置静态 Web 页面，无需单独部署前端服务

## 技术栈

- Java 21
- JDK HttpServer
- MySQL Connector/J 8.4
- Gson 2.11
- SLF4J + Logback
- Maven
- JUnit 5

## 运行要求

- JDK 21
- Maven 3.9 或更高版本
- MySQL 8.0 或兼容版本

检查本机环境：

```bash
java -version
mvn -version
mysql --version
```

## 快速开始

### 1. 获取项目

```bash
git clone https://github.com/liii932/Teaching_Management_System.git
cd Teaching_Management_System
```

如需使用当前开发版本，请切换分支：

```bash
git switch feature/user-permission
```

### 2. 初始化数据库

在项目根目录进入 MySQL 客户端：

```bash
mysql -u root -p
```

需要完整演示数据时执行：

```sql
SOURCE create_database.sql;
```

`create_database.sql` 会创建 `edusystem` 数据库、重建全部业务表并导入测试数据。

只需要空表结构时执行：

```sql
CREATE DATABASE IF NOT EXISTS edusystem
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;
USE edusystem;
SOURCE src/main/resources/db/edusystem.sql;
```

如数据库中已经存在空表，也可以单独导入测试数据：

```sql
USE edusystem;
SOURCE test_data.sql;
```

注意：`create_database.sql`、`edusystem.sql` 以及下文的自动初始化功能都会删除并重建现有业务表，请勿直接用于需要保留数据的数据库。

### 3. 创建本地配置

PowerShell：

```powershell
Copy-Item src/main/resources/application.properties.example src/main/resources/application.properties
```

Bash：

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

编辑 `src/main/resources/application.properties`：

```properties
server.port=8080
db.url=jdbc:mysql://localhost:3306/edusystem?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
db.username=root
db.password=你的数据库密码
db.init-schema=false
```

该配置文件已被 Git 忽略，不会随代码提交。`db.init-schema=true` 会在每次服务启动时删除并重建表，只建议用于临时开发数据库。

也可以通过环境变量覆盖配置文件：

| 环境变量 | 配置项 | 默认值 |
| --- | --- | --- |
| `TMS_SERVER_PORT` | `server.port` | `8080` |
| `TMS_DB_URL` | `db.url` | 空 |
| `TMS_DB_USERNAME` | `db.username` | `root` |
| `TMS_DB_PASSWORD` | `db.password` | 空 |
| `TMS_DB_INIT_SCHEMA` | `db.init-schema` | `false` |

环境变量的优先级高于 `application.properties`。

### 4. 启动服务

```bash
mvn compile exec:java
```

启动成功后访问：

- 管理界面：<http://localhost:8080>
- 健康检查：<http://localhost:8080/api/health>

使用 `Ctrl+C` 停止服务。

## API 概览

所有接口均以 `/api` 为前缀，数据使用 JSON 格式。

| 资源 | 路径 | 说明 |
| --- | --- | --- |
| 健康检查 | `GET /api/health` | 检查服务和数据库连接 |
| 系 | `/api/departments` | 系信息 CRUD |
| 专业 | `/api/majors` | 专业信息 CRUD，可按系筛选 |
| 班级 | `/api/classes` | 班级信息 CRUD，可按专业筛选 |
| 学生 | `/api/students` | 学生信息 CRUD，可按班级或关键字筛选 |
| 教师 | `/api/teachers` | 教师信息 CRUD，可按班级或关键字筛选 |
| 课程 | `/api/courses` | 课程信息 CRUD，可按教师或关键字筛选 |
| 成绩 | `/api/scores` | 成绩信息 CRUD，可按学生或课程筛选 |
| 统计总览 | `GET /api/statistics/overview` | 返回各类业务数据数量 |
| 学生统计 | `GET /api/statistics/student/{学号}` | 查询学生成绩单 |
| 课程统计 | `GET /api/statistics/course/{课程号}` | 查询课程成绩分析 |
| 班级统计 | `GET /api/statistics/class/{班级号}` | 查询班级成绩分析 |

普通资源的集合路径支持 `GET` 和 `POST`，单条资源路径支持 `GET`、`PUT` 和 `DELETE`。成绩以学号和课程号作为联合主键，单条路径为 `/api/scores/{学号}/{课程号}`。

## 测试与构建

运行自动化测试：

```bash
mvn test
```

构建 JAR：

```bash
mvn clean package
```

构建结果位于 `target/`，该目录不会提交到 Git。

## 项目结构

```text
src/main/java/com/teachmanage/
├── common/          # 领域模型、校验工具和业务异常
├── server/core/     # 配置、数据库连接和服务生命周期
├── server/dao/      # 数据访问层
├── server/service/  # 业务逻辑与统计查询
└── web/             # HTTP 路由、JSON 响应和静态资源处理

src/main/resources/
├── db/edusystem.sql # 表结构初始化脚本
├── web/index.html   # 浏览器管理界面
└── logback.xml      # 日志配置
```

其他数据文件：

- `create_database.sql`：创建数据库、重建表并导入完整测试数据
- `test_data.sql`：单独的测试数据导入脚本
- `测试数据.md`：字段约束和测试数据格式说明

## 常见问题

### 端口已被占用

停止占用端口的进程，或修改 `server.port`/`TMS_SERVER_PORT` 后重新启动。服务会在启动失败时显示具体的占用端口。

### 数据库连接失败

确认 MySQL 已启动、`edusystem` 已创建，并检查数据库地址、用户名和密码。也可以访问 `/api/health` 查看连接状态。

### 中文数据乱码

确认数据库和表使用 `utf8mb4`，并保留示例 JDBC URL 中的 `useUnicode=true`、`characterEncoding=utf8` 和 `serverTimezone=Asia/Shanghai` 参数。
