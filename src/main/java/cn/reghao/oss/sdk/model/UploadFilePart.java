package cn.reghao.oss.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author reghao
 * @date 2022-04-25 10:42:38
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UploadFilePart implements Serializable {
    private static final long serialVersionUID = 1L;

    private String clientSha256sum;
    private String uploadId;
    // 文件标识
    private String identifier;
    private String filename;
    private String relativePath;
    // 文件大小
    private long totalSize;
    // 分片文件大小
    private long chunkSize;
    // 分片文件数量
    private long totalChunks;
    // 当前分片文件索引
    private int chunkNumber;
    // 当前分片文件大小
    private int currentChunkSize;
}
