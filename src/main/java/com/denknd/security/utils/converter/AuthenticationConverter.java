package com.denknd.security.utils.converter;

import com.denknd.exception.BadCredentialsException;
import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationConverter {
  PreAuthenticatedAuthenticationToken convert(HttpServletRequest httpRequest) throws BadCredentialsException;
}
