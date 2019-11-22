package cn.com.jgyhw.message.vo;

import lombok.Data;

import java.util.Map;

/**
 * 模版消息Pojo
 */
@Data
public class TemplateMessageVo {

    /**
     * 接收者openid
     */
    private String touser;

    /**
     * 模板ID
     */
    private String template_id;

    /**
     * 消息点击跳转URL
     */
    private String url;

    /**
     * 跳小程序所需数据，不需跳小程序可不用传该数据
     */
    private TemplateMessageMiniprogramVo miniprogram;

    /**
     * 模板数据，数据格式如下
     * "data":{
     *      "first": {
     *          "value":"恭喜你购买成功！",
     *          "color":"#173177"
     *      },
     *      "keyword1":{
     *          "value":"巧克力",
     *          "color":"#173177"
     *      }
     * }
     */
    private Map<String, Object> data;
}
