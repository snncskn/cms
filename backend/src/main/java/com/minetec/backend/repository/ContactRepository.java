package com.minetec.backend.repository;

import com.minetec.backend.entity.Contact;
import com.minetec.backend.repository.projection.ContactListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends BaseRepository<Contact> {
    @Query("select c from Contact c where c.isActive = true")
    Page<ContactListItemProjection> list(Pageable pageable);
}
