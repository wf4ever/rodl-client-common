package org.purl.wf4ever.rosrs.client.search;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.exception.SearchException;

public class SolrSearchServerTest {

    public static URI SERVER_URI = URI.create("http://sandbox.wf4ever-project.org/solr/");
    SearchServer server;


    @Before
    public void setUp()
            throws SearchException {
        server = new SolrSearchServer(SERVER_URI);
    }


    @Test
    public void testSearch()
            throws SearchException {
        SearchResult result = server.search("sandbox");
        System.out.println(result.getROsList().size());
    }
}
