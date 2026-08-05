package cn.reghao.oss.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author reghao
 * @date 2026-02-21 23:06:55
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UploadSample {
    private String uploadId;
    private String sampleMd5;
}
