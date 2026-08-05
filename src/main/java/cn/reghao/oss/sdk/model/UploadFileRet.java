package cn.reghao.oss.sdk.model;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author reghao
 * @date 2023-05-23 11:11:32
 */
@Getter
public class UploadFileRet implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String uploadId;
    private boolean fastUpload;
    private String url;
    //private boolean merged;
    private boolean uploaded;

    public UploadFileRet(String uploadId) {
        this.uploadId = uploadId;
        this.url = null;
        this.uploaded = false;
    }

    public UploadFileRet(String uploadId, boolean fastUpload) {
        this.uploadId = uploadId;
        this.url = null;
        this.fastUpload = fastUpload;
    }

    public UploadFileRet(String uploadId, String url) {
        this.uploadId = uploadId;
        this.url = url;
        this.uploaded = false;
    }

    public UploadFileRet(String uploadId, String url, boolean uploaded) {
        this.uploadId = uploadId;
        this.url = url;
        this.uploaded = true;
    }
}
