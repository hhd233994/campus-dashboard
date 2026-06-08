# 🎓 校园消费数据管理平台

> 基于 Spring Boot 3 + MyBatis-Plus + Redis 的全栈校园消费管理系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.7-blue.svg)](https://baomidou.com/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📋 项目简介

这是一个功能完善的校园消费数据管理平台，实现了用户认证、消费记录管理、数据统计分析、Excel导出等核心功能。项目采用前后端分离架构，后端使用Spring Boot 3最新技术栈，前端使用原生HTML/CSS/JavaScript + ECharts实现数据可视化。

### ✨ 核心特性

- 🔐 **JWT认证** - 无状态Token认证，支持登录/注册
- 🛡️ **RBAC权限控制** - 基于角色的访问控制（ADMIN/USER）
- 📊 **数据可视化** - ECharts图表展示消费趋势和分类占比
- 🔍 **智能筛选** - 支持按日期范围和消费类别多维度筛选
- 📥 **Excel导出** - 基于EasyExcel实现数据导出功能
- 💾 **Redis缓存** - 统计数据缓存优化查询性能
- 📝 **操作日志** - 完整的审计日志系统，记录用户操作
- 🚦 **接口限流** - 防止恶意刷接口，保护系统安全
- 🔒 **BCrypt加密** - 企业级密码加密，抗彩虹表攻击
- 📱 **响应式设计** - 适配不同屏幕尺寸
- 🛡️ **参数校验** - JSR-303标准参数验证
- 📝 **API文档** - Knife4j在线接口文档
- 🐳 **Docker部署** - 一键启动整套环境

---

## 🏗️ 系统架构

### 技术架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层 (Frontend)                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ HTML5    │  │ Tailwind │  │ ECharts  │  │ Fetch API│   │
│  │ CSS3     │  │   CSS    │  │   5.4    │  │          │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/HTTPS + JWT Token
┌────────────────────────▼────────────────────────────────────┐
│                     网关层 (Gateway)                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         JwtInterceptor (JWT认证拦截器)                │  │
│  │  • Token验证  • 用户信息提取  • 权限校验              │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                   控制层 (Controller)                         │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐      │
│  │  Auth    │ │ Record   │ │   File   │ │   Log    │      │
│  │Controller│ │Controller│ │Controller│ │Controller│      │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘      │
│  @RateLimit  @RequireRole  @OperationLog                   │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                   业务层 (Service)                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  AOP切面:                                             │  │
│  │  • RateLimitAspect (接口限流)                         │  │
│  │  • RoleAspect (权限控制)                              │  │
│  │  • OperationLogAspect (操作日志)                      │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐      │
│  │ UserService│RecordSvc │ FileService│ LogService │      │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘      │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                   数据层 (Data Access)                        │
│  ┌──────────────────┐  ┌──────────────────┐                │
│  │  MyBatis-Plus    │  │     Redis        │                │
│  │  • Mapper接口    │  │  • 缓存统计       │                │
│  │  • XML映射       │  │  • 限流计数       │                │
│  │  • Lambda查询    │  │  • Session存储    │                │
│  └────────┬─────────┘  └────────┬─────────┘                │
│           │                     │                           │
│  ┌────────▼─────────────────────▼─────────┐                │
│  │         MySQL 8.0 数据库                │                │
│  │  • user表  • consumption_record表      │                │
│  │  • operation_log表  • 索引优化          │                │
│  └────────────────────────────────────────┘                │
└─────────────────────────────────────────────────────────────┘
```

### 核心业务流程

#### 1. 用户登录流程
```
用户输入账号密码 → AuthController.login()
    ↓
UserService验证用户名密码(BCrypt)
    ↓
JwtUtil生成Token(userId, username, role)
    ↓
返回Token给前端 → 前端存储到localStorage
    ↓
后续请求携带Token → JwtInterceptor验证
```

#### 2. 权限控制流程
```
请求到达 → @RequireRole("ADMIN")注解
    ↓
RoleAspect切面拦截
    ↓
从Request获取用户角色
    ↓
角色匹配？→ 是：继续执行 / 否：抛出异常
```

#### 3. 接口限流流程
```
请求到达 → @RateLimit(timeWindow=60, maxRequests=10)
    ↓
RateLimitAspect切面拦截
    ↓
Redis.increment(key) 计数
    ↓
超过限制？→ 是：返回429 / 否：继续执行
    ↓
设置过期时间实现滑动窗口
```

---

## 💾 数据库设计

### ER图

```
┌─────────────────────┐       ┌──────────────────────────┐
│       user          │       │   consumption_record     │
├─────────────────────┤       ├──────────────────────────┤
│ *id (BIGINT)        │───┐   │ *id (BIGINT)             │
│  username (VARCHAR) │   └──>│  user_id (BIGINT) FK     │
│  password (VARCHAR) │       │  category (VARCHAR)      │
│  real_name         │       │  amount (DECIMAL)        │
│  email             │       │  description             │
│  phone             │       │  create_time (DATETIME)  │
│  role (ENUM)       │       └──────────────────────────┘
│  create_time       │                ▲
│  update_time       │                │ 1:N
└─────────────────────┘                │
                                       │
                          ┌──────────────────────────┐
                          │    operation_log         │
                          ├──────────────────────────┤
                          │ *id (BIGINT)             │
                          │  user_id (BIGINT) FK     │
                          │  username (VARCHAR)      │
                          │  operation (VARCHAR)     │
                          │  module (VARCHAR)        │
                          │  description             │
                          │  ip (VARCHAR)            │
                          │  method (VARCHAR)        │
                          │  status_code (INT)       │
                          │  duration (BIGINT)       │
                          │  create_time (DATETIME)  │
                          └──────────────────────────┘
```

### 表结构说明

#### 1. user表 - 用户信息表
| 字段 | 类型 | 说明 | 索引 |
|------|------|------|------|
| id | BIGINT | 主键，自增 | PRIMARY KEY |
| username | VARCHAR(50) | 用户名，唯一 | UNIQUE INDEX |
| password | VARCHAR(255) | BCrypt加密密码 | - |
| real_name | VARCHAR(50) | 真实姓名 | - |
| email | VARCHAR(100) | 邮箱 | - |
| phone | VARCHAR(20) | 手机号 | - |
| role | VARCHAR(20) | 角色：USER/ADMIN | - |
| create_time | DATETIME | 创建时间 | - |
| update_time | DATETIME | 更新时间 | - |

#### 2. consumption_record表 - 消费记录表
| 字段 | 类型 | 说明 | 索引 |
|------|------|------|------|
| id | BIGINT | 主键，自增 | PRIMARY KEY |
| user_id | BIGINT | 用户ID，外键 | INDEX idx_user_id |
| category | VARCHAR(50) | 消费类别 | INDEX idx_category |
| amount | DECIMAL(10,2) | 消费金额 | - |
| description | VARCHAR(255) | 备注说明 | - |
| create_time | DATETIME | 创建时间 | INDEX idx_create_time |

**索引优化说明**：
- `idx_user_id`: 加速按用户查询消费记录
- `idx_create_time`: 加速按时间范围筛选
- `idx_category`: 加速按类别统计

#### 3. operation_log表 - 操作日志表
| 字段 | 类型 | 说明 | 索引 |
|------|------|------|------|
| id | BIGINT | 主键，自增 | PRIMARY KEY |
| user_id | BIGINT | 用户ID | INDEX idx_user_id_log |
| username | VARCHAR(50) | 用户名 | - |
| operation | VARCHAR(20) | 操作类型：ADD/UPDATE/DELETE/QUERY | - |
| module | VARCHAR(50) | 模块名称 | - |
| description | VARCHAR(255) | 操作描述 | - |
| ip | VARCHAR(50) | 请求IP | - |
| method | VARCHAR(100) | 请求方法 | - |
| status_code | INT | 响应状态码 | - |
| duration | BIGINT | 耗时（毫秒） | - |
| create_time | DATETIME | 创建时间 | INDEX idx_create_time_log |

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 启动方式

#### 方式一:Maven直接运行(开发推荐)

```bash
mvn spring-boot:run
```

**优点**:支持热重载,适合开发调试

#### 方式二:使用启动脚本

**Windows**:
```bash
start.bat
```

**Linux/Mac**:
```bash
chmod +x start.sh
./start.sh
```

#### 方式三:Docker一键部署(生产推荐)

```bash
docker-compose up -d
```

**优点**:自动启动MySQL、Redis和应用,环境隔离

### 启动前准备

#### 🆕 方式一：自动初始化（推荐）

项目已配置数据库自动初始化，只需：

1. **确保MySQL和Redis已启动**
2. **创建空数据库**：
```sql
CREATE DATABASE campus_dashboard CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
3. **直接启动项目**，系统会自动执行 `schema.sql` 创建表和插入测试数据

#### 方式二：手动初始化

如果你想手动控制数据库初始化：

1. **创建数据库**：
```sql
CREATE DATABASE campus_dashboard CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **初始化数据**：

```bash
mysql -u root -p campus_dashboard < src/main/resources/sql/schema.sql
```

#### 3. 配置应用

修改 `src/main/resources/application.properties`:

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/campus_dashboard?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码

# Redis配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=

# JWT配置
jwt.secret=your-secret-key-here-change-this-in-production
jwt.expiration=604800000
```

#### 4. 启动Redis

```bash
# Windows
redis-server

# Linux/Mac
redis-server --daemonize yes
```

### 访问应用

- **前端页面**: http://localhost:8080/index.html
- **API文档**: http://localhost:8080/doc.html

> 💡 **提示**：启动项目后，先访问API文档测试接口，再使用前端页面。

---

## 📖 使用说明

### 测试账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | 123456 | ADMIN | 管理员账号 |
| zhangsan | 123456 | USER | 普通用户（有消费记录） |
| lisi | 123456 | USER | 普通用户（有消费记录） |

### 主要功能

#### 1. 用户认证

- **登录**: 输入用户名和密码，获取JWT Token
- **注册**: 创建新用户账号
- **自动登录**: Token存储在localStorage，刷新页面保持登录状态

#### 2. 数据概览（Dashboard）

- 总消费金额统计
- 今日消费金额
- 消费次数统计
- 消费类别占比饼图
- 每日消费趋势折线图

#### 3. 消费记录管理

- **分页列表**: 展示所有消费记录，支持翻页
- **筛选功能**: 
  - 按日期范围筛选（开始日期 - 结束日期）
  - 按消费类别筛选（食堂、超市、网吧等12种分类）
- **重置筛选**: 一键清除所有筛选条件
- **导出Excel**: 将当前数据导出为Excel文件

#### 4. 统计分析

- 分类详细统计（柱状图+折线图）
- 各分类消费次数和总金额对比

---

## 🔌 API接口

### 认证接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/register` | POST | 用户注册 |

### 消费记录接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/record/add` | POST | 新增消费记录 |
| `/api/record/list` | GET | 分页查询（支持筛选） |
| `/api/record/user/{userId}` | GET | 查询用户消费记录 |
| `/api/record/{id}` | DELETE | 删除消费记录 |
| `/api/record/export/excel` | GET | 导出Excel |

### 统计接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/record/statistics/category` | GET | 分类统计 |
| `/api/record/statistics/daily` | GET | 每日统计 |
| `/api/record/statistics/total` | GET | 总消费金额 |
| `/api/record/statistics/today` | GET | 今日消费金额 |
| `/api/record/statistics/overview` | GET | 数据概览 |

### 文件上传接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/file/upload` | POST | 上传文件 |

> 详细接口文档请访问：http://localhost:8080/doc.html

---

## 🧪 测试

### 单元测试

```bash
mvn test
```

### API测试

使用Knife4j进行接口测试：
1. 访问 http://localhost:8080/doc.html
2. 选择要测试的接口
3. 填写参数
4. 点击"发送"按钮

---

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

---

## 👨‍💻 作者

**贵州师范大学计算机专业学生项目**

---

## 🙏 致谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)
- [ECharts](https://echarts.apache.org/)
- [EasyExcel](https://github.com/alibaba/easyexcel)
- [Knife4j](https://doc.xiaominfo.com/)

---

## 📚 相关文档

- [🎯 贵阳本地实习指南](GUIYANG_INTERNSHIP_GUIDE.md) - **必读**：贵阳IT公司、投递渠道、面试技巧
- [🎤 面试核心问题](INTERVIEW_QA_SIMPLE.md) - 10个最常问的技术问题及回答
- [📄 简历项目描述](RESUME_TEMPLATE_SIMPLE.md) - 2个实用的简历模板
- [📝 项目完善报告](PROJECT_IMPROVEMENTS.md) - 项目优化记录

---

## 📞 联系方式

如有问题或建议，欢迎提Issue或联系作者。

---

**⭐ 如果这个项目对你有帮助，请给一个Star！**
