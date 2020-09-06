package com.minetec.backend.repository;

import com.minetec.backend.entity.Item;
import com.minetec.backend.repository.projection.ItemListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Sinan
 */
@Repository
public interface ItemRepository extends BaseRepository<Item> {

    @Query("select v from Item v where v.isActive = true")
    Page<ItemListItemProjection> list(Pageable pageable);

    /**
     * @param storePartNumber
     * @param barcode
     * @param itemDescription
     * @param pageable
     * @return
     */
    @Query("select v from Item v where v.isActive = true and " +
        " v.storePartNumber like %:storePartNumber%  and " +
        " v.itemDescription like %:itemDescription%  and " +
        " v.barcode  like %:barcode%  ")
    Page<Item> filter(String storePartNumber, String itemDescription,
                      String barcode, Pageable pageable);

    /**
     * @param filter
     * @param pageable
     * @return
     */
    @Query("select v from Item v where v.isActive = true and (" +
        " v.storePartNumber like %:filter%  or " +
        " v.itemDescription like %:filter%  or " +
        " v.barcode  like %:filter%  )")
    Page<Item> allFilter(String filter, Pageable pageable);

}
