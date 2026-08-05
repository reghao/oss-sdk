package cn.reghao.oss.sdk.model;

import cn.reghao.jutil.jdk.media.model.MediaProps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author reghao
 * @date 2023-01-11 10:41:53
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VideoInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String objectId;
    private String videoCodec;
    private Long vbitRate;
    private String audioCodec;
    private Long abitRate;
    private String formatName;
    private Integer width;
    private Integer height;
    // 单位秒
    private Double duration;
    private String urlType;
    private String url;
    private String quality;
    private Long size;
    //private LocalDateTime createTime;

    public VideoInfo(String objectId, MediaProps mediaProps) {
        this.objectId = objectId;
        this.videoCodec = mediaProps.getVideoProps().getCodecName();
        this.vbitRate = mediaProps.getVideoProps().getBitRate();
        this.audioCodec = mediaProps.getAudioProps() != null ? mediaProps.getAudioProps().getCodecName() : "";
        this.abitRate = mediaProps.getAudioProps() != null ? mediaProps.getAudioProps().getBitRate() : 0;
        this.formatName = mediaProps.getFormatName();
        this.width = mediaProps.getVideoProps().getCodedWidth().intValue();
        this.height = mediaProps.getVideoProps().getCodedHeight().intValue();
        this.duration = mediaProps.getVideoProps().getDuration();
    }
}
