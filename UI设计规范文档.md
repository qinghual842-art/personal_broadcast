# 个人博客 — UI 设计规范文档

**设计系统**：Midnight Ink（墨夜书房）
**灵感来源**：温暖纸张质感 + 琥珀金点缀 + 深邃蓝黑文字
**气质定位**：文人书房 · 优雅温润 · 科技暗涌
**最后更新**：2026-07-02

---

# 一、设计总纲

## 1.1 设计理念

打造"墨夜书房"视觉定位——如同深夜书房中一盏暖灯下的阅读体验，温暖纸张质感搭配琥珀金点缀，沉静而有力量。

核心原则：
- **温润感**：温暖纸张色背景（`#f7f5f2`），柔和暖色调阴影，避免冷冰冰的纯白
- **书卷气**：Playfair Display 衬线标题字体 + DM Sans 现代无衬线正文，层次分明
- **克制感**：琥珀金（`#d4a853`）作为唯一强调色，点到为止，不喧宾夺主
- **呼吸感**：32px 模块间距、24px 卡片内边距，基于 8px 基准的间距系统

## 1.2 主题系统

项目支持两套主题，通过 CSS 变量一键切换：

| 主题 | 类型 | 适用场景 |
|------|------|----------|
| Midnight Ink（默认） | 亮色·暖纸 | 日常浏览 |
| Ink Well | 暗色 | 夜间阅读 |

---

# 二、色彩系统

## 2.1 主色系 — 深海蓝黑 Midnight

```
--color-primary-50:  #e8ecf4    最浅，背景高亮
--color-primary-100: #c5cee3    浅，标签背景
--color-primary-200: #9faed0    中浅，边框强调
--color-primary-300: #788ebd    中，hover 边框
--color-primary-400: #5874ac    中深，聚焦边框
--color-primary-500: #3d5a96    品牌主色
--color-primary-600: #2d4778    深，hover 状态
--color-primary-700: #22365c    更深，active 状态
--color-primary-800: #182847    最深，文字强调
--color-primary-900: #0f1c33    最深色
--color-primary-950: #0a0e1a
```

## 2.2 强调色 — 暖琥珀金 Amber Gold

```
--color-amber-50:  #fdf8f0    最浅，暖底
--color-amber-100: #faf0de    浅底
--color-amber-200: #f5e0b8    浅边框
--color-amber-300: #f5d89a    中浅
--color-amber-400: #edc76a    中，渐变终点
--color-amber-500: #d4a853    品牌强调色（标签、按钮渐变、hover 态、装饰线）
--color-amber-600: #c29540    深，渐变起点
--color-amber-700: #a67b2e    最深，文字强调
```

## 2.3 辅助色 — 墨玉绿 Ink Jade

```
--color-jade-400: #4fd1a5
--color-jade-500: #2cb685    成功状态
--color-jade-600: #1f9a6e
--color-jade-700: #177d58
```

## 2.4 语义色

| 用途 | 颜色 | 色值 |
|------|------|------|
| 成功 | Jade | `#2cb685` |
| 警告 | Amber | `#d4a853` |
| 危险 | Red | `#c44040` |
| 信息 | Midnight | `#3d5a96` |

## 2.5 背景色系 — 温暖纸张质感

```
--bg-page:          #f7f5f2    页面底色，温暖纸张色
--bg-soft:          #ede9e3    柔和背景
--bg-surface:       #ffffff    卡片/面板表面
--bg-surface-2:     #f0ece6    次级表面
--bg-surface-hover: #f5f1eb    悬浮态表面
--bg-subtle:        #eae5dd    微妙强调背景
--bg-accent-light:  #f0ece6    琥珀浅底
--bg-warm:          #faf6f0    暖色背景（Hero / Profile 渐变起点）
```

## 2.6 文字色系

```
--text-primary:    #1c1a17    主文字，深黑褐
--text-secondary:  #37322d    次要文字
--text-tertiary:   #8b7e6e    辅助文字 / 元信息
--text-placeholder:#c4b9a9    占位符
--text-inverse:    #f7f5f2    反色文字
```

## 2.7 边框色

```
--border-default:  rgba(28, 26, 23, 0.10)    默认边框
--border-light:    rgba(28, 26, 23, 0.06)    浅边框
--border-strong:   rgba(28, 26, 23, 0.20)    深边框
--border-accent:   rgba(212, 168, 83, 0.30)  琥珀金边框
--border-focus:    #d4a853                   聚焦边框
```

