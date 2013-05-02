package org.purl.wf4ever.rosrs.client.evo;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.evo.JobStatus.State;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Test of refreshing the job status.
 * 
 * @author piotrekhol
 * 
 */
public class JobStatusTest extends BaseTest {

    /** A test HTTP mock server. */
    @Rule
    public static final WireMockRule WIREMOCK_RULE = new WireMockRule(8089); // No-args constructor defaults to port 8080


    /**
     * Test that the job status can be refreshed.
     */
    @Test
    public final void testRefresh() {
        JobStatus status = ro.snapshot("ro1-copy");

        while (status.getState() == State.RUNNING) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            status.refresh();
        }
        Assert.assertEquals(State.DONE, status.getState());
    }
}
