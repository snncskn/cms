package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.warehouse.StockHistoryInfo;
import com.minetec.backend.entity.StockHistory;
import com.minetec.backend.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class StockHistoryMapper {

    public static StockHistoryInfo toInfo(final StockHistory stockHistory) {
        var info = new StockHistoryInfo();
        info.setCreatedDate(Utils.toString(stockHistory.getCreatedAt()));
        info.setItemDescription(stockHistory.getItem().getItemDescription());
        info.setMoveType(stockHistory.getStockType().name());
        info.setStockCode(stockHistory.getItem().getStorePartNumber());
        info.setSiteName(stockHistory.getSourceSite().getDescription());

        if (!Utils.isEmpty(stockHistory.getTransferItem()) &&
            !Utils.isEmpty(stockHistory.getTransferItem().getTransfer())) {
            info.setMoveNumber(stockHistory.getTransferItem().getTransfer().getTransferNumber());
        }

        if (!Utils.isEmpty(stockHistory.getJobCardItem()) &&
            !Utils.isEmpty(stockHistory.getJobCardItem().getJobCard().getReportNumber())) {
            info.setMoveNumber(stockHistory.getJobCardItem().getJobCard().getReportNumber());
        }

        if (!Utils.isEmpty(stockHistory.getOrderItem()) &&
            !Utils.isEmpty(stockHistory.getOrderItem().getOrder().getRequestNumber())) {
            info.setMoveNumber(stockHistory.getOrderItem().getOrder().getRequestNumber());
        }

        switch (stockHistory.getStockType()) {
            case INPUT:
                info.setQuantity("(+) " + stockHistory.getQuantity());
                break;
            case TRANSFER:
                info.setQuantity("(-) " + stockHistory.getQuantity());
                break;
            case OUTPUT:
                info.setQuantity("(-) " + stockHistory.getQuantity());
                break;
            default:
                break;
        }


        return info;
    }

    public static List<StockHistoryInfo> toInfos(final List<StockHistory> stockHistories) {
        var stockHistoryInfos = new ArrayList<StockHistoryInfo>();
        stockHistories.forEach(stock -> stockHistoryInfos.add(toInfo(stock)));
        return stockHistoryInfos;
    }
}
