package com.angel.nettychat.handler;

import com.angel.nettychat.common.enmus.MsgActionEnum;
import com.angel.nettychat.model.dto.DataContent;
import com.angel.nettychat.utils.UserChannelRel;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author angel
 * @Date 19-10-30
 * 处理消息的handler
 * TextWebSocketFrame： 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
@Slf4j
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    // 用于记录和管理所有客户端的channel
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        // 获取客户端传输过来的消息
        String content = textWebSocketFrame.text();
        log.info("接收过来的数据" + content);

        Channel currentChannel = channelHandlerContext.channel();

        // 将客户端传过来的json数据转成对象
        Gson gson = new Gson();
        DataContent dataContent = gson.fromJson(content, DataContent.class);

        // 如果是聊天消息
        if (MsgActionEnum.CHAT.getType() == dataContent.getAction()) {
            // 将用户id和channel加入到UserChannelRel
            UserChannelRel.put(dataContent.getChatMsg().getSenderId(), currentChannel);

            // 发送消息
            // 从全局用户Channel关系中获取接受方的channel
            Channel receiverChannel = UserChannelRel.get(dataContent.getChatMsg().getSenderId());

            // 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
//        Channel findChannel = channelGroup.find(receiverChannel.id());
            channelGroup.writeAndFlush(
                    new TextWebSocketFrame(
                            gson.toJson(dataContent)));
        /*for (Channel channel : channelGroup) {
            channel.writeAndFlush(new TextWebSocketFrame("服务器时间:" + LocalDateTime.now() + ",消息为:" + content));
        }*/
        }

    }

    /**
     * 当客户端连接服务端
     * 获取客户端channel  接受到并放到ChannelGroup管理
     * @param ctx ctx
     * @throws Exception exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 当触发 handlerRemoved  ChannelGroup会自动移除客户端的channel
//        channelGroup.remove(channel);
        log.info("客户端断开 channel对应的长id:" + channel.id().asLongText());
        log.info("客户端断开 channel对应的短id:" + channel.id().asShortText());
    }
}
