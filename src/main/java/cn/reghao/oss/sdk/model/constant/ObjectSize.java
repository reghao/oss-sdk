package cn.reghao.oss.sdk.model.constant;

/**
 * @author reghao
 * @date 2024-10-23 10:49:04
 */
public enum ObjectSize {
    mb2(1024L*1024*2, "2MB"),
    mb10(1024L*1024*10, "10MB"),
    gb1(1024L*1024*1024, "1GB"),
    gb10(1024L*1024*1024*10, "10GB"),
    gb20(1024L*1024*1024*20, "20GB");

    private final long size;
    private final String desc;
    ObjectSize(long size, String desc) {
        this.size = size;
        this.desc = desc;
    }

    /**
     * 提供给 @ValidEnum 调用
     *
     * @param
     * @return
     * @date 2023-10-11 14:44:42
     */
    public long getValue() {
        return this.size;
    }

    public long getSize() {
        return size;
    }

    public String getDesc() {
        return desc;
    }
}