## 2.8 阴影 — 暖色调

所有阴影统一带暖褐色调，营造温暖纸质层次感：

```css
--shadow-xs:   0 1px 2px rgba(28, 26, 23, 0.04);
--shadow-sm:   0 1px 3px rgba(28, 26, 23, 0.06), 0 1px 2px rgba(28, 26, 23, 0.04);
--shadow-md:   0 2px 8px rgba(28, 26, 23, 0.07), 0 12px 32px rgba(28, 26, 23, 0.05);
--shadow-lg:   0 8px 24px rgba(28, 26, 23, 0.10), 0 30px 80px rgba(28, 26, 23, 0.06);
--shadow-xl:   0 20px 40px rgba(28, 26, 23, 0.12), 0 40px 100px rgba(28, 26, 23, 0.08);
--shadow-card: 0 1px 3px rgba(28, 26, 23, 0.04), 0 8px 24px rgba(28, 26, 23, 0.05);
--shadow-glow: 0 0 40px rgba(212, 168, 83, 0.08);
```

## 2.9 渐变

```css
--gradient-primary:  linear-gradient(135deg, #3d5a96, #d4a853);    /* 蓝→金 主渐变 */
--gradient-soft:     linear-gradient(135deg, #f7f5f2, #faf6f0);    /* 柔和纸色渐变 */
--gradient-accent:   linear-gradient(135deg, #d4a853, #c29540);    /* 琥珀金渐变 */
--gradient-gold:     linear-gradient(135deg, #d4a853, #edc76a);    /* 亮金渐变 */
--gradient-gold-blue:linear-gradient(135deg, #3d5a96, #d4a853);   /* 蓝金渐变（分页器active） */
--gradient-warm:     linear-gradient(180deg, #faf6f0, #f0ece6);    /* 温暖渐变（Hero Banner） */
```

---

# 三、字体系统

## 3.1 字体族

| 用途 | 字体栈 |
|------|--------|
| 展示标题 | `'Playfair Display', 'Noto Serif SC', Georgia, 'STSong', serif` |
| 正文阅读 | `'DM Sans', 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Microsoft YaHei', sans-serif` |
| 衬线装饰 | 同展示标题 |
| 代码等宽 | `'JetBrains Mono', 'IBM Plex Mono', 'SF Mono', 'Consolas', monospace` |

## 3.2 字号层级

| Token | 大小 | 用途 |
|-------|------|------|
| `--text-xs` | 12px | 标签、脚注 |
| `--text-sm` | 13px | 辅助文字 |
| `--text-base` | 14px | 正文（Element Plus 默认） |
| `--text-md` | 15px | 增强正文 |
| `--text-lg` | 18px | 小标题、卡片标题 |
| `--text-xl` | 22px | 区块标题 |
| `--text-2xl` | 28px | 页面副标题 |
| `--text-3xl` | 36px | 页面标题 |
| `--text-4xl` | 48px | Hero 大标题 |

## 3.3 排版工具类

- `.page-title` — 页面主标题（font-serif, 48px, 700）
- `.section-title` — 区块标题（font-serif, 28px, 700）
- `.article-title` — 文章标题（font-serif, 22px, 600）
- `.body-text` — 正文（15px, line-height 1.8）
- `.gradient-text` — 蓝→金渐变文字
- `.gradient-gold` — 琥珀金渐变文字
- `.gradient-flow` — 渐变动画流动文字
- `.serif` / `.mono` — 字体切换工具类

---

# 四、形状与空间

## 4.1 圆角 — 温润感

| Token | 值 | 用途 |
|-------|-----|------|
| `--radius-xs` | 3px | 小标签、徽章角标 |
| `--radius-sm` | 6px | 输入框、小标签 |
| `--radius-md` | 10px | 按钮、菜单项（Element Plus 默认） |
| `--radius-lg` | 14px | 卡片 |
| `--radius-xl` | 20px | 大卡片、对话框 |
| `--radius-2xl` | 28px | Hero 区域、特殊容器 |

## 4.2 间距（8px 基准）

