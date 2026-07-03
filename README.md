# 个人博客 + AI 智能体平台

全栈博客系统，支持文章管理、分类标签、评论互动，并内置多模型 AI 智能体聊天。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Pinia + Vue Router |
| UI | Element Plus + Sass |
| Markdown | marked + highlight.js |
| 后端 | Spring Boot 3.2 + MyBatis-Plus + Spring Security |
| 数据库 | MySQL + Redis |
| 认证 | JWT（管理员 + 用户双端） |
| AI | 多模型调度（DeepSeek / Qwen / 豆包 / Moonshot / Zhipu / OpenAI） |
| 工具 | Hutool + FastJson2 + Lombok |

## 本地运行

### 环境要求

- Node.js >= 18
- Java 21
- Maven 3.9+
- MySQL 8.0
- Redis 7

### 1. 初始化数据库

在 MySQL 中依次执行：

```bash
mysql -u root -p < backend/src/main/resources/db/schema.sql
mysql -u root -p < blog_init.sql
```

### 2. 启动后端

```bash
cd backend
cp .env.example .env          # 编辑 .env 填入数据库密码等配置
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
│       ├── views/public/     # 公开页面
│       ├── api/              # 接口请求封装
│       ├── stores/           # Pinia 状态管理
│       ├── components/       # 公共组件
│       └── layouts/          # 布局组件
└── blog_init.sql             # 初始化数据
```

## 功能导览

- **文章系统**：Markdown 编辑、标签分类、归档、模糊搜索
- **评论系统**：嵌套回复、敏感词过滤
- **AI 智能体**：多模型切换、流式对话、会话管理
- **管理后台**：数据看板、内容管理、站点配置
- **主题**：Midnight Ink（墨夜书房）设计系统
