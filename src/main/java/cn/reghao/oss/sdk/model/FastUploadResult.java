package cn.reghao.oss.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author reghao
 * @date 2026-02-01 15:50:24
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FastUploadResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sha256sum;
    private String filename;
    private String objectId;
    private String objectName;
    private String channelPrefix;
    private int bucketId;

    public FastUploadResult(UploadResult uploadResult) {
        this.sha256sum = uploadResult.getSha256sum();
        this.filename = uploadResult.getFilename();
        this.objectId = uploadResult.getObjectId();
        this.objectName = uploadResult.getObjectName();
        this.channelPrefix = uploadResult.getChannelPrefix();
        this.bucketId = uploadResult.getBucketId();
    }
}
