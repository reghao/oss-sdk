package cn.reghao.oss.sdk.model.constant;

/**
 * @author reghao
 * @date 2025-10-19 17:24:54
 */
public enum UploadChannelType {
    videoChannel(101, "video/playback/", ObjectType.Video.getCode(), ObjectSize.gb10.getSize(), ObjectScope.PRIVATE.getCode()),
    imageChannel(102, "image/i/", ObjectType.Image.getCode(), ObjectSize.mb2.getSize(), ObjectScope.PUBLIC.getCode()),
    photoChannel(103, "image/p/", ObjectType.Image.getCode(), ObjectSize.mb10.getSize(), ObjectScope.PUBLIC.getCode()),
    camChannel(107, "video/cam/", ObjectType.Video.getCode(), ObjectSize.gb10.getSize(), ObjectScope.PRIVATE.getCode()),
    chatChannel(109, "file1/", ObjectType.Any.getCode(), ObjectSize.gb1.getSize(), ObjectScope.PRIVATE.getCode()),
    fileChannel(111, "file/", ObjectType.Any.getCode(), ObjectSize.gb10.getSize(), ObjectScope.PRIVATE.getCode());

    private final int channelCode;
    private final String channelPrefix;
    private final int fileType;
    private final long maxSize;
    private final int scope;
    UploadChannelType(int channelCode, String channelPrefix, int fileType, long maxSize, int scope) {
        this.channelCode = channelCode;
        this.channelPrefix = channelPrefix;
        this.fileType = fileType;
        this.maxSize = maxSize;
        this.scope = scope;
    }

    public int getChannelCode() {
        return channelCode;
    }

    public String getChannelPrefix() {
        return channelPrefix;
    }

    public int getFileType() {
        return fileType;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public int getScope() {
        return scope;
    }
}
