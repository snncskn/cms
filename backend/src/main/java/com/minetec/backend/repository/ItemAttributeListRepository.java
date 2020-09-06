package com.minetec.backend.repository;

import com.minetec.backend.entity.ItemAttributeList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Sinan
 */
@Repository
public interface ItemAttributeListRepository extends BaseRepository<ItemAttributeList> {
    @Query("select va from ItemAttributeList va where va.item.id = ?1 ")
    List<ItemAttributeList> findByItemId(long partId);
}
