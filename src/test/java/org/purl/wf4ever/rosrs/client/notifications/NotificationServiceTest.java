package org.purl.wf4ever.rosrs.client.notifications;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import javax.ws.rs.core.UriBuilder;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test of the {@link NotificationService} class.
 * 
 * @author piotrekhol
 * 
 */
public class NotificationServiceTest {

    /** Service URI mapped to a test service description document. */
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


    /**
     * Test that the notification URI template is expanded properly for all criteria.
     * 
     * @throws UnsupportedEncodingException
     *             when the URL encoder in test uses an invalid encoding
     */
    @Test
    public final void testGetNotificationsUriForAllCriteria()
            throws UnsupportedEncodingException {
        NotificationService notificationService = new NotificationService(EXAMPLE_SERVICE_URI, null);
        URI roUri = URI.create("http://example.org/ROs/ro1/");
        DateTime from = ISODateTimeFormat.dateTimeParser().parseDateTime("2000-06-13T18:20:02.000+02:00");
        DateTime to = ISODateTimeFormat.dateTimeParser().parseDateTime("2006-06-13T18:20:02.000+02:00");

        URI expectedAll = UriBuilder.fromUri(EXAMPLE_SERVICE_URI).path("notifications")
                .queryParam("ro", URLEncoder.encode("http://example.org/ROs/ro1/", "UTF-8"))
                .queryParam("from", URLEncoder.encode("2000-06-13T18:20:02.000+02:00", "UTF-8"))
                .queryParam("to", URLEncoder.encode("2006-06-13T18:20:02.000+02:00", "UTF-8")).build();
        Assert.assertEquals(expectedAll, notificationService.getNotificationsUri(roUri, from, to));
    }


    /**
     * Test that the notification URI template is expanded properly if only one criterion is provided.
     * 
     * @throws UnsupportedEncodingException
     *             when the URL encoder in test uses an invalid encoding
     */
    @Test
    public final void testGetNotificationsUriForOneCriterion()
            throws UnsupportedEncodingException {
        NotificationService notificationService = new NotificationService(EXAMPLE_SERVICE_URI, null);
        DateTime from = ISODateTimeFormat.dateTimeParser().parseDateTime("2000-06-13T18:20:02.000+02:00");

        URI expectedOnlyFrom = UriBuilder.fromUri(EXAMPLE_SERVICE_URI).path("notifications")
                .queryParam("from", URLEncoder.encode("2000-06-13T18:20:02.000+02:00", "UTF-8")).build();
        Assert.assertEquals(expectedOnlyFrom, notificationService.getNotificationsUri(null, from, null));
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
