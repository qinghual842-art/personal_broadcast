package com.blog.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.module.admin.entity.Admin;
import com.blog.module.admin.mapper.AdminMapper;
import com.blog.module.site.entity.SiteConfig;
import com.blog.module.site.mapper.SiteConfigMapper;
import com.blog.module.user.entity.User;
import com.blog.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminMapper adminMapper;
    private final SiteConfigMapper siteConfigMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        ensureAdmin();
        ensureUser("tech_writer", "test123", "技术写手", "tech@blog.com", "热爱分享编程经验的全栈开发者");
        ensureUser("code_reader", "demo123", "代码读者", "code@blog.com", "每天阅读技术文章的终身学习者");
        initSiteConfig();
    }

    /**
     * 无条件删除旧管理员并重建，确保密码由 Java 原生 BCryptPasswordEncoder 生成。
     * 外部库（bcryptjs/Python bcrypt）生成的 BCrypt 哈希与 jBCrypt 存在字节级不兼容，
     * 因此不用 matches() 判断——直接删旧建新，由 Java 自己生成哈希。
     */
    private void ensureAdmin() {
        adminMapper.delete(new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, "admin"));
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setNickname("博主");
        admin.setEmail("admin@blog.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        adminMapper.insert(admin);
        log.info("✅ 管理员账号已就绪: admin / admin123");
    }

    private void ensureUser(String username, String rawPwd, String nickname, String email, String bio) {
        userMapper.delete(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPwd));
        user.setNickname(nickname);
        user.setEmail(email);
        user.setBio(bio);
        userMapper.insert(user);
        log.info("✅ 测试用户已就绪: {} / {}", username, rawPwd);
    }

    private void initSiteConfig() {
        if (siteConfigMapper.selectCount(null) == 0) {
            insertConfig("site_name", "我的个人博客");
            insertConfig("site_desc", "个人技术博客，记录学习与生活");
            insertConfig("copyright", "专门服务于自己");
            insertConfig("about_me", "## 关于我\n\n一位热爱技术的开发者，在这里分享我的学习心得和技术文章。");
            insertConfig("social_links", "{\"github\":\"https://github.com\",\"email\":\"admin@blog.com\"}");
            insertConfig("keywords", "技术博客,编程,AI");
            insertConfig("site_logo", "");
            insertConfig("site_favicon", "");
            log.info("默认站点配置已初始化");
        }
    }

    private void insertConfig(String key, String value) {
        SiteConfig config = new SiteConfig();
        config.setConfigKey(key);
        config.setConfigValue(value);
        siteConfigMapper.insert(config);
    }
}
