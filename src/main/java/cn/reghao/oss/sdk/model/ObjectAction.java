package cn.reghao.oss.sdk.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author reghao
 * @date 2023-10-18 13:15:45
 */
public enum ObjectAction {
    upload(1, "上传"),
    download(2, "下载"),
    access(3, "访问"),
    delete(4, "删除"),
    all(5, "all");

    private final int code;
    private final String desc;

    private static Map<Integer, String> descMap = new HashMap<>();
    static {
        for (ObjectAction scope : ObjectAction.values()) {
            descMap.put(scope.code, scope.desc);
        }
    }

    ObjectAction(Integer code, String desc) {
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

    // TODO 第一次调用时会初始化 descMap
    public static String getDescByCode(int code) {
        return descMap.get(code);
    }
}
