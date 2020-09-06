package com.minetec.backend.dto.info;

import lombok.Data;

import java.util.UUID;

@Data
public class ImageInfo {
    private UUID uuid;
    private boolean selected;
    private String downloadUrl;
}
