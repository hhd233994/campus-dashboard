# 校园消费数据管理平台

基于 **Spring Boot 3 + Vue 3 + Element Plus** 的全栈数据分析平台，实现用户认证、消费记录管理、数据统计分析与 Excel 导出等核心功能。

**在线演示：** [https://campus-dashboard-production.up.railway.app](https://campus-dashboard-production.up.railway.app)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4-brightgreen.svg)](https://vuejs.org/)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.7-blue.svg)](https://baomidou.com/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 项目截图

> **前端应用：** Vue 3 + Element Plus（清新典雅主题）
> - 开发环境访问：`http://localhost:5174`
> - 生产环境需独立部署前端应用

| 登录页面 | 数据概览 |
|---------|------------|
| <!-- 在此插入 Login 截图 --> | <!-- 在此插入 Dashboard 截图 --> |

| 消费记录管理 | 统计分析 |
|---------|---------|
| <!-- 在此插入记录列表截图 --> | <!-- 在此插入分类统计截图 --> |

---

## 技术栈

| 层次 | 技术 |
|------|------|
| **后端框架** | Spring Boot 3.5.13、MyBatis-Plus 3.5.7 |
| **前端框架** | Vue 3.4、Element Plus 2.6、ECharts 5.5、Pinia 2.1 |
| **数据库** | MySQL 8.0、Caffeine（本地缓存） |
| **安全认证** | JWT（jjwt）、BCrypt、Spring Security |
| **数据导出** | EasyExcel 3.3.2 |
| **接口文档** | Knife4j 4.4.0（Swagger UI） |
| **构建工具** | Maven 3.9、Vite 5.4 |
| **部署** | Docker、Docker Compose、Railway PaaS、GitHub Actions CI/CD |
| **测试** | JUnit 5、MockMvc |

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
- 基于 Redis/Caffeine 滑动窗口算法的接口限流（`@RateLimit` 注解）
- Spring Security + JWT 双重认证机制
- 完整的 Controller / Service 层单元测试（9个测试类）
- GitHub Actions 自动化 CI 流程，Docker 一键部署
- Railway PaaS 生产环境部署

---

## 快速开始

### 环境要求

**后端：**
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- （可选）Redis 6.0+ / Caffeine（本地缓存）

**前端：**
- Node.js 18+
- npm 9+ 或 pnpm 8+

---

## 构建与部署

### 一、本地开发环境

#### 1.1 后端启动（Spring Boot）

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE campus_dashboard CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 配置数据库连接
# 编辑 src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_dashboard?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码

# 3. 启动后端服务（系统自动执行 schema.sql 初始化表结构与测试数据）
mvn spring-boot:run

# 4. 访问 API 文档
# http://localhost:8080/doc.html
```

#### 1.2 前端启动（Vue 3）

```bash
# 1. 进入前端目录
cd src/main/resources/vue-frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev

# 4. 访问前端应用
# http://localhost:5174
```

**说明：**
- 后端运行在 `http://localhost:8080`（提供 API 服务）
- 前端运行在 `http://localhost:5174`（Vue 应用）
- 前端通过 Vite 代理转发 API 请求到后端

---

### 二、Docker 部署（生产环境推荐）

#### 2.1 使用 Docker Compose 一键部署

```bash
# 1. 复制环境变量配置文件
cp .env.example .env

# 2. 编辑 .env 文件，修改数据库密码和 JWT 密钥
vim .env

# 3. 启动所有服务（MySQL + Redis + Spring Boot）
docker-compose up -d

# 4. 查看日志
docker-compose logs -f app

# 5. 停止服务
docker-compose down

# 6. 停止并删除数据卷（谨慎使用）
docker-compose down -v
```

**服务端口：**
- 后端 API：`http://localhost:8080`
- MySQL：`localhost:3307`（映射到容器内 3306）
- Redis：`localhost:6379`

**注意事项：**
- 首次启动时，MySQL 会自动执行 `schema.sql` 初始化数据库
- 数据持久化存储在 Docker Volume 中（`mysql-data`、`redis-data`）
- 如需重置数据库，执行 `docker-compose down -v` 后重新启动

#### 2.2 单独构建 Docker 镜像

```bash
# 1. 构建镜像
docker build -t campus-dashboard:latest .

# 2. 运行容器（需先启动 MySQL 和 Redis）
docker run -d \
  --name campus-app \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/campus_dashboard \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e JWT_SECRET=your_jwt_secret \
  campus-dashboard:latest
```

---

### 三、Railway PaaS 部署（在线演示推荐）✨

本项目已成功部署到 Railway PaaS 平台，提供**永久免费的在线演示环境**。

#### 3.1 快速部署步骤

1. **Fork 本仓库**到你的 GitHub 账号

2. 访问 [railway.app](https://railway.app)，使用 GitHub 登录

3. 新建 Project，选择 `Deploy from GitHub repo`，导入你的 Fork 仓库

4. 在 Project 中添加 **MySQL** 插件（免费套餐包含 500MB 存储）

5. 在应用的 Variables 中配置以下环境变量（点击 `{}` 按钮选择变量引用）：

| 变量名 | 值（使用 Railway 变量引用语法） |
|--------|------------------------------|
| `DB_HOST` | `${{MySQL.MYSQLHOST}}` |
| `DB_PORT` | `${{MySQL.MYSQLPORT}}` |
| `DB_NAME` | `${{MySQL.MYSQLDATABASE}}` |
| `DB_USERNAME` | `${{MySQL.MYSQLUSER}}` |
| `DB_PASSWORD` | `${{MySQL.MYSQLPASSWORD}}` |
| `JWT_SECRET` | 自定义随机字符串（至少32位） |

6. **重要：** 删除默认的 `REDIS_HOST`、`REDIS_PORT` 等 Redis 相关变量（Railway 免费版不支持 Redis，系统会自动使用 Caffeine 本地缓存）

7. 等待自动部署完成（约 2-5 分钟）

8. 在 Settings → Networking 中开启公网访问（Generate Domain）

9. 访问生成的域名即可使用后端 API

#### 3.2 数据库初始化

首次部署后，需要手动执行 SQL 脚本创建表结构和测试数据：

1. 在 Railway Dashboard 中点击 MySQL 服务 → **Connect** → **Database** 标签页

2. 复制 `src/main/resources/sql/schema.sql` 文件内容

3. 粘贴到文本框中，点击 **Execute**

4. 刷新应用页面，即可看到测试数据

#### 3.3 前端部署建议

Railway 仅部署后端 API，前端 Vue 应用建议独立部署：

**方案一：Vercel（推荐）**
```bash
# 在 vue-frontend 目录下
cd src/main/resources/vue-frontend

# 安装 Vercel CLI
npm i -g vercel

# 部署到 Vercel
vercel
```

**方案二：Netlify**
```bash
# 构建前端应用
npm run build

# 将 dist 目录拖拽到 Netlify 控制面板
```

**方案三：GitHub Pages**
```bash
# 使用 gh-pages 包自动化部署
npm install -D gh-pages

# 在 package.json 中添加脚本
"scripts": {
  "deploy": "vite build && gh-pages -d dist"
}

# 部署
npm run deploy
```

**配置前端 API 地址：**
```javascript
// vue-frontend/.env.production
VITE_API_BASE_URL=https://your-railway-domain.up.railway.app
```

#### 3.4 查看部署状态

- **HTTP Logs**：查看所有 API 请求日志
- **Deployments**：查看每次代码推送后的构建和部署日志
- **Variables**：确认环境变量是否正确配置

---

### 四、数据库配置

#### 4.1 本地开发环境

编辑 `src/main/resources/application.properties`：

```properties
# 数据库连接
spring.datasource.url=jdbc:mysql://localhost:3306/campus_dashboard?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码

# Redis 配置（可选，未配置时自动使用 Caffeine 本地缓存）
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=

# JWT 密钥（生产环境请修改为随机字符串）
jwt.secret=your_jwt_secret_at_least_32_characters
```

#### 4.2 生产环境（Docker）

使用 `.env` 文件管理敏感信息：

```bash
# 复制示例文件
cp .env.example .env

# 编辑 .env 文件
DB_USERNAME=root
DB_PASSWORD=secure_password_here
JWT_SECRET=random_string_at_least_32_chars
```

Docker Compose 会自动读取 `.env` 文件中的变量。

#### 4.3 数据库初始化脚本

系统会在首次启动时自动执行 `src/main/resources/sql/schema.sql`，创建以下内容：

- **user 表**：用户信息表（含测试账号）
- **consumption_record 表**：消费记录表（含测试数据）
- **operation_log 表**：操作审计日志表

**手动执行（如需重置数据库）：**
```bash
mysql -u root -p campus_dashboard < src/main/resources/sql/schema.sql
```

---

### 五、访问地址

| 环境 | 后端 API | 前端应用 | API 文档 |
|------|---------|---------|----------|
| **本地开发** | `http://localhost:8080` | `http://localhost:5174` | `http://localhost:8080/doc.html` |
| **Docker** | `http://localhost:8080` | 需独立部署 | `http://localhost:8080/doc.html` |
| **Railway** | `https://your-domain.up.railway.app` | 需独立部署 | `https://your-domain.up.railway.app/doc.html` |

---

## 测试账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | 123456 | 管理员 | 可访问所有功能，包括操作日志 |
| zhangsan | 123456 | 普通用户 | 仅可查看自己的消费记录 |
| lisi | 123456 | 普通用户 | 仅可查看自己的消费记录 |

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
| GET | `/api/record/statistics/overview` | 数据概览（综合统计） |

> 完整接口文档请访问 Knife4j：`http://localhost:8080/doc.html`

---

## 数据库设计

### 核心表结构

**user 表**：用户信息表
- `id`：主键，自增
- `username`：用户名（唯一索引）
- `password`：BCrypt 加密后的密码
- `role`：角色（ADMIN / USER）
- `create_time`：创建时间

**consumption_record 表**：消费记录表
- `id`：主键，自增
- `user_id`：用户 ID（外键，关联 user.id）
- `category`：消费类别（食堂、超市、网吧等）
- `amount`：消费金额（DECIMAL(10,2)）
- `description`：备注说明
- `create_time`：消费时间
- **索引优化**：建立 `idx_user_id`、`idx_create_time`、`idx_category` 复合索引，提升查询性能

**operation_log 表**：操作审计日志表
- `id`：主键，自增
- `user_id`：操作用户 ID
- `username`：操作用户名
- `operation`：操作类型（ADD / UPDATE / DELETE / QUERY）
- `module`：操作模块
- `description`：操作描述
- `ip`：客户端 IP 地址
- `duration`：请求耗时（毫秒）
- `create_time`：操作时间
- **索引优化**：建立 `idx_user_id`、`idx_create_time` 索引

详细 SQL 脚本参见 `src/main/resources/sql/schema.sql`。

---

## 运行测试

```bash
mvn test
```

---

## 常见问题

### 1. 前端无法连接后端 API？

**问题：** Vue 应用请求 API 时出现 CORS 错误或 404。

**解决：**
- 确认后端服务已启动（`http://localhost:8080`）
- 检查 `vue-frontend/vite.config.js` 中的代理配置
- 确保后端允许跨域请求（已在 `WebConfig.java` 中配置）

### 2. 数据库初始化失败？

**问题：** 启动时提示表不存在或数据为空。

**解决：**
```bash
# 手动执行 SQL 脚本
mysql -u root -p campus_dashboard < src/main/resources/sql/schema.sql

# 或重启 Docker 容器（会自动重新初始化）
docker-compose down -v
docker-compose up -d
```

### 3. Railway 部署后无法访问？

**问题：** 部署成功但访问域名时返回错误。

**解决：**
- 检查 Variables 中是否正确配置了所有环境变量
- 确认 MySQL 插件已添加并正常运行
- 查看 HTTP Logs 和 Deployments 日志排查错误
- 确保已执行数据库初始化脚本

### 4. Redis 未启动会影响功能吗？

**回答：** 不会。系统使用了 Caffeine 作为本地缓存备选方案，当 Redis 不可用时会自动切换到 Caffeine。

### 5. 如何重置管理员密码？

**解决：**
```sql
-- 登录 MySQL
UPDATE user SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkkOQS3LjVVWxK6' WHERE username = 'admin';
-- 密码将被重置为 123456
```

### 6. Docker 构建速度慢？

**优化：**
- 使用国内 Maven 镜像（修改 `pom.xml`）
- 启用 Docker BuildKit：`export DOCKER_BUILDKIT=1`
- 利用 Docker 缓存层（Dockerfile 已优化）

---

## 许可证

[MIT License](LICENSE)

---

## 联系方式

- 作者：沈仕炜
- 邮箱：3228475260@qq.com
- GitHub：[github.com/hhd233994](https://github.com/hhd233994)
