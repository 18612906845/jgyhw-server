package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 回复消息之音乐消息
 */
@Data
@XStreamAlias("MusicMessage")
public class MusicMessageVo extends BaseMessageVo {

    // 回复的消息内容
    @XStreamAlias("Content")
    private String Content;
}
