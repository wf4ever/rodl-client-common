/**
 * 
 */
package org.purl.wf4ever.rosrs.client.search;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.purl.wf4ever.rosrs.client.Creator;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.exception.SearchException;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * This service fetches prepares a query using the OpenSearch API and sends it to dLibra, later it parses the responses.
 * 
 * @author piotrek
 * 
 */
public class OpenSearchSearchServer implements SearchServer {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(OpenSearchSearchServer.class);

    /** dLibra namespace. */
    private static final String DL_QUERY_NS = "http://dlibra.psnc.pl/opensearch/";

    /** date format for parsing the dates in search results. */
    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");

    /** search module URI. */
    private URI endpointUri;


    /**
     * Constructor.
     * 
     * @param endpointUri
     *            search module URI
     */
    public OpenSearchSearchServer(URI endpointUri) {
        this.endpointUri = endpointUri;
    }


    /**
     * Performs a search in RODL.
     * 
     * @param keywords
     *            words to look for
     * @return list of search results
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<SearchResult> search(String keywords)
            throws SearchException {
        URI queryURI = new UriBuilderImpl().uri(endpointUri).queryParam("searchTerms", keywords)
                .queryParam("aggregate", "false").queryParam("count", 50).build();

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed;
        try {
            feed = input.build(new XmlReader(queryURI.toURL()));
        } catch (IllegalArgumentException | FeedException | IOException e1) {
            throw new SearchException("Error when loading the search results", e1);
        }

        List<SyndEntry> entries = feed.getEntries();
        List<SearchResult> ros = new ArrayList<>();
        for (SyndEntry entry : entries) {
            URI researchObjectURI = null;
            DateTime created = null;
            String title = null;
            Set<Creator> creators = new HashSet<>();
            double score = -1;
            List<Element> dlMarkup = (List<Element>) entry.getForeignMarkup();
            for (Element element : dlMarkup) {
                if (!DL_QUERY_NS.equals(element.getNamespaceURI())) {
                    continue;
                }
                switch (element.getName()) {
                    case "attribute":
                        switch (element.getAttributeValue("name")) {
                            case "Identifier":
                                researchObjectURI = URI.create(element.getValue());
                                break;
                            case "Creator":
                                //                                creators.add(Creator.get(ums, usernames, element.getValue()));
                                break;
                            case "Created":
                                try {
                                    created = new DateTime(SDF.parse(element.getValue()).getTime());
                                } catch (ParseException e) {
                                    LOG.warn("Incorrect date", e);
                                    created = null;
                                }
                                break;
                            case "Title":
                                title = element.getValue();
                                break;
                            default:
                                break;
                        }
                        break;
                    case "score":
                        score = Double.parseDouble(element.getValue());
                        break;
                    default:
                        break;
                }
            }

            if (researchObjectURI != null && score != -1) {
                ResearchObject ro = new ResearchObject(researchObjectURI, null);
                ro.setCreated(created);
                ro.setCreators(creators);
                //                ro.setTitle(title);
                ros.add(new SearchResult(ro, score));
            }
        }

        return ros;
    }
}
