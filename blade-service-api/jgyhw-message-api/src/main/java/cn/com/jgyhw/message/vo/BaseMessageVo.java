package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 消息基类（公众帐号 -> 普通用户）
 */
@Data
public class BaseMessageVo {

    // 接收方帐号（收到的OpenID）
    @XStreamAlias("ToUserName")
    private String ToUserName;
    // 开发者微信号
    @XStreamAlias("FromUserName")
    private String FromUserName;
    // 消息创建时间 （整型）
    @XStreamAlias("CreateTime")
    private long CreateTime;
    // 消息类型（text/music/news/image）
    @XStreamAlias("MsgType")
    private String MsgType;
    // 位0x0001被标志时，星标刚收到的消息
    @XStreamAlias("FuncFlag")
    private int FuncFlag;
}
