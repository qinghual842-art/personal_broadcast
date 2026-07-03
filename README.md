# 个人博客 + AI 智能体平台

全栈博客系统，支持文章管理、分类标签、评论互动，并内置多模型 AI 智能体聊天。用户可注册账号、发表文章、创建个人智能体。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Pinia + Vue Router |
| UI | Element Plus + Sass |
| Markdown | marked + highlight.js |
| 后端 | Spring Boot 3.2 + MyBatis-Plus + Spring Security |
| 数据库 | MySQL + Redis |
| 认证 | JWT（管理员 + 用户双端） |
| AI | 多模型调度（DeepSeek / 通义千问 / 豆包 / Moonshot / 智谱GLM / OpenAI） |
| 工具 | Hutool + FastJson2 + Lombok |

## 本地运行

### 环境要求

- Node.js >= 18
- Java 21
- Maven 3.9+
- MySQL 8.0
- Redis 7

### 1. 初始化数据库

```bash
mysql -u root -p < backend/src/main/resources/db/schema.sql
```

应用首次启动时会通过 `DataInitializer` 自动创建默认管理员和测试用户。

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

默认运行在 `http://localhost:8080`。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

默认运行在 `http://localhost:5173`。

## 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 管理员（后台） |
| test001 | testpass001 | 普通用户 |
| test002 | testpass002 | 普通用户 |

## 项目结构

```
├── backend/                  # Spring Boot 后端
│   └── src/main/java/com/blog/
│       ├── module/admin/     # 管理员认证
│       ├── module/article/   # 文章管理
│       ├── module/category/  # 分类管理
│       ├── module/tag/       # 标签管理
│       ├── module/comment/   # 评论管理
│       ├── module/agent/     # AI 智能体
│       ├── module/site/      # 站点配置
│       ├── module/user/      # 用户认证
│       ├── config/           # 安全/缓存/初始化配置
│       └── security/         # JWT 鉴权
├── frontend/                 # Vue 3 前端
│   └── src/
│       ├── views/admin/      # 管理后台页面
│       ├── views/public/     # 公开页面（首页/文章/用户/智能体）
│       ├── api/              # 接口请求封装
│       ├── stores/           # Pinia 状态管理
│       ├── components/       # 公共组件
│       └── layouts/          # 布局组件
└── docs/                     # 项目文档
```

## 功能导览

### 前台功能
- **用户系统**：注册/登录、头像上传、个人资料管理
- **文章系统**：Markdown 编辑、标签分类、归档、模糊搜索、作者展示
- **评论系统**：登录后评论、自动显示头像昵称、文章作者标识
- **AI 智能体**：系统默认智能体 + 用户自建智能体、多模型切换、会话管理、用户隔离聊天记录
- **智能体选择器**：首页侧边栏下拉切换不同智能体，对话记录独立保存
- **博客声明**：自定义站点声明页面

### 后台功能
- **数据看板**：文章/评论/用户统计
- **内容管理**：文章、分类、标签、评论的增删改查
- **智能体管理**：创建系统默认智能体，配置厂商/模型/密钥
- **站点配置**：站点名称、描述、关键词、博客声明

### 设计系统
- **Midnight Ink（墨夜书房）**：暖色调、金色点缀、衬线字体
- 响应式布局，适配桌面和移动端
