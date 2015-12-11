/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.integration.util.SignatureHelper;

/**
 * Authenticates RESTful web service resources based on a username and signature provided in the request header. If authentication fails
 * then a "x-authentication-failed"="true" header is added to the response. The resource can then use that header to generate the
 * appropriate error response.
 *
 */
public class RestSignatureHeaderFilter extends OncePerRequestFilter {
  @Autowired
  AuthenticationManager authenticationManager;
  @Autowired
  SecurityManager securityManager;

  /** Used during development only. Set this to true to use REST resources without authentication. Good for manual testing/exploration. */
  private static boolean UNAUTHENTICATED_MODE = false;
  /** Resources created (POST) and modified (PUT) will be associated with this user in UNAUTHENTICATED_MODE. This user must exist. */
  private static String UNAUTHENTICATED_MODE_USER = "admin";

  private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

  /**
   * Creates a new RestSignatureFilter instance with a default HttpSessionSecurityContextRepository set
   */
  public RestSignatureHeaderFilter() {
    super();
  }

  /**
   * Creates a new RestSignatureFilter instance with a defined SecurityContextRepository
   * 
   * @param securityContextRepository
   *          of type SecurityContextRepository
   */
  public RestSignatureHeaderFilter(SecurityContextRepository securityContextRepository) {
    super();
    this.securityContextRepository = securityContextRepository;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (UNAUTHENTICATED_MODE) {
      doFilterUnauthenticatedMode(request, response, filterChain);
    } else {
      doFilterInternalAuthenticated(request, response, filterChain);
    }
  }

  protected void doFilterInternalAuthenticated(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    boolean authenticated = false;

    String loginName = getHeader(request, SignatureHelper.USER_HEADER);
    String signature = getHeader(request, SignatureHelper.SIGNATURE_HEADER);
    if (loginName != null && signature != null) {
      com.eaglegenomics.simlims.core.User user = getUser(loginName);
      if (user != null) {
        User userdetails = LimsSecurityUtils.toUserDetails(user);
        boolean validSignature = validateSignature(request, user, signature);
        if (validSignature && userdetails != null) {
          SecurityContext sc = securityContextRepository.loadContext(new HttpRequestResponseHolder(request, response));
          if (sc != null && sc.getAuthentication() != null) {
            logger.debug("User already logged in.");
            SecurityContextHolder.getContextHolderStrategy().setContext(sc);
          } else {
            setupSecurity(userdetails);
          }
          authenticated = true;
        }
      }
    }

    if (!authenticated) {
      response.setHeader("x-authentication-failed", "true");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      logger.debug("Rest authentication failed. Set header 'x-authentication-failed' to 'true'.");
    }
    filterChain.doFilter(request, response);
  }

  private void setupSecurity(User userdetails) {
    PreAuthenticatedAuthenticationToken newAuthentication = new PreAuthenticatedAuthenticationToken(userdetails, userdetails.getPassword(),
        userdetails.getAuthorities());
    newAuthentication.setAuthenticated(true);
    newAuthentication.setDetails(userdetails);

    SecurityContext sc = SecurityContextHolder.getContextHolderStrategy().getContext();
    sc.setAuthentication(newAuthentication);
    SecurityContextHolder.getContextHolderStrategy().setContext(sc);
  }

  private boolean validateSignature(HttpServletRequest request, com.eaglegenomics.simlims.core.User user, String signature) {
    boolean validSignature = false;
    try {
      validSignature = SignatureHelper.validateSignature(request,
          SignatureHelper.generatePrivateUserKey((user.getLoginName() + "::" + user.getPassword()).getBytes("UTF-8")), signature);
    } catch (InvalidKeyException e) {
      logger.debug("Rest authentication failed. Unable to validate signature. ", e);
    } catch (NoSuchAlgorithmException e) {
      logger.debug("Rest authentication failed. Unable to validate signature. ", e);
    } catch (UnsupportedEncodingException e) {
      logger.debug("Rest authentication failed. Unable to validate signature. ", e);
    } catch (Exception e) {
      logger.debug("Rest authentication failed. Unable to validate signature. ", e);
    }
    if (!validSignature) {
      logger.debug("Rest authentication failed. Signature '" + signature + "' is not valid.");
    }
    return validSignature;
  }

  private String getHeader(HttpServletRequest request, String headerName) {
    String headerValue = request.getHeader(headerName);
    if (headerValue == null) {
      logger.debug("Rest authentication failed. Required header '" + headerName + "' is null.");
    }
    return headerValue;
  }

  private com.eaglegenomics.simlims.core.User getUser(String loginName) throws IOException {
    com.eaglegenomics.simlims.core.User user = securityManager.getUserByLoginName(loginName);
    if (user == null) {
      logger.debug("Rest authentication failed. Unable to retrieve user for login name '" + loginName + "'.");
    }
    return user;
  }

  private void doFilterUnauthenticatedMode(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    StringBuilder sb = new StringBuilder();
    sb.append("**************************************************************************************\n");
    sb.append("**  DANGER!! REST requests in MISO are currently unauthenticated. This is suitable  **\n");
    sb.append("**  during development only. Adjust this setting in the RestSignatureFilter class.  **\n");
    sb.append("**************************************************************************************\n");
    logger.error(sb.toString());

    User userdetails = null;
    com.eaglegenomics.simlims.core.User user;
    user = securityManager.getUserByLoginName(UNAUTHENTICATED_MODE_USER);
    if (user != null) {
      userdetails = LimsSecurityUtils.toUserDetails(user);
    }

    if (userdetails != null) {
      PreAuthenticatedAuthenticationToken newAuthentication = new PreAuthenticatedAuthenticationToken(userdetails,
          userdetails.getPassword(), userdetails.getAuthorities());
      newAuthentication.setAuthenticated(true);
      newAuthentication.setDetails(userdetails);

      try {
        SecurityContext sc = SecurityContextHolder.getContextHolderStrategy().getContext();
        sc.setAuthentication(newAuthentication);
        SecurityContextHolder.getContextHolderStrategy().setContext(sc);
        logger.debug("Set context - chaining");
      } catch (AuthenticationException a) {
        logger.error("filter", a);
      }
      filterChain.doFilter(request, response);
    } else {
      throw new AuthenticationCredentialsNotFoundException("No valid user found to authenticate");
    }
  }
}
