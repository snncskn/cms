package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.ContactInfo;
import com.minetec.backend.dto.info.SupplierInfo;
import com.minetec.backend.entity.Supplier;

import java.util.ArrayList;

public class SupplierMapper {

    public static SupplierInfo toInfo(final Supplier supplier) {
        var info = new SupplierInfo();
        info.setUuid(supplier.getUuid());
        info.setName(supplier.getName());
        info.setAddress(supplier.getAddress());
        info.setRegisterNumber(supplier.getRegisterNumber());
        info.setTaxNumber(supplier.getTaxNumber());
        var contactInfos = new ArrayList<ContactInfo>();
        supplier.getContacts().forEach(contact -> {
            var contactInfo = new ContactInfo();
            contactInfo.setUuid(contact.getUuid());
            contactInfo.setName(contact.getName());
            contactInfo.setEmail(contact.getEmail());
            contactInfo.setRole(contact.getRole());
            contactInfo.setLandLine(contact.getLandLine());
            contactInfo.setPhoneNo(contact.getPhoneNo());
            contactInfos.add(contactInfo);
        });
        info.setContacts(contactInfos);
        return info;
    }

}
