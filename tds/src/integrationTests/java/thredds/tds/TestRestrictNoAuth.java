/*
 * Copyright (c) 1998-2021 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */
package thredds.tds;

import org.apache.http.HttpStatus;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assert;
import org.junit.Test;
import thredds.test.util.TestOnLocalServer;
import ucar.httpservices.HTTPException;
import ucar.httpservices.HTTPFactory;
import ucar.httpservices.HTTPMethod;
import ucar.httpservices.HTTPSession;
import java.lang.invoke.MethodHandles;

import static thredds.test.util.TestOnLocalServer.clearCredentials;

/**
 * Test that restricted datasets fail when not authorized
 *
 * @author caron
 * @since 4/21/2015
 */
public class TestRestrictNoAuth {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Test
  public void testFailNoAuth() {
    String endpoint = TestOnLocalServer.withHttpPath("/dodsC/testRestrictedDataset/testData2.nc.dds");
    logger.info(String.format("testRestriction req = '%s'", endpoint));

    try (HTTPSession session = HTTPFactory.newSession(endpoint)) {
      clearCredentials(session);
      HTTPMethod method = HTTPFactory.Get(session);
      int statusCode = method.execute();

      Assert.assertTrue("Expected HttpStatus.SC_UNAUTHORIZED|HttpStatus.SC_FORBIDDEN",
          statusCode == HttpStatus.SC_UNAUTHORIZED || statusCode == HttpStatus.SC_FORBIDDEN);

    } catch (ucar.httpservices.HTTPException e) {
      Assert.fail(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }
}
