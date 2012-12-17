package org.purl.wf4ever.rosrs.client.common;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * A test of the annotation class.
 * 
 * @author piotrekhol
 * 
 */
public class AnnotationTest {

    /** RO that will be mapped to local resources. */
    private static final URI RO_PREFIX = URI.create("http://example.org/ro1/");

    /** Annotation that will be mapped to local resources. */
    private static final URI ANN_PREFIX = URI.create("http://example.org/ro1/.ro/annotations/1");

    /** Annotation body that will be mapped to local resources. */
    private static final URI BODY_PREFIX = URI.create("http://example.org/ro1/body1.rdf");

    /** A loaded RO. */
    private static ResearchObject ro1;

    /** A loaded annotation. */
    private static Annotation an1;


    /**
     * Prepare a loaded RO.
     * 
     * @throws ROException
     *             example RO has incorrect data
     * @throws ROSRSException
     *             could not load the example RO
     * @throws IOException
     *             could not load the example annotation
     */
    @BeforeClass
    public static final void setUp()
            throws ROSRSException, ROException, IOException {
        ROSRService rosrs = new ROSRService(URI.create("http://example.org/"), "foo");
        ro1 = new ResearchObject(RO_PREFIX, rosrs);
        ro1.load();
        an1 = new Annotation(ro1, ANN_PREFIX, BODY_PREFIX, RO_PREFIX, URI.create("http://test.myopenid.com"),
                new DateTime(2011, 12, 02, 16, 01, 10, DateTimeZone.UTC));
        an1.load();
    }


    /**
     * Test the constructor.
     */
    @Test
    public final void testAnnotationResearchObjectURIURISetOfURIURIDateTime() {
        Set<URI> targets = new HashSet<>();
        targets.add(RO_PREFIX);
        Annotation annotation = new Annotation(ro1, ANN_PREFIX, BODY_PREFIX, targets,
                URI.create("http://test.myopenid.com"), new DateTime(2011, 12, 02, 16, 01, 10, DateTimeZone.UTC));
        Assert.assertFalse(annotation.isLoaded());
    }


    /**
     * Test the constructor.
     */
    @Test
    public final void testAnnotationResearchObjectURIURIURIURIDateTime() {
        Annotation annotation = new Annotation(ro1, ANN_PREFIX, BODY_PREFIX, RO_PREFIX,
                URI.create("http://test.myopenid.com"), new DateTime(2011, 12, 02, 16, 01, 10, DateTimeZone.UTC));
        Assert.assertFalse(annotation.isLoaded());
    }


    @Test
    public final void testCreateResearchObjectURISetOfURI() {
        fail("Not yet implemented");
    }


    @Test
    public final void testCreateResearchObjectURIURI() {
        fail("Not yet implemented");
    }


    @Test
    public final void testDelete() {
        fail("Not yet implemented");
    }


    @Test
    public final void testLoad() {
        fail("Not yet implemented");
    }


    /**
     * Test get RO.
     */
    @Test
    public final void testGetResearchObject() {
        Assert.assertEquals(ro1, an1.getResearchObject());
    }


    @Test
    public final void testGetUri() {
        Assert.assertEquals(ANN_PREFIX, an1.getUri());
    }


    @Test
    public final void testGetBody() {
        Assert.assertEquals(BODY_PREFIX, an1.getBody());
    }


    @Test
    public final void testGetCreator() {
        Assert.assertEquals(URI.create("http://test.myopenid.com"), an1.getCreator());
    }


    @Test
    public final void testGetCreated() {
        Assert.assertEquals(new DateTime(2011, 12, 02, 16, 01, 10, DateTimeZone.UTC), an1.getCreated());
    }


    @Test
    public final void testGetTargets() {
        Set<URI> targets = new HashSet<>();
        targets.add(RO_PREFIX);
        Assert.assertEquals(targets, an1.getTargets());
    }


    @Test
    public final void testIsLoaded() {
        Assert.assertTrue(an1.isLoaded());
    }


    @Test
    public final void testGetBodySerializedAsString()
            throws IOException {
        String body = an1.getBodySerializedAsString();
        Assert.assertNotNull(body);

        Model ex = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("ro1/body1.rdf")) {
            ex.read(in, BODY_PREFIX.toString());
        }

        Model res = ModelFactory.createDefaultModel();
        try (InputStream in = IOUtils.toInputStream(body)) {
            res.read(in, BODY_PREFIX.toString());
        }
        Assert.assertTrue(ex.isIsomorphicWith(res));
    }
}
