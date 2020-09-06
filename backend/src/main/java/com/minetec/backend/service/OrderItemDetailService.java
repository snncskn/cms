package com.minetec.backend.service;

import com.minetec.backend.dto.info.OrderItemDetailInfo;
import com.minetec.backend.dto.mapper.OrderItemDetailMapper;
import com.minetec.backend.entity.OrderItem;
import com.minetec.backend.entity.OrderItemDetail;
import com.minetec.backend.repository.OrderItemDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sinan
 */

@Service
@RequiredArgsConstructor
public class OrderItemDetailService extends EntityService<OrderItemDetail, OrderItemDetailRepository> {

    private final UserService userService;

    public boolean create(@NotNull final List<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> this.create(orderItem, orderItem.getApproveQuantity()));
        return true;
    }

    public boolean create(@NotNull final OrderItem orderItem, @NotNull final BigDecimal quantity) {
        var detail = new OrderItemDetail();
        detail.setOrderItem(orderItem);
        detail.setQuantity(quantity);
        detail.setDescription(orderItem.getDescription());
        this.persist(detail);
        return true;
    }

    /**
     * pageable list
     *
     * @param pageable
     * @return<
     */
    public Page<OrderItemDetailInfo> list(@NotNull final Long orderItemId, final Pageable pageable) {
        Page<OrderItemDetail> orderItemDetails = getRepository().list(orderItemId, pageable);
        final List<OrderItemDetailInfo> infos =
            orderItemDetails.stream().map(OrderItemDetailMapper::toInfo).collect(Collectors.toList());
        infos.forEach(orderItemDetailInfo ->
            orderItemDetailInfo.setCreatedUser(
                userService.getRepository().findFullName(orderItemDetailInfo.getCreatedBy())));
        return new PageImpl<>(infos, pageable, orderItemDetails.getTotalElements());
    }

}
