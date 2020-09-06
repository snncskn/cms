package com.minetec.backend.repository;

import com.minetec.backend.entity.Image;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author Sinan
 **/
@Repository
public interface ImageRepository extends BaseRepository<Image> {

    List<Image> findByVehicleId(long vehicleId);

    List<Image> findByItemId(long itemId);

    Image findByUserId(long userId);

    @Query("select i from Image i  where i.user.uuid = :userUuid ")
    Image findByUserUuid(UUID userUuid);
}
