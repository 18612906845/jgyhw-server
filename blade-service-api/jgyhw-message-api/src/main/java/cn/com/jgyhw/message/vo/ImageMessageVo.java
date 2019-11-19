package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 回复消息之图片消息
 */
@XStreamAlias("ImageMessage")
public class ImageMessageVo extends BaseMessageVo {

    @XStreamAlias("Image")
    private ImageVo Image;

    public ImageVo getImage() {
        return Image;
    }

    public void setImage(ImageVo image) {
        Image = image;
    }
}
