package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 消息基类（公众帐号 -> 普通用户）
 */
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

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public long getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(long createTime) {
        CreateTime = createTime;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public int getFuncFlag() {
        return FuncFlag;
    }

    public void setFuncFlag(int funcFlag) {
        FuncFlag = funcFlag;
    }

    @Override
    public String toString() {
        return "BaseMessage [ToUserName=" + ToUserName + ", FromUserName=" + FromUserName + ", CreateTime=" + CreateTime
                + ", MsgType=" + MsgType + ", FuncFlag=" + FuncFlag + "]";
    }
}
