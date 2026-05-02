package com.taolife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.taolife.common.config.LoggingAutoConfig;
import com.taolife.common.config.ApiEncryptionAutoConfig;

/**
 * TaoLife单体应用启动类
 *
 * @author 文二
 * @date 2026-03-21
 */
@SpringBootApplication(scanBasePackages = "com.taolife")
@EnableAsync
@EnableScheduling
@Import({ LoggingAutoConfig.class, ApiEncryptionAutoConfig.class })
public class TaoLifeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaoLifeApplication.class, args);
    }
}