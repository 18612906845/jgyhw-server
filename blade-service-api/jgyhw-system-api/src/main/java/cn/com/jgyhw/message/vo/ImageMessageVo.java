package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 回复消息之图片消息
 */
@Data
@XStreamAlias("ImageMessage")
public class ImageMessageVo extends BaseMessageVo {

    @XStreamAlias("Image")
    private ImageVo Image;
}
