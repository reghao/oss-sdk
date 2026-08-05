package cn.reghao.oss.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author reghao
 * @date 2026-02-01 15:57:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sha256sum;
    private String absolutePath;
    private long size;
    private String filename;
    private String contentType;
    private String channelPrefix;
    private String objectId;
    private String objectName;
    private int uploadBy;
    private String host;
    private int port;
    private int uploadStatus;
    private boolean active = false;
}
