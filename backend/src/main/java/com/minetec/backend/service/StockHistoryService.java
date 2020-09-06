package com.minetec.backend.service;

import com.minetec.backend.dto.enums.StockType;
import com.minetec.backend.entity.OrderItem;
import com.minetec.backend.entity.StockHistory;
import com.minetec.backend.entity.User;
import com.minetec.backend.entity.warehouse.TransferItem;
import com.minetec.backend.entity.workshop.JobCardItem;
import com.minetec.backend.repository.StockHistoryRepository;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Sinan
 */
@Service
public class StockHistoryService extends EntityService<StockHistory, StockHistoryRepository> {

    public boolean createOrderItem(@NotNull final OrderItem orderItem, @NotNull final BigDecimal quantity) {
        var stockHistory = new StockHistory();
        stockHistory.setItem(orderItem.getItem());
        stockHistory.setOrderItem(orderItem);
        stockHistory.setQuantity(quantity);
        stockHistory.setSourceSite(orderItem.getOrder().getSite());
        stockHistory.setStockType(StockType.INPUT);
        this.persist(stockHistory);
        return true;
    }

    public boolean createJobCardItem(@NotNull final JobCardItem jobCardItem,
                                     @NotNull final BigDecimal deliverQuantity,
                                     @NotNull final User receivedUser) {
        var stockHistory = new StockHistory();
        stockHistory.setItem(jobCardItem.getItem());
        stockHistory.setJobCardItem(jobCardItem);
        stockHistory.setQuantity(deliverQuantity);
        stockHistory.setSourceSite(jobCardItem.getJobCard().getVehicle().getSite());
        stockHistory.setStockType(StockType.OUTPUT);
        stockHistory.setReceivedUser(receivedUser);
        this.persist(stockHistory);
        return true;
    }


    public boolean createTransferItem(@NotNull final TransferItem transferItem) {
        var transfer = new StockHistory();
        transfer.setItem(transferItem.getItem());
        transfer.setTransferItem(transferItem);
        transfer.setQuantity(transferItem.getApproveQuantity());
        transfer.setSourceSite(transferItem.getTransfer().getSourceSite());
        transfer.setTargetSite(transferItem.getTransfer().getTargetSite());
        transfer.setStockType(StockType.TRANSFER);
        this.persist(transfer);

        var input = new StockHistory();
        input.setItem(transferItem.getItem());
        input.setTransferItem(transferItem);
        input.setQuantity(transferItem.getApproveQuantity());
        input.setSourceSite(transferItem.getTransfer().getTargetSite());
        input.setStockType(StockType.INPUT);
        this.persist(input);
        return true;
    }

    public boolean createTransferItems(@NotNull final List<TransferItem> transferItems) {
        transferItems.forEach(transferItem ->
            this.createTransferItem(transferItem));
        return true;
    }

    public boolean createOrderItems(@NotNull final List<OrderItem> orderItems) {
        orderItems.forEach(orderItem ->
            this.createOrderItem(orderItem, orderItem.getApproveQuantity()));
        return true;
    }
}
