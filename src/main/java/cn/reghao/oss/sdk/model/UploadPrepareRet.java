package cn.reghao.oss.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author reghao
 * @date 2022-04-21 09:27:55
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UploadPrepareRet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uploadId;
    private int splitSize;
    private boolean exist;
    private long offset;
    private int length;

    public UploadPrepareRet(String uploadId, int splitSize) {
        this.uploadId = uploadId;
        this.splitSize = splitSize;
        this.exist = false;
        this.offset = 0;
        this.length = 0;
    }
}
