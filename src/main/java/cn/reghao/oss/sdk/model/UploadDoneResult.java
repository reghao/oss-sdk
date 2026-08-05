package cn.reghao.oss.sdk.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author reghao
 * @date 2026-04-30 11:13:40
 */
@NoArgsConstructor
@Data
public class UploadDoneResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String objectId;
    private String sha256sum;
    private String absolutePath;
    private long size;
    private String hostPort;

    public UploadDoneResult(String objectId, String sha256sum, String absolutePath, long size, String hostPort) {
        this.objectId = objectId;
        this.sha256sum = sha256sum;
        this.absolutePath = absolutePath;
        this.size = size;
        this.hostPort = hostPort;
    }
}