| Token | 值 | 用途 |
|-------|-----|------|
| `--space-1` | 4px | 极小间距 |
| `--space-2` | 8px | 图标与文字间距 |
| `--space-3` | 12px | 标签间距 |
| `--space-4` | 16px | 元素间距 |
| `--space-5` | 20px | 列表项间距 |
| `--space-6` | 24px | 卡片内边距 |
| `--space-8` | 32px | 模块间距 |
| `--space-10` | 40px | Hero 内边距 |
| `--space-12` | 48px | 大区块间距 |

模块间距 32px，卡片内边距 24px，段落间距 12px。

## 4.3 过渡缓动

```css
--ease-out:    cubic-bezier(0.4, 0, 0.2, 1);         /* 标准缓出 */
--ease-spring: cubic-bezier(0.22, 1.3, 0.36, 1);     /* 弹性缓动（搜索框展开） */
--ease-ink:    cubic-bezier(0.65, 0, 0.35, 1);       /* 墨韵缓动（路由过渡） */
--transition-fast:   150ms var(--ease-out);
--transition-normal: 300ms var(--ease-out);
--transition-slow:   500ms var(--ease-ink);
```

---

# 五、组件规范

## 5.1 卡片系统

```css
.content-card     /* 标准白底卡片：圆角14px + 浅边框 + 暖色阴影，hover 上浮2px + 边框变金 */
.card-accent      /* 顶部 2px 琥珀金强调线 */
.card-hover       /* hover 上浮 4px + 深阴影 */
```

卡片 hover 统一效果：`translateY(-2px)` + `box-shadow: var(--shadow-md)` + `border-color: var(--border-accent)`

## 5.2 标签/徽章

```css
.pill             /* 标准药丸标签：圆角999px，灰色底 */
.pill-accent      /* 深海蓝浅底 + 蓝文字 */
.pill-gold        /* 琥珀金浅底 + 金文字 */
```

## 5.3 按钮

- **主按钮**：深海蓝→琥珀金渐变，白色文字，hover 加深 + 上浮 1px + 琥珀色阴影
- **登录/注册按钮**：琥珀金渐变（`amber-500 → amber-600`），白色文字，圆角 999px，hover 上浮 + 阴影加深
- **默认按钮**：白色背景 + 灰色边框，hover 琥珀金边框 + 暖色浅底

## 5.4 导航栏

- 白色 85% 透明度毛玻璃效果（`backdrop-filter: blur(16px) saturate(180%)`）
- 高度 64px，sticky 置顶，z-index 100
- 底部 1px 琥珀金色渐变装饰线（中间亮、两端透明）
- **Logo**：紫色渐变 SVG 图标（`#7c3aed → #6366f1 → #8b5cf6`）+ 站点名称（Playfair Display 衬线字体）
- Logo hover：旋转 `-2deg` + 缩放 `1.03` + 紫色发光投影
- **导航链接**：15px / 500 字重，hover 时底部琥珀金色下划线从 `width: 0 → 60%`，active 保持下划线
- **搜索框**：pill 形状（`border-radius: 999px`），180px 宽度，聚焦弹性展开至 280px（`cubic-bezier(0.22, 1.3, 0.36, 1)`）
- **设置按钮**：三点/叉号图标切换，34px 方形圆角按钮

## 5.5 后台侧边栏

- 宽度 220px，白色背景
- 左侧 3px 琥珀金渐变装饰条
- 菜单项：圆角 10px，active 态琥珀金渐变浅底 + 琥珀色文字 + 金色细边框
- 底部"返回站点"链接

## 5.6 Element Plus 覆盖

统一将 Element Plus 组件融入 Midnight Ink 设计系统：
- 主色覆盖为深海蓝 `#3d5a96`
- 聚焦环使用琥珀金 `rgba(212, 168, 83, 0.10)`
- 表格表头使用 `--bg-warm` 暖色背景，文字大写 + 字间距
- 分页器 active 页码使用蓝金渐变 + 琥珀色阴影
- 对话框圆角 20px
- 菜单 active 态琥珀金渐变浅底 + 金色边框

---

# 六、动画系统

## 6.1 入场动画

