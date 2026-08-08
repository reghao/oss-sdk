package cn.reghao.oss.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author reghao
 * @date 2023-08-23 11:41:55
 */
@AllArgsConstructor
@Getter
public class ServerInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ossUrl;
    private int bucketId;
    private int channelCode;
    private long maxSize;
    private String token;
}
