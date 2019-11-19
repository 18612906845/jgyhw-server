package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 回复消息之视频消息
 */
@XStreamAlias("VideoMessage")
public class VideoMessageVo extends BaseMessageVo {

    @XStreamAlias("Video")
    private VideoVo Video;

    public VideoVo getVideo() {
        return Video;
    }

    public void setVideo(VideoVo video) {
        Video = video;
    }
}
