package com.minetec.backend.security.filter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
public class AuthorizationDetail {
    private UUID siteUuid;
}
