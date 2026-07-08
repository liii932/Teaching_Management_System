# 教学管理系统

Java 21 + JDK HttpServer + MySQL 的教学管理系统，参考 `desktop-doc-share` 的分层风格实现。

## 数据库

先在 MySQL 中创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS edusystem
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;
```

导入建表脚本：

```bash
mysql -u root -p edusystem < src/main/resources/db/edusystem.sql
```

也可以把 `src/main/resources/application.properties` 中的 `db.init-schema` 改为 `true`，服务启动时会重建表结构。

## 配置

默认配置在 `src/main/resources/application.properties`：

- `server.port=8080`
- `db.url=jdbc:mysql://localhost:3306/edusystem?...`
- `db.username=root`
- `db.password=`

也支持环境变量覆盖：

- `TMS_SERVER_PORT`
- `TMS_DB_URL`
- `TMS_DB_USERNAME`
- `TMS_DB_PASSWORD`
- `TMS_DB_INIT_SCHEMA`

## 启动

```bash
mvn compile exec:java
```

浏览器打开：

```text
http://localhost:8080
```

健康检查：

```text
GET http://localhost:8080/api/health
```
