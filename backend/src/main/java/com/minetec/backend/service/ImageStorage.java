package com.minetec.backend.service;

import com.minetec.backend.error_handling.exception.ErrorOccurredException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * @author Sinan
 */

@Data
@Component
public class ImageStorage {

    private static final String ALGORITHM = "HmacSHA1";

    @Value("${imageKit.publicApiKey}")
    private String publicApiKey;

    @Value("${imageKit.privateApiKey}")
    private String privateApiKey;

    @Value("${imageKit.uploadUrl}")
    private String uploadUrl;

    @Value("${imageKit.uploadFolder}")
    private String uploadFolder;

    @Value("${imageKit.downloadUrl}")
    private String downloadUrl;

    @Value("${imageKit.deleteUrl}")
    private String deleteUrl;

    @Value("${imageKit.imagekitId}")
    private String imagekitId;

    @Value("${imageKit.sizeLimit}")
    private String sizeLimit;

    private static String toHexString(final byte[] bytes) {
        var formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private HttpEntity<MultiValueMap<String, Object>> prepareUploadRequestEntity(final byte[] bytes,
                                                                                 final String filename) {

        final HttpHeaders headersSimpleData = new HttpHeaders();
        headersSimpleData.setContentType(MediaType.MULTIPART_FORM_DATA);

        final String currentTime = Long.toString(System.currentTimeMillis() / 1000L);
        final String content = "apiKey=" + publicApiKey + "&filename=" + filename + "&timestamp=" + currentTime;

        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("filename", filename);
        body.add("folder", "");
        body.add("apiKey", publicApiKey);
        body.add("timestamp", currentTime);
        body.add("signature", generateUploadSignature(content));
        body.add("useUniqueFilename", false);

        final Resource xmlFile = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        final var pictureHeader = new HttpHeaders();
        pictureHeader.setContentType(MediaType.IMAGE_JPEG);
        var picturePart = new HttpEntity<>(xmlFile, pictureHeader);
        body.add("file", picturePart);

        return new HttpEntity<>(body, headersSimpleData);
    }

    private HttpEntity<MultiValueMap<String, Object>> prepareDeleteRequestEntity(final String filename) {
        final HttpHeaders headersSimpleData = new HttpHeaders();
        headersSimpleData.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final String content = "imagekitId=" + getImagekitId() + "&path=" + filename;

        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("imagekitId", getImagekitId());
        body.add("signature", generateUploadSignature(content));
        body.add("path", filename);

        return new HttpEntity<>(body, headersSimpleData);
    }


    private String generateUploadSignature(final String content) {
        String encoded;
        try {
            var signingKey = new SecretKeySpec(privateApiKey.getBytes(), ALGORITHM);
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(signingKey);
            encoded = toHexString(mac.doFinal(content.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Cannot create signature.", e);
        }
        return encoded;
    }

    /**
     * @param bytes
     * @param filename
     * @return
     */
    public String upload(final byte[] bytes, final String filename) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForEntity(uploadUrl, prepareUploadRequestEntity(bytes, filename), String.class);
            return uploadFolder + "/" + filename;
        } catch (HttpClientErrorException ex) {
            throw new ErrorOccurredException("84dbe2e8f52a", "cannot-updated-image-to-cloud", ex);
        }
    }

    /**
     * @param filename
     * @return
     */
    public boolean delete(final String filename) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForEntity(deleteUrl, prepareDeleteRequestEntity(filename), String.class);
            return true;
        } catch (HttpClientErrorException ex) {
            throw new ErrorOccurredException("16dce2e3f82g", "cannot-deleted-image-to-cloud", ex);
        }
    }

}

