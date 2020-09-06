package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.SiteInfo;
import com.minetec.backend.entity.Site;

import java.util.ArrayList;
import java.util.List;

public class SiteMapper {

    public static SiteInfo toInfo(final Site site) {
        var info = new SiteInfo();
        info.setUuid(site.getUuid());
        info.setDescription(site.getDescription());
        info.setSupplierId(site.getSupplierId());
        return info;
    }

    public static List<SiteInfo> toInfos(final List<Site> sites) {
        var siteInfos = new ArrayList<SiteInfo>();
        sites.forEach(site -> siteInfos.add(toInfo(site)));
        return siteInfos;
    }
}
