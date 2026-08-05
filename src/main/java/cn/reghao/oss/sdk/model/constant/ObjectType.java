package cn.reghao.oss.sdk.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author reghao
 * @date 2023-06-13 15:09:09
 */
public enum ObjectType {
    Folder(1000, "Folder"),
    Image(1001, "Image"),
    Video(1002, "Video"),
    Audio(1003, "Audio"),
    Text(1004, "Text"),
    Other(1005, "Application"),
    Any(1006, "*/*"),
    DocPdf(1007, "DocPdf"),
    DocWord(1008, "DocWord"),
    DocExcel(1009, "DocExcel"),
    DocPpt(1010, "DocPpt");

    private final int code;
    // contentType 的值不能改变, 代码中会使用它作为前缀和上传文件的 content-type 进行比较
    private final String contentType;
    ObjectType(int code, String contentType) {
        this.code = code;
        this.contentType = contentType;
    }

    private static Map<Integer, ObjectType> map = new HashMap<>();
    static {
        for (ObjectType type : ObjectType.values()) {
            map.put(type.code, type);
        }
    }

    private static Map<Integer, String> descMap = new HashMap<>();
    static {
        for (ObjectType objectType : ObjectType.values()) {
            descMap.put(objectType.code, objectType.contentType);
        }
    }

    public static ObjectType getByCode(int code) {
        return map.get(code);
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

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return contentType;
    }

    public static String getDescByCode(int code) {
        return descMap.get(code);
    }
}
