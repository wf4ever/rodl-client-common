package org.purl.wf4ever.rosrs.client;

import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.notMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Test {@link Resource} methods.
 * 
 * @author piotrekhol
 * 
 */
public class AnnotationTripleTest extends BaseTest {

    /** A test HTTP mock server. */
    @Rule
    public static final WireMockRule WIREMOCK_RULE = new WireMockRule(8089); // No-args constructor defaults to port 8080

    /** A test resource. */
    private Resource res1;


    /**
     * Set up a sample RO.
     * 
     * @throws Exception
     *             if there are any problem with test resources
     */
    @Before
    public void setUp()
            throws Exception {
        super.setUp();
        res1 = new Resource(ro1, MOCK_RESOURCE, MOCK_RESOURCE_PROXY, PERSON_1, new DateTime(2011, 12, 02, 15, 02, 10,
                DateTimeZone.UTC));
    }


    /**
     * See name.
     * 
     * @throws ROSRSException
     *             wiremock error
     */
    @Test
    public final void shouldUpdateAllComments()
            throws ROSRSException {
        List<AnnotationTriple> list = res1.getPropertyValues(RDFS_COMMENT, true);
        assertThat(list, hasSize(equalTo(1)));
        AnnotationTriple triple = list.get(0);
        triple.updateValue("Res1 comment 3");

        verify(putRequestedFor(urlEqualTo("/ro1/body.rdf")).withRequestBody(matching(".*Res1 comment 3.*")));

        list = res1.getPropertyValues(RDFS_COMMENT, true);
        assertThat(list, hasSize(equalTo(1)));
    }


    /**
     * See name.
     * 
     * @throws ROSRSException
     *             wiremock error
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void shouldUpdateOneComment()
            throws ROSRSException {
        List<AnnotationTriple> list = res1.getPropertyValues(RDFS_COMMENT, false);
        assertThat(list, hasSize(equalTo(2)));
        AnnotationTriple triple = list.get(0);
        triple.updateValue("Res1 comment 3");
        String theOtherValue = list.get(1).getValue();

        verify(putRequestedFor(urlEqualTo("/ro1/body.rdf")).withRequestBody(matching(".*Res1 comment 3.*")));

        List<?> list2 = res1.getPropertyValues(RDFS_COMMENT, false);
        assertThat(list, hasSize(equalTo(2)));
        assertThat((List<Object>) list2, hasItem(hasProperty("value", Matchers.equalTo("Res1 comment 3"))));
        assertThat((List<Object>) list2, hasItem(hasProperty("value", Matchers.equalTo(theOtherValue))));
    }


    /**
     * See name.
     * 
     * @throws ROSRSException
     *             wiremock error
     * @throws ROException
     *             incorrect manifest
     */
    @Test
    public final void shouldDeleteAllComments()
            throws ROSRSException, ROException {
        List<AnnotationTriple> list = res1.getPropertyValues(RDFS_COMMENT, true);
        assertThat(list, hasSize(equalTo(1)));
        AnnotationTriple triple = list.get(0);
        triple.delete();

        verify(putRequestedFor(urlEqualTo("/ro1/body.rdf")).withRequestBody(notMatching(".*Res1 comment.*")));
    }


    /**
     * See name.
     * 
     * @throws ROSRSException
     *             wiremock error
     */
    @Test
    public final void shouldDeleteOneComment()
            throws ROSRSException {
        List<AnnotationTriple> list = res1.getPropertyValues(RDFS_COMMENT, false);
        assertThat(list, hasSize(equalTo(2)));
        AnnotationTriple triple = list.get(0);
        String value = triple.getValue();
        triple.delete();

        verify(putRequestedFor(urlEqualTo("/ro1/body.rdf")).withRequestBody(notMatching(".*" + value + ".*")));
    }

}
