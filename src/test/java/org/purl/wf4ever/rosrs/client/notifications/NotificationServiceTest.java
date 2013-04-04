package org.purl.wf4ever.rosrs.client.notifications;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.exception.NotificationsException;

/**
 * Test of the {@link NotificationService} class.
 * 
 * @author piotrekhol
 * 
 */
public class NotificationServiceTest {

    /** Service URI mapped to a test service description document. */
    private static final URI EXAMPLE_SERVICE_URI = URI.create("http://example.org/notifications/");

    /** Notifications stored in the test feed. */
    private static List<Notification> exampleNotifications;


    /**
     * Create the expected notifications.
     * 
     * @throws Exception
     *             when something unexpected happens
     */
    @BeforeClass
    public static void setUpBeforeClass()
            throws Exception {
        exampleNotifications = new ArrayList<>();

        Notification n1 = new Notification("urn:X-rodl:13", "Quality improvement", "<p>Lorem ipsum</p>");
        n1.setPublished(ISODateTimeFormat.dateTimeParser().parseDateTime("2005-06-13T16:20:02Z"));
        n1.setSource(URI.create("http://sandbox.wf4ever-project.org/roevaluate/"));
        n1.setResearchObjectUri(URI.create("http://example.org/rodl/ROs/myGenes/"));
        exampleNotifications.add(n1);

        Notification n2 = new Notification("urn:X-rodl:15", "Quality decrease", "<strong>Lorem ipsum</strong>");
        n2.setPublished(ISODateTimeFormat.dateTimeParser().parseDateTime("2005-07-21T11:45:07Z"));
        n2.setSource(URI.create("http://sandbox.wf4ever-project.org/roevaluate/"));
        n2.setResearchObjectUri(URI.create("http://example.org/rodl/ROs/myPuzzles/"));
        exampleNotifications.add(n2);
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


    /**
     * Test that an example feed can be parsed.
     * 
     * @throws NotificationsException
     *             the example feed is invalid
     */
    @Test
    public final void testGetNotificationsInputStream()
            throws NotificationsException {
        InputStream feed = getClass().getClassLoader().getResourceAsStream("notifications/exampleFeed.xml");
        NotificationService notificationService = new NotificationService(EXAMPLE_SERVICE_URI, null);
        List<Notification> notifications = notificationService.getNotifications(feed);
        Assert.assertNotNull(notifications);
        Assert.assertEquals(exampleNotifications.size(), notifications.size());
        for (int i = 0; i < notifications.size(); i++) {
            assertNotificationsEquals(exampleNotifications.get(i), notifications.get(i));
        }
    }


    /**
     * Check that all properties of two notifications are the same.
     * 
     * @param n1
     *            expected notification
     * @param n2
     *            tested notification
     */
    private void assertNotificationsEquals(Notification n1, Notification n2) {
        Assert.assertEquals(n1.getId(), n2.getId());
        Assert.assertEquals(n1.getTitle(), n2.getTitle());
        Assert.assertEquals(n1.getContent(), n2.getContent());
        Assert.assertEquals(n1.getPublished(), n2.getPublished());
        Assert.assertEquals(n1.getSource(), n2.getSource());
        Assert.assertEquals(n1.getResearchObjectUri(), n2.getResearchObjectUri());
    }


    @Test
    public final void testGetNotificationsURIDateTimeDateTime() {
        Assert.fail("Not yet implemented");
    }

}