| 类名/动画名 | 效果 |
|------|------|
| `fadeInUp` | 上移 20px 淡入 |
| `fadeInDown` | 下移 20px 淡入 |
| `fadeIn` | 纯淡入 |
| `inkRise` | 上移 30px + scale(0.98) + blur(3px) → 清晰（墨韵升起） |
| `slideInLeft` | 左移 20px 淡入 |
| `slideInRight` | 右移 20px 淡入 |
| `zoomPop` | 弹性缩放弹出（scale 0.85 → 1.01 → 1） |

## 6.2 持续动画

| 动画名 | 效果 |
|------|------|
| `shimmer` | 微光扫过（光泽掠过表面） |
| `gradientFlow` | 渐变文字流动（蓝→金→蓝循环） |
| `pulseGlow` | 琥珀金脉冲发光 |
| `warmBreath` | 琥珀金环呼吸效果（opacity 0.4 ↔ 0.8，4s周期） |
| `goldShimmer` | 金色光泽扫过 |
| `gentleFloat` | 轻柔上下浮动（6px，3s周期） |

## 6.3 交错的列表入场

`.stagger-list > *` 子元素依次触发 `inkRise` 动画，每个延迟约 0.07s，营造书页逐张展开的感觉。

## 6.4 路由过渡 — 墨韵翻页

页面切换使用 `page-fade` 过渡：

- **进入**：`translateY(16px) + scale(0.98) + blur(4px)` → 清晰，0.4s `ease-ink` 缓动
- **离开**：清晰 → `translateY(-10px) + scale(0.99) + blur(2px)`，0.3s 缓动

## 6.5 微交互

- 导航栏设置面板：下拉展开/收起带弹性缩放（`scale 0.97 → 1`）
- 卡片 hover：`translateY(-2px)` + 阴影加深 + 边框变琥珀金
- 文章卡片 hover：左侧 3px 琥珀金渐变装饰条淡入 + "阅读全文"箭头从左侧滑入
- 封面图 hover：`scale(1.05)` 缩放
- AI 助手卡片 hover：上浮 2px + 阴影加深 + 右箭头右移 3px
- 标签药丸 hover：琥珀色边框 + 上浮 1px
- 统计卡片 hover：上浮 2px + 金色边框 + 阴影加深

---

# 七、页面布局规范

## 7.1 前台公共布局

```
┌──────────────────────────────────────────────┐
│  PublicHeader (sticky, 64px, 毛玻璃)          │
│  ┌─Logo─┬──导航链接──┬─搜索框─┬─登录按钮─┐    │
│  │紫色渐变│ 5个链接   │pill形  │ 琥珀金   │    │
│  └───────┴──────────┴────────┴──────────┘    │
│  ═══════════════════════════════════════════  │ ← 金色渐变装饰线
├──────────────────────────────────────────────┤
│                                              │
│  main-content (flex: 1)                      │
│  └─ container (max-width: 1400px)            │
│     ┌──────────────┬─────────────┐           │
│     │  主内容区     │  右侧边栏   │           │
│     │  (flex:1)    │  (340px)    │           │
│     │  min-w:0     │  sticky     │           │
│     │              │  top:80px   │           │
│     └──────────────┴─────────────┘           │
│                                              │
├──────────────────────────────────────────────┤
│  PublicFooter                                │
└──────────────────────────────────────────────┘
```

## 7.2 首页布局

- **Hero Banner**：温暖渐变背景（`--gradient-warm`），圆角 20px
  - 博主头像（80px，琥珀金光环呼吸动画）+ 昵称（Playfair Display）+ 签名
  - 右侧三张数据统计小卡片（文章数/分类数/标签数），hover 上浮
- **文章列表**：纵向排列，卡片间距 20px，交错入场动画
  - 左侧分类标签（琥珀金浅底 + 金色文字）+ 标题（衬线体）+ 摘要（2行截断）
  - 封面图右侧展示（200×130px），hover 缩放 1.05
  - 底部：日期 + 阅读量 + 评论数 + "阅读全文"（hover 滑入）
- **分页器**：居中，active 页码蓝金渐变 + 琥珀色阴影
- **右侧边栏**（340px，sticky top:80px）：
  1. 博主信息卡片（温暖渐变 + 头像 + 三栏统计）
  2. 热门文章（Top3 金色序号）
  3. 文章分类（名称 + 数量徽章）
  4. 热门标签云（pill 药丸布局）
  5. AI 智能助手入口（琥珀金渐变主题卡片）

## 7.3 后台管理布局

