package org.purl.wf4ever.rosrs.client.notifications;

import java.net.URI;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NotificationServiceTest {

    private static final URI EXAMPLE_SERVICE_URI = URI.create("http://example.org/notifications/");


    @BeforeClass
    public static void setUpBeforeClass()
            throws Exception {
    }


    @AfterClass
    public static void tearDownAfterClass()
            throws Exception {
    }


    @Before
    public void setUp()
            throws Exception {
    }


    @After
    public void tearDown()
            throws Exception {
    }


    /**
     * Test that the service description document is loaded properly.
     */
    @Test
    public final void testInit() {
        NotificationService notificationService = new NotificationService(EXAMPLE_SERVICE_URI, null);
        notificationService.init();
        Assert.assertEquals("notifications{?ro,from,to}", notificationService.getNotificationsUriTemplateString());
    }


    @Test
    public final void testGetNotificationsUri() {
        Assert.fail("Not yet implemented");
    }


    @Test
    public final void testGetNotificationsInputStream() {
        Assert.fail("Not yet implemented");
    }


    @Test
    public final void testGetNotificationsURIDateTimeDateTime() {
        Assert.fail("Not yet implemented");
    }

}
