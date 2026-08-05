package cn.reghao.oss.sdk.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述资源的可见范围
 *
 * @author reghao
 * @date 2023-05-19 18:15:45
 */
public enum ObjectScope {
    PRIVATE(1, "本人可见"),
    PUBLIC(2, "所有人可见");

    private final int code;
    private final String desc;
    private static final Map<Integer, ObjectScope> map = new HashMap<>();
    static {
        for (ObjectScope scope : ObjectScope.values()) {
            map.put(scope.code, scope);
        }
    }

    ObjectScope(Integer code, String desc) {
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

    public static ObjectScope getByCode(int code) {
        return map.get(code);
    }
}
