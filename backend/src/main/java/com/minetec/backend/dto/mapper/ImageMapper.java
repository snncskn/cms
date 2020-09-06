package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.ImageInfo;
import com.minetec.backend.entity.Image;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImageMapper {

    public static ImageInfo toInfo(final Image entity) {
        var imageInfo = new ImageInfo();
        imageInfo.setSelected(entity.isSelected());
        imageInfo.setUuid(entity.getUuid());
        return imageInfo;
    }

    public static List<ImageInfo> toInfos(final List<Image> images) {
        var imageInfos = new ArrayList<ImageInfo>();
        images.forEach(image -> imageInfos.add(toInfo(image)));
        return imageInfos;
    }
}
