package com.minetec.backend.service;

import com.minetec.backend.dto.info.ImageInfo;
import com.minetec.backend.dto.mapper.ImageMapper;
import com.minetec.backend.entity.Image;
import com.minetec.backend.entity.Item;
import com.minetec.backend.entity.Supplier;
import com.minetec.backend.entity.User;
import com.minetec.backend.entity.Vehicle;
import com.minetec.backend.entity.workshop.JobCard;
import com.minetec.backend.error_handling.exception.BadRequestException;
import com.minetec.backend.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService extends EntityService<Image, ImageRepository> {

    private final ImageStorage imageStorage;

    public ImageInfo uploadFile(@NotNull final MultipartFile file) {

        final long size = file.getSize();

        if (size > Integer.parseInt(imageStorage.getSizeLimit())) {
            throw new BadRequestException("55593235ce41", "file-size-limit-exceeded");
        }

        final byte[] bytes = getBytes(file);

        if (!isImage(bytes)) {
            throw new BadRequestException("5e9be9a1c4be", "bad-request");
        }

        final var image = new Image();

        image.setName(file.getName());
        image.setSize(file.getSize());

        this.persist(image);

        imageStorage.upload(bytes, image.getUuid().toString());

        var imageInfo = ImageMapper.toInfo(image);
        imageInfo.setDownloadUrl(this.imageUrl(image.getUuid()));

        return imageInfo;
    }


    private boolean isImage(final byte[] bytes) {
        return true;
    }

    private byte[] getBytes(@NotNull final MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * image
     *
     * @param entity
     * @param imageList
     */
    public void updateTo(@NotNull final Vehicle entity, @NotNull final List<ImageInfo> imageList) {
        imageList.forEach(item -> {
            var image = this.findByUuid(item.getUuid());
            image.setSelected(item.isSelected());
            image.setVehicle(entity);
            this.persist(image);
        });
    }

    /**
     * image
     *
     * @param entity
     * @param imageList
     */
    public void updateTo(@NotNull final Item entity, @NotNull final List<ImageInfo> imageList) {
        imageList.forEach(item -> {
            var image = this.findByUuid(item.getUuid());
            image.setSelected(item.isSelected());
            image.setItem(entity);
            this.persist(image);
        });
    }

    public void updateTo(@NotNull final User entity, @NotNull final ImageInfo imageInfo) {
        var image = this.findByUuid(imageInfo.getUuid());
        image.setSelected(imageInfo.isSelected());
        image.setUser(entity);
        this.persist(image);
    }

    /**
     * @param uuid
     * @return
     */
    public ImageInfo find(@NotNull final UUID uuid) {
        var imageInfo = ImageMapper.toInfo(this.findByUuid(uuid));
        StringBuilder sb = new StringBuilder(imageStorage.getDownloadUrl()).append(uuid.toString());
        imageInfo.setDownloadUrl(sb.toString());
        return imageInfo;
    }

    /**
     * delete imageKit and db
     *
     * @param uuid
     */
    public boolean delete(@NotNull final UUID uuid) {
        this.deleteByUuid(uuid);
        return imageStorage.delete(uuid.toString());
    }

    /**
     * @param uuid
     * @return
     */
    public String imageUrl(@NotNull final UUID uuid) {
        return new StringBuilder(imageStorage.getDownloadUrl()).append(uuid.toString()).toString();
    }


    /**
     * @param jobCard
     * @param imageList
     */
    public void updateTo(@NotNull final JobCard jobCard, @NotNull final List<ImageInfo> imageList) {
        imageList.forEach(item -> {
            var image = this.findByUuid(item.getUuid());
            image.setSelected(item.isSelected());
            image.setJobCard(jobCard);
            this.persist(image);
        });
    }


    /**
     * image
     *
     * @param entity
     * @param imageList
     */
    public void updateTo(@NotNull final Supplier entity, @NotNull final List<ImageInfo> imageList) {
        imageList.forEach(item -> {
            var image = this.findByUuid(item.getUuid());
            image.setSelected(item.isSelected());
            image.setSupplier(entity);
            this.persist(image);
        });
    }
}
