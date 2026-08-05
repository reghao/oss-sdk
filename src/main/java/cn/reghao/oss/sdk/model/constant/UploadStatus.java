package cn.reghao.oss.sdk.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author reghao
 * @date 2026-04-29 14:35:46
 */
public enum UploadStatus {
    UPLOADING(1, "上传中"),
    FLUSHING(2, "迁移中"),
    AVAILABLE(3, "已完成");

    private final int code;
    private final String desc;
    private static final Map<Integer, UploadStatus> map = new HashMap<>();
    static {
        for (UploadStatus uploadStatus : UploadStatus.values()) {
            map.put(uploadStatus.code, uploadStatus);
        }
    }

    UploadStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getName() {
        return this.name();
    }

    /**
     * 提供给 @ValidEnum 调用
     *
     * @param
     * @return
     * @date 2023-10-11 14:44:42
     */
    public int getValue() {
        return this.code;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UploadStatus getByCode(int code) {
        return map.get(code);
    }
}