```
┌──────────┬───────────────────────────┐
│          │  AdminTopbar (56px)       │
│ Sidebar  ├───────────────────────────┤
│ (220px)  │                           │
│ 左侧金线 │  admin-content            │
│          │  (overflow-y: auto)       │
│          │                           │
└──────────┴───────────────────────────┘
```

## 7.4 响应式断点

| 断点 | 行为 |
|------|------|
| **1200px 以下** | 容器全宽（`max-width: 100%`），侧边栏缩至 300px |
| **768px 以下** | 单列布局，侧边栏全宽 + 取消 sticky，导航链接隐藏，搜索框收窄至 130px，导航栏高度降至 56px |

---

# 八、深色模式（Ink Well）

激活方式：`<html data-theme="dark">`（通过 SettingsPanel 切换，状态持久化到 localStorage）

Ink Well 风格：
- 背景：`#111014`（深墨色）
- 表面：`#1e1d23`（墨蓝色卡片）
- 主文字：`#e8e5df`（暖白）
- 主色：`#788ebd`（浅蓝）
- 琥珀金保持不变，在暗色背景上更显温暖

所有颜色/阴影/边框通过 `[data-theme="dark"]` CSS 变量全覆盖，无需 JS 参与（除 `index.html` 防闪烁脚本）。

---

# 九、实现要点

1. **CSS 变量驱动**：所有颜色、间距、圆角、阴影、字体、过渡通过 `:root` CSS 变量管理，位于 `src/assets/styles/global.scss`（约 890 行）
2. **Element Plus 深度覆盖**：通过 `:deep()` 和 `--el-*` CSS 变量覆盖，不改组件库源码
3. **字体 CDN**：Google Fonts 加载 Playfair Display + DM Sans + JetBrains Mono
4. **防闪烁**：`index.html` 内联脚本在 CSS 加载前读取 localStorage 主题设置
5. **零额外图标依赖**：所有装饰图标使用内联 SVG，仅 Element Plus Icons 用于功能性图标
6. **Pinia 状态管理**：auth（管理员）/ user（用户）/ site（站点配置）三个 Store
7. **全局 SCSS**：单文件管理所有设计令牌 + 全局重置 + Element Plus 覆盖 + 动画系统

---

# 十、文件清单

| 文件 | 说明 |
|------|------|
| `src/assets/styles/global.scss` | 全局设计令牌 + 重置 + Element Plus 覆盖 + 动画 |
| `src/App.vue` | 根组件：字体 + 墨韵翻页路由过渡动画 |
| `index.html` | 字体 CDN 加载 + 防闪烁脚本 |
| `src/layouts/PublicLayout.vue` | 前台布局壳（导航 + 内容 + 页脚） |
| `src/layouts/AdminLayout.vue` | 后台布局壳（侧栏 + 顶栏 + 内容） |
| `src/layouts/components/PublicHeader.vue` | 前台导航栏（毛玻璃 + 紫色Logo + pill搜索 + 金色登录按钮） |
| `src/layouts/components/PublicFooter.vue` | 前台页脚 |
| `src/layouts/components/AdminSidebar.vue` | 后台侧边栏（左侧金线 + 8个菜单项） |
| `src/layouts/components/AdminTopbar.vue` | 后台顶栏（面包屑 + 用户下拉） |
| `src/components/article/ArticleCard.vue` | 文章卡片（金色装饰条 + 封面缩放 + 阅读全文箭头） |
| `src/components/article/CommentSection.vue` | 评论区组件 |
| `src/components/article/MarkdownRenderer.vue` | Markdown 渲染（marked + highlight.js） |
| `src/components/site/Sidebar.vue` | 前台侧边栏（博主信息 + 热门 + 分类 + 标签云 + AI入口） |
| `src/components/site/SettingsPanel.vue` | 设置面板（字体缩放 + 主题切换 + 背景色预设） |
| `src/components/common/Pagination.vue` | 分页器封装 |
| `src/views/public/HomeView.vue` | 首页（Hero Banner + 文章列表 + 分页） |
| `src/views/public/*.vue` | 其他前台页面（文章详情/搜索/分类/标签/归档/关于/智能体等） |
| `src/views/admin/*.vue` | 后台页面（仪表盘/登录/文章管理/分类/标签/评论/智能体/站点设置等） |
