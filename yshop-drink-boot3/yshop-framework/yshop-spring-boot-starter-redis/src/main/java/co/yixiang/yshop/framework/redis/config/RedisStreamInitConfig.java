package co.yixiang.yshop.framework.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class RedisStreamInitConfig {
    private static final String CONSUMER_GROUP = "yshop-server";
    private static final String[] STREAM_KEYS = {
            "weixin.msg.notice",
            "order.pay.notice"
    };

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisStreamInitConfig(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initStreamGroup() {
        ensureStreamGroups();
    }

    /**
     * 每60秒巡检，自动重建stream与消费组，解决Redis重启丢失元数据问题
     */
    @Scheduled(fixedDelay = 60_000, initialDelay = 5_000)
    public void ensureStreamGroups() {
        StreamOperations<String, Object, Object> streamOps = redisTemplate.opsForStream();
        for (String streamKey : STREAM_KEYS) {
            try {
                // stream不存在才插入初始化消息，避免定时任务产生大量垃圾消息
                if (Boolean.FALSE.equals(redisTemplate.hasKey(streamKey))) {
                    log.info("[ensureStreamGroups] stream[{}]不存在，创建stream", streamKey);
                    streamOps.add(streamKey, Collections.singletonMap("init", ""));
                }
                // 创建消费组
                streamOps.createGroup(streamKey, ReadOffset.latest(), CONSUMER_GROUP);
            } catch (Exception e) {
                String errMsg = e.getMessage();
                // BUSYGROUP：消费组已经存在，属于正常现象，不打warn日志
                if (errMsg != null && errMsg.contains("BUSYGROUP")) {
                    continue;
                }
                log.warn("[ensureStreamGroups][streamKey({}) 巡检异常]", streamKey, e);
            }
        }
    }

}
