package com.minetec.backend.service;

import com.auth0.jwt.JWT;
import com.minetec.backend.dto.form.LoginForm;
import com.minetec.backend.dto.info.UserInfo;
import com.minetec.backend.dto.mapper.SupplierMapper;
import com.minetec.backend.error_handling.exception.AccessNotAllowedException;
import com.minetec.backend.security.filter.SecurityConstants;
import com.minetec.backend.dto.mapper.UserMapper;
import com.minetec.backend.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

/**
 * @author Sinan
 */

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final SupplierService supplierService;
    private final SecurityConstants securityConstants;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserInfo authenticateLoginCredentials(final LoginForm form) {

        var user = userService.getRepository().findByEmail(form.getEmail()).orElseThrow(() ->
            new AccessNotAllowedException("8cd06beb0239", "user-or-pass-wrong"));

        if (!passCheck(form.getPassword(), user.getPassword())) {
            throw new AccessNotAllowedException("2cd01beb1225", "user-or-pass-wrong");
        }

        final var userInfo = UserMapper.toInfo(user);

        userInfo.setToken(JWT.create()
            .withSubject(user.getUuid().toString())
            .withExpiresAt(new Date(System.currentTimeMillis() + securityConstants.getExpirationTime()))
            .sign(HMAC512(securityConstants.getSecretKey().getBytes())));

        if (!Utils.isEmpty(userInfo.getSiteInfos())) {
            /**if siteInfos not null, we set a default site **/
            userInfo.setCurrentSite(userInfo.getSiteInfos().get(0));
            userInfo.getSiteInfos().forEach(siteInfo -> {
                if (!Utils.isEmpty(siteInfo.getSupplierId())) {
                    final var supplier = supplierService.findById(siteInfo.getSupplierId());
                    siteInfo.setSupplierInfo(SupplierMapper.toInfo(supplier));
                }
            });
        }

        return userInfo;
    }

    /**
     * @param loginPass
     * @param userPass
     * @return
     */
    private boolean passCheck(final String loginPass, final String userPass) {
        return bCryptPasswordEncoder.matches(loginPass, userPass);
    }
}
