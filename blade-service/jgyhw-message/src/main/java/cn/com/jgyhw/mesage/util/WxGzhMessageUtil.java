package cn.com.jgyhw.mesage.util;

import cn.com.jgyhw.message.vo.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信接收、被动回复消息工具类
 */
public class WxGzhMessageUtil {

    private static Logger LOGGER = LogManager.getLogger(WxGzhMessageUtil.class);

    public static final String ANSWER_MSG_TYPE_TEXT = "text";//回复文本类型消息
    public static final String ANSWER_MSG_TYPE_IMAGE = "image";//回复图片类型消息
    public static final String ANSWER_MSG_TYPE_VOICE = "voice";//回复语音类型消息
    public static final String ANSWER_MSG_TYPE_VIDEO = "video";//回复视频类型消息
    public static final String ANSWER_MSG_TYPE_MUSIC = "music";//回复音乐类型消息
    public static final String ANSWER_MSG_TYPE_NEWS = "news";//回复图文类型消息

    public static final String RESP_MESSAGE_TYPE_TEXT = "text";
    public static final Object REQ_MESSAGE_TYPE_TEXT = "text";
    public static final Object REQ_MESSAGE_TYPE_IMAGE = "image";
    public static final Object REQ_MESSAGE_TYPE_VOICE = "voice";
    public static final Object REQ_MESSAGE_TYPE_VIDEO = "video";
    public static final Object REQ_MESSAGE_TYPE_LOCATION = "location";
    public static final Object REQ_MESSAGE_TYPE_LINK = "link";
    public static final Object REQ_MESSAGE_TYPE_EVENT = "event";
    public static final Object EVENT_TYPE_SUBSCRIBE = "subscribe";
    public static final Object EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
    public static final Object EVENT_TYPE_SCAN = "SCAN";
    public static final Object EVENT_TYPE_LOCATION = "LOCATION";
    public static final Object EVENT_TYPE_CLICK = "CLICK";
    public static String PREFIX_CDATA = "<![CDATA[";
    public static String SUFFIX_CDATA = "]]>";

    /**
     * 扩展xstream，使其支持CDATA块
     * 由于xstream框架本身并不支持CDATA块的生成，下面代码是对xtream做了扩展，
     * 使其支持在生成xml各元素值时添加CDATA块。
     * @date 2013-05-19
     */
    private static XStream xstreamCDATA = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;
                @SuppressWarnings("unchecked")
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write(PREFIX_CDATA);
                        writer.write(text);
                        writer.write(SUFFIX_CDATA);
                    } else {
                        super.writeText(writer, text);
                    }
                }
            };
        }
    });

    /**
     * 普通转换不添加CDATA块
     */
    private static XStream xstream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;
                @SuppressWarnings("unchecked")
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write(text);
                    } else {
                        super.writeText(writer, text);
                    }
                }
            };
        }
    });

    /**
     * 解析微信发来的请求(xml)
     *
     * @param request
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map map = new HashMap();
        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();
        // 遍历所有子节点
        for (Element e : elementList){
            map.put(e.getName(), e.getText());
        }
        LOGGER.info("得到Xml元素Map：" + map.toString());
        // 释放资源
        inputStream.close();
        inputStream = null;
        return map;
    }

    /**
     * 文本消息对象转换成xml
     *
     * @param textMessageVo 文本消息对象
     * @return xml
     */
    public static String textMessageVoToXml(TextMessageVo textMessageVo) {
        xstreamCDATA.alias("xml", textMessageVo.getClass());
        return xstreamCDATA.toXML(textMessageVo);
    }

    /**
     * 图片消息对象转换成xml
     *
     * @param imageMessageVo 图片消息对象
     * @return xml
     */
    public static String imageMessageVoToXml(ImageMessageVo imageMessageVo) {
        xstreamCDATA.alias("xml", imageMessageVo.getClass());
        return xstreamCDATA.toXML(imageMessageVo);
    }

    /**
     * 视频消息对象转换成xml
     *
     * @param videoMessageVo 视频消息对象
     * @return xml
     */
    public static String videoMessageVoToXml(VideoMessageVo videoMessageVo) {
        xstreamCDATA.alias("xml", videoMessageVo.getClass());
        return xstreamCDATA.toXML(videoMessageVo);
    }

    /**
     * 音乐消息对象转换成xml
     *
     * @param musicMessageVo 音乐消息对象
     * @return xml
     */
    public static String musicMessageVoToXml(MusicMessageVo musicMessageVo) {
        xstreamCDATA.alias("xml", musicMessageVo.getClass());
        return xstreamCDATA.toXML(musicMessageVo);
    }

    /**
     * 图文消息对象转换成xml
     *
     * @param newsMessage 图文消息对象
     * @param articleList item对象集合
     * @return xml
     */
    public static String newsMessageToXml(NewsMessageVo newsMessage, List<ArticleVo> articleList) {
        if(articleList == null || articleList.isEmpty()){
            return "";
        }
        int articleSize = articleList.size();
        newsMessage.setArticleCount(String.valueOf(articleSize));
        newsMessage.setArticles("");
        xstreamCDATA.alias("xml", newsMessage.getClass());

        String itemListStr = "";
        for(int i=0;i<articleSize;i++){
            itemListStr += articleItemToXml(articleList.get(i));
            if(i < (articleSize - 1)){
                itemListStr += "\n";
            }
        }
        String newsMessageXml = xstreamCDATA.toXML(newsMessage);
        return newsMessageXml.replace("<Articles><![CDATA[]]></Articles>", "<Articles>\n" + itemListStr + "\n</Articles>");
    }

    /**
     * 图文消息Item Xml生成
     *
     * @param article item对象
     * @return
     */
    private static String articleItemToXml(ArticleVo article){
        xstreamCDATA.alias("item", article.getClass());
        return xstreamCDATA.toXML(article);
    }
}
