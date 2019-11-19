package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

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

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
