-- =====================================================
-- 个人博客 + AI 智能体项目 - 数据库初始化脚本
-- 适用: MySQL 8.0+
-- 执行: mysql -u root -p < blog_init.sql
-- =====================================================

CREATE DATABASE IF NOT EXISTS `blog`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `blog`;

-- =====================================================
-- 1. 管理员表（单用户）
-- =====================================================
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(50)  NOT NULL,
    `password`    VARCHAR(255) NOT NULL,
    `nickname`    VARCHAR(50)  DEFAULT '',
    `avatar`      VARCHAR(500) DEFAULT '',
    `email`       VARCHAR(100) DEFAULT '',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 2. 文章分类表
-- =====================================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(50)  NOT NULL,
    `description`   VARCHAR(200) DEFAULT '',
    `sort_order`    INT          NOT NULL DEFAULT 0,
    `article_count` INT          NOT NULL DEFAULT 0,
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 3. 文章标签表
-- =====================================================
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(50) NOT NULL,
    `article_count` INT         NOT NULL DEFAULT 0,
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 4. 文章表
-- =====================================================
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '作者用户ID，0=管理员发布',
    `title`         VARCHAR(200) NOT NULL,
    `slug`          VARCHAR(200) NOT NULL,
    `summary`       VARCHAR(500) DEFAULT '',
    `content`       MEDIUMTEXT,
    `content_html`  MEDIUMTEXT,
    `category_id`   BIGINT UNSIGNED DEFAULT NULL,
    `cover_image`   VARCHAR(500) DEFAULT '',
    `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '0=草稿, 1=已发布',
    `is_top`        TINYINT      NOT NULL DEFAULT 0 COMMENT '0=否, 1=置顶',
    `view_count`    INT UNSIGNED NOT NULL DEFAULT 0,
    `comment_count` INT UNSIGNED NOT NULL DEFAULT 0,
    `like_count`    INT UNSIGNED NOT NULL DEFAULT 0,
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status_create_time` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 5. 文章-标签关联表（多对多）
-- =====================================================
DROP TABLE IF EXISTS `article_tag_relation`;
CREATE TABLE `article_tag_relation` (
    `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `article_id` BIGINT UNSIGNED NOT NULL,
    `tag_id`     BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 6. 评论表
-- =====================================================
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `article_id`   BIGINT UNSIGNED NOT NULL,
    `user_id`      BIGINT UNSIGNED DEFAULT NULL COMMENT '登录用户ID，NULL=匿名',
    `parent_id`    BIGINT UNSIGNED DEFAULT NULL COMMENT '父评论ID（预留嵌套回复）',
    `author_name`  VARCHAR(50)  NOT NULL,
    `author_email` VARCHAR(100) DEFAULT '',
    `content`      TEXT         NOT NULL,
    `ip_address`   VARCHAR(45)  DEFAULT '',
    `status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '0=待审, 1=通过, 2=拒绝',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 7. 站点配置表（KV 结构）
-- =====================================================
DROP TABLE IF EXISTS `site_config`;
CREATE TABLE `site_config` (
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `config_key`   VARCHAR(100) NOT NULL,
    `config_value` TEXT,
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 8. AI 智能体配置表
-- =====================================================
DROP TABLE IF EXISTS `agent`;
CREATE TABLE `agent` (
    `id`                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`               VARCHAR(100) NOT NULL COMMENT '智能体名称',
    `description`        VARCHAR(500) DEFAULT '' COMMENT '简短描述',
    `avatar`             VARCHAR(500) DEFAULT '' COMMENT '头像URL',
    `capability_desc`    TEXT         COMMENT '能力描述（系统提示词）',
    `provider`           VARCHAR(50)  NOT NULL COMMENT '大模型提供商: openai/deepseek/doubao 等',
    `api_key_encrypted`  VARCHAR(500) NOT NULL COMMENT 'AES-256-GCM 加密后的 API Key',
    `model_name`         VARCHAR(100) NOT NULL DEFAULT '' COMMENT '模型名，如 gpt-4o / deepseek-chat',
    `temperature`        DECIMAL(3,2) NOT NULL DEFAULT 0.70 COMMENT '温度 0-2',
    `max_tokens`         INT          NOT NULL DEFAULT 2048 COMMENT '最大回复 Token 数',
    `context_length`     INT          NOT NULL DEFAULT 10 COMMENT '上下文记忆轮数',
    `is_enabled`         TINYINT      NOT NULL DEFAULT 1 COMMENT '0=关闭, 1=启用',
    `rate_limit_per_min` INT          NOT NULL DEFAULT 10 COMMENT '每分钟每IP最大请求数',
    `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 9. AI 对话记录表（审计日志）
-- =====================================================
DROP TABLE IF EXISTS `agent_conversation`;
CREATE TABLE `agent_conversation` (
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `agent_id`    BIGINT UNSIGNED NOT NULL,
    `session_id`  VARCHAR(64) NOT NULL COMMENT '会话唯一标识',
    `role`        VARCHAR(20) NOT NULL COMMENT 'user / assistant',
    `content`     TEXT        COMMENT '消息内容',
    `ip_address`  VARCHAR(45) DEFAULT '',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_agent_id` (`agent_id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 10. 用户表（多用户平台）
-- =====================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(50)  NOT NULL,
    `password`    VARCHAR(255) NOT NULL,
    `nickname`    VARCHAR(50)  DEFAULT '',
    `avatar`      VARCHAR(500) DEFAULT '',
    `email`       VARCHAR(100) DEFAULT '',
    `bio`         VARCHAR(500) DEFAULT '',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 11. 文章点赞表
-- =====================================================
DROP TABLE IF EXISTS `article_like`;
CREATE TABLE `article_like` (
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `article_id`  BIGINT UNSIGNED NOT NULL,
    `user_id`     BIGINT UNSIGNED NOT NULL,
    `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 默认数据
-- =====================================================

-- 管理员: admin / admin123
-- 密码由 DataInitializer 在启动时用 Java 原生 BCryptPasswordEncoder 自动设置
INSERT INTO `admin` (`username`, `password`, `nickname`, `email`) VALUES
('admin', 'TO_BE_OVERWRITTEN_BY_DATAINITIALIZER', '博主', 'admin@blog.com');

-- 测试用户 (BCrypt $2a$)
-- tech_writer / test123
-- code_reader / demo123
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `bio`) VALUES
('tech_writer', '$2a$10$FehfdaeTLqRxeMrBo/zNqe3fWIqOuAo8R.EN0UONc/rOYHeyU2F72', '技术写手', 'tech@blog.com', '热爱分享编程经验的全栈开发者'),
('code_reader', '$2a$10$uO9iWrbOHAJZXB6kBQCr/eo/9SL.FDZsFsep/jfLl4TV.RMkNh.Si', '代码读者', 'code@blog.com', '每天阅读技术文章的终身学习者');

-- 默认站点配置
INSERT INTO `site_config` (`config_key`, `config_value`) VALUES
('site_name',    '我的个人博客'),
('site_desc',    '个人技术博客，记录学习与生活'),
('copyright',    '专门服务于自己'),
('about_me',     '## 关于我\n\n一位热爱技术的开发者，在这里分享我的学习心得和技术文章。'),
('social_links', '{"github":"https://github.com","email":"admin@blog.com"}'),
('keywords',     '技术博客,编程,AI'),
('site_logo',    ''),
('site_favicon', '');

-- 默认分类
INSERT INTO `category` (`name`, `description`, `sort_order`) VALUES
('技术笔记', '编程技术相关文章', 1),
('生活随笔', '日常生活记录',       2),
('AI 探索',  '人工智能相关内容',   3);

-- 默认标签
INSERT INTO `tag` (`name`) VALUES
('Java'), ('SpringBoot'), ('Vue'), ('AI'),
('Python'), ('前端'), ('后端'), ('数据库'), ('其他');
