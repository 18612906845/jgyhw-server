package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Image")
public class ImageVo {

    /**
     * 图片永久素材MediaId
     */
    @XStreamAlias("MediaId")
    private String MediaId;

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }
}
