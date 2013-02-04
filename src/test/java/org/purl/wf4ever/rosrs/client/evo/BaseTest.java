package org.purl.wf4ever.rosrs.client.evo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.purl.wf4ever.rosrs.client.ROSRService;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

/**
 * A test class for the RO evo service.
 * 
 * @author piotrekhol
 * 
 */
public class BaseTest {

    /** ro evo service. */
    protected static ROEVOService roevo;

    /** ROSR service. */
    protected static ROSRService rosrs;

    /** RODL access token. */
    private static final String TOKEN = "32801fc0-1df1-4e34-b";

    /** RODL URI for testing. */
    private static final URI RODL_URI = URI.create("http://localhost:8082/");

    //    /** RODL URI for testing. */
    //    private static final URI RODL_URI = URI.create("http://sandbox.wf4ever-project.org/rodl/");
    /** ROs to delete after a test. */
    protected static List<ResearchObject> rosToDelete = new ArrayList<>();

    /** The Live RO. */
    protected ResearchObject ro;


    /**
     * Prepare an ROEVO service.
     * 
     * @throws ROException
     *             example RO has incorrect data
     * @throws ROSRSException
     *             could not load the example RO
     */
    @BeforeClass
    public static final void setUpBeforeClass()
            throws ROSRSException, ROException {
        rosrs = new ROSRService(RODL_URI.resolve("ROs/"), TOKEN);
        roevo = new ROEVOService(RODL_URI.resolve("evo/"), TOKEN);
    }


    /**
     * Create the Live RO.
     * 
     * @throws ROSRSException
     *             unexpected response from RODL
     */
    @Before
    public final void setUp()
            throws ROSRSException {
        ro = new ResearchObject(rosrs.getRosrsURI().resolve("ROEVOServiceTest/"), rosrs);
        ro.delete();
        ro = ResearchObject.create(rosrs, "ROEVOServiceTest");
        rosToDelete.add(ro);
    }


    /**
     * Remove all ROs created in the tests.
     * 
     * @throws ROSRSException
     *             unexpected response from RODL
     */
    @After
    public void tearDown()
            throws ROSRSException {
        for (ResearchObject roToDelete : rosToDelete) {
            roToDelete.delete();
        }
    }

}
