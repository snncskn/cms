package com.minetec.backend.service;

import com.minetec.backend.dto.form.ContactCreateUpdateForm;
import com.minetec.backend.dto.info.ContactInfo;
import com.minetec.backend.entity.Contact;
import com.minetec.backend.entity.Supplier;
import com.minetec.backend.repository.ContactRepository;
import com.minetec.backend.repository.projection.ContactListItemProjection;
import com.minetec.backend.dto.mapper.ContactMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ContactService extends EntityService<Contact, ContactRepository> {

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<ContactListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * save
     *
     * @param form
     * @return
     */
    public ContactInfo create(final Supplier supplier, final ContactCreateUpdateForm form) {

        var entity = new Contact();
        entity.setName(form.getName());
        entity.setRole(form.getRole());
        entity.setEmail(form.getEmail());
        entity.setPhoneNo(form.getPhoneNo());
        entity.setLandLine(form.getLandLine());
        entity.setSupplier(supplier);

        var newContact = this.persist(entity);

        return ContactMapper.toInfo(newContact);
    }

    /**
     * @param uuid
     * @return
     */
    public ContactInfo find(final UUID uuid) {
        return ContactMapper.toInfo(this.findByUuid(uuid));
    }

    /**
     * @param uuid
     */
    public void delete(final UUID uuid) {
        var entity = this.findByUuid(uuid);
        entity.setActive(false);
        this.persist(entity);
    }


    /**
     * @param uuid
     * @param form
     * @return
     */
    public ContactInfo update(final UUID uuid, final ContactCreateUpdateForm form) {
        var entity = this.findByUuid(uuid);
        entity.setName(form.getName());
        entity.setRole(form.getRole());
        entity.setEmail(form.getEmail());
        entity.setPhoneNo(form.getPhoneNo());
        entity.setLandLine(form.getLandLine());
        this.persist(entity);
        return ContactMapper.toInfo(entity);
    }

    /**
     * @param uuid
     */
    public void softDelete(final UUID uuid) {
        var entity = this.findByUuid(uuid);
        entity.setActive(false);
        this.persist(entity);
    }

}

