package cn.reghao.oss.sdk.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author reghao
 * @date 2023-05-24 09:23:25
 */
@Setter
@Getter
public class UploadedPart implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean needMerge;
    private List<Integer> uploaded;
    private boolean skipUpload;
    private String uploadId;
    private String url;

    public UploadedPart(String uploadId, String url) {
        this.uploadId = uploadId;
        this.url = url;
        this.skipUpload = true;
    }

    public UploadedPart() {
        this.skipUpload = false;
        this.uploaded = Collections.emptyList();
    }

    public void setUploaded(List<Integer> uploaded) {
        this.uploaded = uploaded;
    }
}
