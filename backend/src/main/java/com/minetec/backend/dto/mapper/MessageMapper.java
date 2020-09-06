package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.MessageInfo;
import com.minetec.backend.entity.Message;
import com.minetec.backend.util.Utils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedList;
import java.util.List;

public class MessageMapper {

    public static MessageInfo toInfo(final Message message) {
        var info = new MessageInfo();
        info.setMessage(message.getMessage());
        info.setUuid(message.getUuid());
        info.setOrderItemUuid(message.getOrderItem().getUuid());
        info.setFullName(SecurityContextHolder.getContext().getAuthentication().getName());
        info.setCreateDate(Utils.toString(message.getCreatedAt()));
        return info;
    }

    public static List<MessageInfo> toInfos(final List<Message> messages) {
        var infos = new LinkedList<MessageInfo>();
        messages.forEach(message -> infos.add(toInfo(message)));
        return infos;
    }
}
