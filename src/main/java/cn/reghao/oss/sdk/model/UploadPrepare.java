package cn.reghao.oss.sdk.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 分片文件
 *
 * @author reghao
 * @date 2021-11-23 10:23:00
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UploadPrepare implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Integer userNodeId;
    @NotNull
    private int channelCode;
    @NotBlank
    private String filename;
    @NotNull
    private Long size;
    @NotBlank
    private String sha256sum;
}
