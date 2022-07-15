package io.github.tomhusky.kkfilemini.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 配置类，扫包配置
 * <p/>
 *
 * @author luowj
 * @version 1.0
 * @since 2022/7/7 16:26
 */
@Configuration
@ComponentScan(basePackages = "io.github.tomhusky.kkfilemini")
public class AutoConfiguration {

}
