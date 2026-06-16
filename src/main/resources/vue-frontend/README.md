# Vue 3 + Element Plus 前端项目

## 项目说明

这是校园消费数据管理平台的 Vue 3 前端版本，使用以下技术栈：

- **Vue 3.4** - 渐进式 JavaScript 框架
- **Vite 5.2** - 下一代前端构建工具
- **Element Plus 2.6** - 基于 Vue 3 的组件库
- **Pinia 2.1** - Vue 状态管理库
- **Vue Router 4.3** - 官方路由管理器
- **Axios 1.6** - HTTP 客户端
- **ECharts 5.5** - 数据可视化图表库

## 已完成的文件结构

```
vue-frontend/
├── package.json              # 项目依赖配置
├── vite.config.js            # Vite 配置文件
├── index.html                # 入口 HTML
└── src/
    ├── main.js               # 应用入口
    ├── App.vue               # 根组件
    ├── api/                  # API 接口
    │   ├── request.js        # Axios 封装（拦截器）
    │   ├── auth.js           # 认证相关 API
    │   └── record.js         # 消费记录相关 API
    ├── stores/               # Pinia 状态管理
    │   └── user.js           # 用户状态
    ├── router/               # 路由配置
    │   └── index.js          # 路由定义和守卫
    ├── views/                # 页面组件
    │   └── Login.vue         # 登录页面 ✅ 已完成
    ├── components/           # 公共组件（待创建）
    └── assets/               # 静态资源（待创建）
```

## 安装依赖

```bash
cd vue-frontend
npm install
```

## 开发环境运行

```bash
npm run dev
```

访问 http://localhost:5173

## 生产环境构建

```bash
npm run build
```

构建后的文件会输出到 `../static/vue-dist/` 目录，Spring Boot 会自动 serving 这些静态文件。

## 需要继续创建的页面

### 1. Dashboard.vue（主布局）
- 侧边栏导航
- 顶部用户信息
- 路由出口

### 2. Overview.vue（数据概览）
- 统计卡片（总消费、今日消费、消费次数）
- ECharts 饼图（分类占比）
- ECharts 折线图（每日趋势）

### 3. Records.vue（消费记录）
- Element Plus Table 表格
- 分页组件
- 筛选表单（日期、类别）
- 导出 Excel 按钮

### 4. Statistics.vue（统计分析）
- ECharts 柱状图（分类统计详情）

### 5. Logs.vue（操作日志，仅管理员）
- Element Plus Table 表格
- 日志筛选

## 核心特性

✅ **自动导入**：Element Plus 组件和 API 自动导入，无需手动 import  
✅ **TypeScript 支持**：可选 TypeScript 类型提示  
✅ **响应式设计**：适配移动端和桌面端  
✅ **路由守卫**：登录验证和权限控制  
✅ **请求拦截**：自动携带 Token，统一错误处理  
✅ **状态管理**：Pinia 管理用户登录状态  

## 与原版 HTML 的对比

| 特性 | 原版 HTML | Vue 3 版本 |
|------|----------|-----------|
| 组件化 | ❌ | ✅ |
| 状态管理 | localStorage | Pinia |
| 路由管理 | 手动切换 | Vue Router |
| UI 组件 | TailwindCSS | Element Plus |
| 代码维护性 | 中等 | 高 |
| 开发体验 | 一般 | 优秀（热更新） |
| 性能 | 好 | 更好（虚拟 DOM） |

## 下一步

1. 创建剩余的页面组件（Dashboard, Overview, Records, Statistics, Logs）
2. 添加 ECharts 图表集成
3. 测试所有功能
4. 构建并部署

## 注意事项

- 开发时使用代理 `/api` → `http://localhost:8080`
- 生产环境构建后，直接访问 Spring Boot 的端口即可
- 确保后端 CORS 配置正确
