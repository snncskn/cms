package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.ContactInfo;
import com.minetec.backend.entity.Contact;

public class ContactMapper {
    /**
     * @param contact
     * @return
     */
    public static ContactInfo toInfo(final Contact contact) {
        var info = new ContactInfo();
        info.setUuid(contact.getUuid());
        info.setName(contact.getName());
        info.setRole(contact.getRole());
        info.setEmail(contact.getEmail());
        info.setPhoneNo(contact.getPhoneNo());
        info.setLandLine(contact.getLandLine());
        return info;
    }
}
