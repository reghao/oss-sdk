package cn.reghao.oss.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author reghao
 * @date 2024-02-26 10:08:12
 */
@AllArgsConstructor
@Getter
public class ObjectChannel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int channelCode;
    private String prefix;
    private long maxSize;
    private int fileType;
    private boolean setUrl;
    private boolean setCallback;
    private int scope;
    private String domain;
    private int createBy;
}
