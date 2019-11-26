package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 回复消息之视频消息
 */
@Data
@XStreamAlias("VideoMessage")
public class VideoMessageVo extends BaseMessageVo {

    @XStreamAlias("Video")
    private VideoVo Video;
}
