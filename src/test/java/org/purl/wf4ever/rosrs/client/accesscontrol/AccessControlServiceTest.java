package org.purl.wf4ever.rosrs.client.accesscontrol;

import java.net.URI;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Test of the {@link AccessControlService} class.
 * 
 * @author pejot
 * 
 */
public class AccessControlServiceTest {

	private static final URI EXAMPLE_SERVICE_URI = URI.create("http://example.org/accesscontrol/");

	/**
	 * Service URI of the mock HTTP server, mapped to a test service description
	 * document.
	 */
	private static final URI MOCK_SERVICE_URI = URI.create("http://localhost:8089/");

	/** A test HTTP mock server. */
	@Rule
	public static final WireMockRule WIREMOCK_RULE = new WireMockRule(8089);

	/**
	 * Test that the service description document is loaded properly.
	 */
	@Test
	public final void testInit() {
		AccessControlService accessControlService = new AccessControlService(EXAMPLE_SERVICE_URI,
				null);
		accessControlService.init();
		Assert.assertEquals("accesscontrol/permissions{?ro}",
				accessControlService.getPermissionsUriTemplateString());
		Assert.assertEquals("accesscontrol/modes{?ro}",
				accessControlService.getModesUriTemplateString());
	}
}
