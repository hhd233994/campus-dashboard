# 消费数据分析平台

基于 Spring Boot 3 + MyBatis-Plus + Redis 的数据分析平台，实现用户认证、消费记录管理、数据统计分析与 Excel 导出等核心功能。

**在线演示：** [https://campus-dashboard-production.up.railway.app](https://campus-dashboard-production.up.railway.app)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.7-blue.svg)](https://baomidou.com/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 项目截图

> 启动项目后访问 `http://localhost:8080/index.html`

| 数据概览 | 消费记录管理 |
|---------|------------|
| <!-- 在此插入 Dashboard 截图 --> | <!-- 在此插入记录列表截图 --> |

| 分类统计 | 消费趋势 |
|---------|---------|
| <!-- 在此插入分类统计截图 --> | <!-- 在此插入趋势图截图 --> |

---

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.5、MyBatis-Plus 3.5 |
| 数据库 | MySQL 8.0、Redis 6.0+ |
| 安全认证 | JWT（jjwt）、BCrypt |
| 数据导出 | EasyExcel |
| 前端 | HTML5 / CSS3 / JavaScript、ECharts 5.4 |
| 接口文档 | Knife4j |
| 部署 | Docker、GitHub Actions CI/CD |
| 测试 | JUnit 5、MockMvc |

---

## 功能特性

**认证与权限**
- JWT 无状态 Token 认证，支持登录 / 注册
- 基于自定义 `@RequireRole` 注解实现方法级 RBAC 权限控制（ADMIN / USER）
- BCrypt 密码加密存储

**业务功能**
- 消费记录 CRUD，支持分页查询
- 多维度条件筛选（日期范围、消费类别）
- EasyExcel 高性能 Excel 数据导出

**数据统计**
- ECharts 可视化图表（饼图、折线图、柱状图）
- 分类统计、每日趋势、总金额与今日消费统计
- Redis 缓存统计数据，Cache-Aside 策略降低数据库查询压力

**安全与工程化**
- 基于 AOP + `@OperationLog` 自定义注解实现操作日志自动记录
- 基于 Redis 滑动窗口算法的接口限流（`@RateLimit` 注解）
- 完整的 Controller / Service 层单元测试（9个测试类）
- GitHub Actions 自动化 CI 流程，Docker 一键部署

---

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 方式一：Maven 运行（开发环境）

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE campus_dashboard CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 修改数据库配置（见下方说明）
# 3. 启动项目（系统自动执行 schema.sql 初始化表结构与测试数据）
mvn spring-boot:run
```

### 方式二：Docker 部署（生产环境）

```bash
docker-compose up -d
```

### 方式三：Railway 一键部署（在线演示推荐）

1. 将代码推送到 GitHub
2. 访问 [railway.app](https://railway.app)，使用 GitHub 登录
3. 新建 Project，选择 `Deploy from GitHub repo`，导入本仓库
4. 在 Project 中分别添加 **MySQL** 和 **Redis** 插件
5. 在应用的 Variables 中配置以下环境变量：

| 变量名 | 值 |
|--------|----|
| `DB_URL` | 从 MySQL 插件连接信息中复制 |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | MySQL 插件生成的密码 |
| `REDIS_HOST` | 从 Redis 插件连接信息中复制 |
| `REDIS_PORT` | `6379` |
| `JWT_SECRET` | 任意随机字符串 |

6. 部署成功后，在 Settings → Networking 中开启公网访问（Generate Domain）

### 数据库配置

修改 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_dashboard?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码

spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=
```

### 访问地址

- 前端页面：`http://localhost:8080/index.html`
- API 文档：`http://localhost:8080/doc.html`

---

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| zhangsan | 123456 | 普通用户 |
| lisi | 123456 | 普通用户 |

---

## API 接口

**认证**

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 用户登录 |
| POST | `/api/auth/register` | 用户注册 |

**消费记录**

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/record/add` | 新增消费记录 |
| GET | `/api/record/list` | 分页查询（支持筛选） |
| GET | `/api/record/user/{userId}` | 查询用户消费记录 |
| DELETE | `/api/record/{id}` | 删除消费记录 |
| GET | `/api/record/export/excel` | 导出 Excel |

**统计分析**

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/record/statistics/category` | 分类统计 |
| GET | `/api/record/statistics/daily` | 每日统计 |
| GET | `/api/record/statistics/total` | 总消费金额 |
| GET | `/api/record/statistics/today` | 今日消费金额 |
| GET | `/api/record/statistics/overview` | 数据概览 |

> 完整接口文档请访问 Knife4j：`http://localhost:8080/doc.html`

---

## 数据库设计

**user 表**：用户信息（username、password、role 等）

**consumption_record 表**：消费记录，建立 `idx_user_id`、`idx_create_time`、`idx_category` 复合索引优化查询性能

**operation_log 表**：操作审计日志，记录用户操作类型、IP、耗时等信息

详细表结构参见 `src/main/resources/sql/schema.sql`。

---

## 运行测试

```bash
mvn test
```

---

## 许可证

[MIT License](LICENSE)

---

## 联系方式

- 作者：沈仕炜
- 邮箱：3228475260@qq.com
- GitHub：[github.com/hhd233994](https://github.com/hhd233994)
