package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("Video")
public class VideoVo {

    /**
     * 视频永久素材MediaId
     */
    @XStreamAlias("MediaId")
    private String MediaId;

    /**
     * 视频永久素材标题
     */
    @XStreamAlias("Title")
    private String Title;

    /**
     * 视频永久素材描述
     */
    @XStreamAlias("Description")
    private String Description;
}
