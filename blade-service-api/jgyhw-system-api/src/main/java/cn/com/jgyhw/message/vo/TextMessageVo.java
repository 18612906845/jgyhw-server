package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 消息响应之文本消息
 */
@Data
@XStreamAlias("TextMessage")
public class TextMessageVo extends BaseMessageVo {

    // 回复的消息内容
    @XStreamAlias("Content")
    private String Content;
}
