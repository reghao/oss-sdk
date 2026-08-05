package cn.reghao.oss.sdk.model;

import cn.reghao.jutil.jdk.converter.DateTimeConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author reghao
 * @date 2023-11-29 18:11:13
 */
@NoArgsConstructor
@Data
public class ObjectInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String objectId;
    private String objectName;
    private int fileType;
    private String filename;
    private long size;
    private String url;
    private int scope;
    private String sha256sum;
    private String updateTime;

    public ObjectInfo(String objectId, String objectName,  int fileType, String filename,
                      long size, int scope, String sha256sum,  LocalDateTime updateTime) {
        this.objectId = objectId;
        this.objectName = objectName;
        this.fileType = fileType;
        this.filename = filename;
        this.size = size;
        this.scope = scope;
        this.sha256sum = sha256sum;
        this.updateTime = DateTimeConverter.format(updateTime);
    }
}
