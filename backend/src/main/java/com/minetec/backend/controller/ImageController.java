package com.minetec.backend.controller;

import com.minetec.backend.dto.RestResponse;
import com.minetec.backend.service.ImageService;
import com.minetec.backend.util.Response;
import com.minetec.backend.util.RestResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author Sinan
 */

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
@CrossOrigin
public class ImageController {

    private final ImageService imageService;
    private final RestResponseFactory resp;

    @PostMapping
    public ResponseEntity<RestResponse> upload(@RequestParam("file") final MultipartFile file) {
        final var imageUuid = imageService.uploadFile(file);
        final var restResponse = resp.success("af2724e07271d", "image-uploaded", imageUuid);
        return Response.ok(restResponse);
    }


    @GetMapping(value = {"/{uuid}"})
    public ResponseEntity downloadUrl(@PathVariable final UUID uuid) {
        final var imageInfo = imageService.find(uuid);
        final var restResponse = resp.success("hf5724e07771j", "image-download-url", imageInfo);
        return Response.ok(restResponse);
    }

    @DeleteMapping(value = {"/{uuid}"})
    public ResponseEntity delete(@PathVariable final UUID uuid) {
        final var imageInfo = imageService.delete(uuid);
        final var restResponse = resp.success("vf2724e07171k", "image-delete-url", imageInfo);
        return Response.ok(restResponse);
    }
}
