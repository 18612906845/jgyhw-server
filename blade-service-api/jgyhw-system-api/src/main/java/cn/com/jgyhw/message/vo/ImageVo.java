package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("Image")
public class ImageVo {

    /**
     * 图片永久素材MediaId
     */
    @XStreamAlias("MediaId")
    private String MediaId;
}
