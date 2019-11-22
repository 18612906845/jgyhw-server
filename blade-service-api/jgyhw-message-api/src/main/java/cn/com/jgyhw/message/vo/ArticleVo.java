package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 回复消息之图文消息
 */
@Data
@XStreamAlias("Article")
public class ArticleVo {

    @XStreamAlias("Title")
    private String Title = "";

    @XStreamAlias("Description")
    private String Description = "";

    @XStreamAlias("PicUrl")
    private String PicUrl = "";

    @XStreamAlias("Url")
    private String Url = "";
}
