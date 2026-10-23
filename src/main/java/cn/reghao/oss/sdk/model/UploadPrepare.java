package cn.reghao.oss.sdk.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "bucketId 不能为 NULL")
    private Integer bucketId;
    @NotNull(message = "channelCode 不能为 NULL")
    private Integer channelCode;
    @NotBlank(message = "filename 不能为空字符串")
    @Size(max = 255, message = "filename 不能超过 255 个字符")
    private String filename;
    @NotNull(message = "size 不能为 NULL")
    private Long size;
    @NotBlank(message = "sha256sum 不能为空字符串")
    @Size(max = 128, message = "sha256sum 不能超过 128 个字符")
    private String sha256sum;
}
