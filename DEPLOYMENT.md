# 部署指南

本文档详细说明校园消费数据管理平台的部署流程，包括本地开发、Docker 生产和 Railway PaaS 部署。

---

## 目录

- [本地开发环境](#本地开发环境)
- [Docker 生产部署](#docker-生产部署)
- [Railway PaaS 部署](#railway-paas-部署)
- [前端独立部署](#前端独立部署)
- [环境变量说明](#环境变量说明)
- [故障排查](#故障排查)

---

## 本地开发环境

### 后端启动

```bash
# 1. 确保 MySQL 8.0+ 已安装并运行
mysql -u root -p

# 2. 创建数据库
CREATE DATABASE campus_dashboard CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. 配置数据库连接
# 编辑 src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_dashboard?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码

# 4. 启动后端
mvn spring-boot:run

# 5. 验证 API 文档
# 访问 http://localhost:8080/doc.html
```

### 前端启动

```bash
# 1. 进入前端目录
cd src/main/resources/vue-frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev

# 4. 访问前端应用
# 浏览器打开 http://localhost:5174
```

**架构说明：**
- 后端提供 RESTful API（端口 8080）
- 前端通过 Vite 代理转发 API 请求到后端
- 开发时两个服务独立运行

---

## Docker 生产部署

### 快速开始

```bash
# 1. 复制环境变量配置
cp .env.example .env

# 2. 编辑 .env 文件
vim .env
# 修改 DB_PASSWORD 和 JWT_SECRET

# 3. 一键启动所有服务
docker-compose up -d

# 4. 查看日志
docker-compose logs -f app

# 5. 访问应用
# 后端 API: http://localhost:8080
# API 文档: http://localhost:8080/doc.html
```

### 服务组成

| 服务 | 镜像 | 端口 | 说明 |
|------|------|------|------|
| mysql | mysql:8.0 | 3307→3306 | 数据库，自动初始化 schema.sql |
| redis | redis:7-alpine | 6379 | 缓存（可选） |
| app | 自定义构建 | 8080 | Spring Boot 应用 |

### 常用命令

```bash
# 查看运行状态
docker-compose ps

# 重启单个服务
docker-compose restart app

# 查看应用日志
docker-compose logs -f app

# 停止服务（保留数据）
docker-compose down

# 停止并删除数据卷（谨慎使用）
docker-compose down -v

# 重新构建镜像
docker-compose build --no-cache
```

### 数据持久化

Docker Compose 使用 Volume 持久化数据：
- `mysql-data`：MySQL 数据库文件
- `redis-data`：Redis 持久化数据

**备份数据库：**
```bash
docker exec campus-mysql mysqldump -u root -p${DB_PASSWORD} campus_dashboard > backup.sql
```

**恢复数据库：**
```bash
docker exec -i campus-mysql mysql -u root -p${DB_PASSWORD} campus_dashboard < backup.sql
```

---

## Railway PaaS 部署

### 后端部署

1. **Fork 仓库**
   - Fork 本仓库到你的 GitHub 账号

2. **创建 Railway 项目**
   - 访问 [railway.app](https://railway.app)
   - 使用 GitHub 登录
   - New Project → Deploy from GitHub repo

3. **添加 MySQL 插件**
   - 在项目中点击 "New" → "Database" → "Add MySQL"
   - Railway 会自动 provision 一个 MySQL 实例

4. **配置环境变量**
   
   在应用的 Variables 标签页中配置：

   ```
   DB_HOST = ${{MySQL.MYSQLHOST}}
   DB_PORT = ${{MySQL.MYSQLPORT}}
   DB_NAME = ${{MySQL.MYSQLDATABASE}}
   DB_USERNAME = ${{MySQL.MYSQLUSER}}
   DB_PASSWORD = ${{MySQL.MYSQLPASSWORD}}
   JWT_SECRET = your_random_secret_at_least_32_chars
   ```

   **重要：** 点击每个值右侧的 `{}` 按钮选择变量引用，不要手动输入。

5. **删除 Redis 变量**
   - Railway 免费版不支持 Redis
   - 删除所有 `REDIS_*` 相关变量
   - 系统会自动使用 Caffeine 本地缓存

6. **等待部署完成**
   - 首次部署约需 2-5 分钟
   - 查看 Deployments 标签页监控进度

7. **开启公网访问**
   - Settings → Networking → Generate Domain
   - 获得类似 `https://xxx.up.railway.app` 的域名

8. **初始化数据库**
   - MySQL 服务 → Connect → Database 标签页
   - 复制 `src/main/resources/sql/schema.sql` 内容
   - 粘贴并执行

### 前端部署建议

Railway 仅适合部署后端 API，前端建议独立部署：

#### 方案一：Vercel（推荐）

```bash
cd src/main/resources/vue-frontend

# 安装 Vercel CLI
npm i -g vercel

# 登录
vercel login

# 部署
vercel

# 设置生产环境变量
vercel env add VITE_API_BASE_URL production
# 输入你的 Railway 后端地址
```

#### 方案二：Netlify

```bash
# 1. 构建前端
cd src/main/resources/vue-frontend
npm run build

# 2. 部署
# - 将 dist 目录拖拽到 Netlify 控制面板
# - 或连接 GitHub 仓库自动部署
```

#### 方案三：GitHub Pages

```bash
# 1. 安装 gh-pages
npm install -D gh-pages

# 2. 在 package.json 添加脚本
{
  "scripts": {
    "deploy": "vite build && gh-pages -d dist"
  }
}

# 3. 配置 vite.config.js 的 base
export default defineConfig({
  base: '/your-repo-name/',
  // ...
})

# 4. 部署
npm run deploy
```

---

## 环境变量说明

### 必需变量

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `DB_HOST` | MySQL 主机地址 | `localhost` 或 `${{MySQL.MYSQLHOST}}` |
| `DB_PORT` | MySQL 端口 | `3306` 或 `${{MySQL.MYSQLPORT}}` |
| `DB_NAME` | 数据库名称 | `campus_dashboard` |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `your_secure_password` |
| `JWT_SECRET` | JWT 签名密钥（至少32位） | `random_string_32_chars_min` |

### 可选变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Spring Profile |
| `SPRING_DATA_REDIS_HOST` | - | Redis 主机（未设置时使用 Caffeine） |
| `SPRING_DATA_REDIS_PORT` | `6379` | Redis 端口 |
| `JAVA_OPTS` | `-Xms128m -Xmx256m` | JVM 参数 |

### 安全建议

1. **永远不要提交 `.env` 文件到 Git**
   - 已在 `.gitignore` 中配置
   - 使用 `.env.example` 作为模板

2. **生产环境使用强密码**
   ```bash
   # 生成随机密码
   openssl rand -base64 32
   
   # 生成 JWT 密钥
   openssl rand -hex 32
   ```

3. **定期轮换密钥**
   - 每 90 天更换一次 JWT_SECRET
   - 更新后需要用户重新登录

---

## 故障排查

### 问题 1：容器启动失败

**症状：** `docker-compose up` 后 app 容器退出

**排查步骤：**
```bash
# 1. 查看日志
docker-compose logs app

# 2. 检查 MySQL 是否就绪
docker-compose logs mysql

# 3. 确认网络连接正常
docker exec -it campus-app ping mysql
```

**常见原因：**
- MySQL 未完全启动（等待 30 秒后重试）
- 数据库密码错误（检查 `.env` 文件）
- 端口冲突（修改 docker-compose.yml 中的端口映射）

### 问题 2：前端无法连接后端

**症状：** Vue 应用显示网络错误

**解决：**
```javascript
// 检查 vue-frontend/vite.config.js 的代理配置
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    }
  }
}
```

**生产环境：**
- 确保前端构建时设置了正确的 API 地址
- 检查 CORS 配置（后端已允许跨域）

### 问题 3：数据库初始化失败

**症状：** 表不存在或数据为空

**解决：**
```bash
# 方法 1：手动执行 SQL
mysql -u root -p campus_dashboard < src/main/resources/sql/schema.sql

# 方法 2：重置 Docker 数据卷
docker-compose down -v
docker-compose up -d
```

### 问题 4：Railway 部署后 502 错误

**症状：** 访问域名返回 502 Bad Gateway

**排查：**
1. 检查 Variables 是否正确配置（特别是 MySQL 变量引用）
2. 查看 HTTP Logs 确认应用是否启动
3. 确认 MySQL 插件已添加
4. 检查是否执行了数据库初始化脚本

**解决：**
- 重新部署（Push 代码或手动 Trigger Deploy）
- 检查环境变量是否使用了 Railway 变量引用语法

### 问题 5：内存不足（OOM）

**症状：** 容器被杀死，日志显示 "Out of memory"

**解决：**
```dockerfile
# 调整 Dockerfile 中的 JVM 参数
ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseG1GC"

# Railway 免费套餐限制 512MB RAM
# 建议设置：-Xms128m -Xmx384m
```

---

## 性能优化建议

### 后端优化

1. **启用 Gzip 压缩**
   ```properties
   server.compression.enabled=true
   server.compression.mime-types=application/json,text/html
   ```

2. **调整线程池**
   ```properties
   server.tomcat.threads.max=200
   server.tomcat.threads.min-spare=10
   ```

3. **数据库连接池**
   ```properties
   spring.datasource.hikari.maximum-pool-size=10
   spring.datasource.hikari.minimum-idle=5
   ```

### 前端优化

1. **代码分割**
   ```javascript
   // 路由懒加载
   const Dashboard = () => import('@/views/Dashboard.vue')
   ```

2. **按需引入 ECharts**
   ```javascript
   // 只引入需要的模块
   import * as echarts from 'echarts/core'
   import { LineChart, PieChart } from 'echarts/charts'
   ```

3. **启用 Gzip/Brotli**
   ```nginx
   # Nginx 配置
   gzip on;
   brotli on;
   ```

---

## 监控与维护

### 健康检查

```bash
# 后端健康检查
curl http://localhost:8080/actuator/health

# 数据库连接测试
curl http://localhost:8080/api/record/statistics/overview \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 日志管理

```bash
# 查看实时日志
docker-compose logs -f app

# 导出日志到文件
docker-compose logs app > app.log

# 查看最近 100 行
docker-compose logs --tail=100 app
```

### 定期维护

1. **清理 Docker 资源**
   ```bash
   docker system prune -a
   ```

2. **备份数据库**
   ```bash
   # 每周备份一次
   0 2 * * 0 docker exec campus-mysql mysqldump -u root -p${DB_PASSWORD} campus_dashboard > /backup/db_$(date +\%Y\%m\%d).sql
   ```

3. **更新依赖**
   ```bash
   # 后端
   mvn versions:display-dependency-updates
   
   # 前端
   npm outdated
   npm update
   ```

---

## 联系支持

如遇到部署问题，请：
1. 查看本文档的"故障排查"章节
2. 检查 GitHub Issues 是否有类似问题
3. 提交新 Issue 并提供：
   - 部署环境（本地/Docker/Railway）
   - 错误日志
   - 复现步骤

---

**最后更新：** 2026-06-15
