package com.minetec.backend.security.filter;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Sinan
 */
@Data
@Component
public class SecurityConstants {

    @Value("${security.expirationType}")
    public Long expirationTime;
    @Value("${security.secretKey}")
    public String secretKey;
}
