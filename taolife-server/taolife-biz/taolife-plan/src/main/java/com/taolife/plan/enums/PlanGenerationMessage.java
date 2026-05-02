package com.taolife.plan.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 方案生成进度消息枚举
 * 用于通过 getGenerationStatus 接口返回给前端的进度文本
 *
 * @author 文二
 * @date 2026-04-10
 */
public enum PlanGenerationMessage {

    P10(10, "正在分析体质信息..."),
    P20(20, "正在检索养生知识..."),
    P30(30, "正在生成个性化方案（第1步）..."),
    P40(40, "正在生成个性化方案（第2步）..."),
    P50(50, "正在生成个性化方案（第3步）..."),
    P60(60, "正在生成个性化方案（第4步）..."),
    P70(70, "正在生成个性化方案（第5步）..."),
    P80(80, "正在生成个性化方案（第6步）..."),
    P90(90, "正在生成个性化方案（最后阶段）..."),
    P95(95, "正在保存方案数据..."),
    P100(100, "方案生成完成！");

    private final int progress;
    private final String message;

    PlanGenerationMessage(int progress, String message) {
        this.progress = progress;
        this.message = message;
    }

    public int getProgress() {
        return progress;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据进度值获取对应的消息
     *
     * @param progress 进度值 0-100
     * @return 对应的消息，找不到则返回空字符串
     */
    public static String getMessage(int progress) {
        Optional<PlanGenerationMessage> found = Arrays.stream(values())
                .filter(e -> e.progress == progress)
                .findFirst();
        return found.map(PlanGenerationMessage::getMessage).orElse("");
    }
}
