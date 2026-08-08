package cn.reghao.oss.sdk.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author reghao
 * @date 2026-08-07 20:32:53
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UploadRequestDto {
    @NotNull
    private int bucketId;
    @NotNull
    private int channelCode;
}
