package io.github.tomhusky.kkfilemini.job;

import io.github.tomhusky.kkfilemini.ConfigConstants;
import io.github.tomhusky.kkfilemini.KkFileUtils;
import io.github.tomhusky.kkfilemini.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 文件缓存清理任务
 * <p/>
 *
 * @author luowj
 * @version 1.0
 * @since 2022/5/10 8:59
 */
@Slf4j
@Component(value = "kkFileCacheJob")
@ConditionalOnProperty(prefix = "office.cache", name = "enabled", havingValue = "true")
public class KKFileCacheJob {

    @Autowired
    private CacheService cacheService;

    @Value("${office.cache.type}")
    private String cacheType;

    private final String fileDir = ConfigConstants.getFileDir();
    private final String convertDir = ConfigConstants.getConvertDir();

    @Scheduled(cron = "${office.cache.clean.cron:0 0 1 * * *}")
    public void fileCacheJob() {
        log.info("Cache clean start");
        KkFileUtils.deleteDirectory(convertDir);

        if (Boolean.FALSE.equals(ConfigConstants.isCacheEnabled())) {
            KkFileUtils.deleteDirectory(fileDir);
        } else {
            cacheService.cleanCache();
            if ("jdk".equals(cacheType)) {
                KkFileUtils.deleteDirectory(fileDir);
            }
        }
        log.info("Cache clean end");
    }
}
