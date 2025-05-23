/*
 * Copyright (c) 1998-2025 John Caron and University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package thredds.servlet.restrict;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import thredds.server.config.TdsContext;
import thredds.servlet.ServletUtil;

/**
 * Use Tomcat security.
 *
 * @author caron
 */
public class TomcatAuthorizer implements Authorizer {
  private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TomcatAuthorizer.class);

  @Autowired
  private TdsContext tdsContext;

  private boolean useSSL = false;
  private String sslPort = "-1";

  public void setUseSSL(boolean useSSL) {
    this.useSSL = useSSL;
  }

  public void setSslPort(String sslPort) {
    this.sslPort = sslPort;
  }

  public void setRoleSource(RoleSource db) {
    // not used
  }

  public boolean authorize(HttpServletRequest req, HttpServletResponse res, String role) throws IOException {
    if (log.isDebugEnabled())
      log.debug("TomcatAuthorizer.authorize req=" + ServletUtil.getRequest(req));

    if (req.isUserInRole(role)) {
      if (log.isDebugEnabled())
        log.debug("TomcatAuthorizer.authorize ok for role {}", role);
      return true;
    }

    // create session to hold information for redirect after authentication / authorization
    HttpSession session = req.getSession(true);
    session.setAttribute("origRequest", ServletUtil.getRequest(req));
    session.setAttribute("role", role);

    // allow users to override http vs https using bean properties in applicationContext.xml
    // for backwards compatibility
    final String server;
    if (sslPort.equals("-1")) {
      // default case: construct endpoint using incoming request details
      server = ServletUtil.getRequestServer(req);
    } else {
      // sslPort was modified, indicating that bean properties in applicationContext.xml should be
      // used
      server = useSSL ? "https://" + req.getServerName() + ":" + sslPort
          : "http://" + req.getServerName() + ":" + req.getServerPort();
    }

    String urlr = server + tdsContext.getContextPath() + "/restrictedAccess/" + role;
    if (log.isDebugEnabled())
      log.debug("redirect to = {}", urlr);
    res.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
    res.addHeader("Location", urlr);
    res.setHeader("Last-Modified", ""); // LOOK
    return false;
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    HttpSession session = req.getSession();
    if (session != null) {
      String origURI = (String) session.getAttribute("origRequest");
      String role = (String) session.getAttribute("role");

      if (req.isUserInRole(role)) {

        if (origURI != null) {
          res.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
          String frag = (origURI.indexOf("?") > 0) ? "&auth" : "?auth"; // WTF ?? breaks simple authentication, eg on
                                                                        // opendap
          // res.addHeader("Location", origURI+frag); // comment out for now 12/22/2010 - needed for CAS or CAMS or ESG
          // ?
          res.addHeader("Location", origURI);
          if (log.isDebugEnabled())
            log.debug("redirect to origRequest = " + origURI); // +frag);
          return;

        } else {
          res.setStatus(HttpServletResponse.SC_OK); // someone came directly to this page
          return;
        }
      }
    }

    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized to access this dataset.");
  }

}
