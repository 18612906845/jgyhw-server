package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 回复消息之音乐消息
 */
@XStreamAlias("MusicMessage")
public class MusicMessageVo extends BaseMessageVo {

    // 回复的消息内容
    @XStreamAlias("Content")
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
