package com.minetec.backend.repository;

import com.minetec.backend.entity.Stock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * @author Sinan
 */

@Repository
public interface StockRepository extends BaseRepository<Stock> {

    @Query("select sum(s.quantity) from Stock s where s.site.id = :siteId and s.item.id = :itemId ")
    BigDecimal sumQuantity(Long siteId, Long itemId);

    Stock findBySiteIdAndItemId(Long siteId, Long itemId);
}
