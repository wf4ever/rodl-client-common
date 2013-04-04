package org.purl.wf4ever.rosrs.client.notifications;

import java.io.Serializable;
import java.net.URI;

import org.joda.time.DateTime;
import org.purl.wf4ever.rosrs.client.ResearchObject;

/**
 * This class represents a notification as specified in the notification API. This class is agnostic of the method the
 * notification was fetched (i.e. via an Atom feed).
 * 
 * @see http 
 *      ://wf4ever-project.org/wiki/display/docs/Showcase+129.+The+notification+service#Showcase129.Thenotificationservice
 *      -Notifications
 * @author piotrekhol
 * 
 */
public class Notification implements Serializable {

    /** id. */
    private static final long serialVersionUID = -5085443511142956798L;

    /** Notification title. */
    private String title;

    /** Research Object which is related to this notification. */
    private ResearchObject researchObject;

    /** Identifier of the service generating this notification. */
    private URI source;

    /** Notification identifier. */
    private String id;

    /** Notification creation timestamp. */
    private DateTime published;

    /** Notification content in HTML. */
    private String content;


    /**
     * Constructor.
     * 
     * @param id
     *            Notification identifier
     * @param title
     *            Notification title
     * @param summary
     *            Notification content in HTML
     */
    public Notification(String id, String title, String summary) {
        this.id = id;
        this.title = title;
        this.content = summary;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public ResearchObject getResearchObject() {
        return researchObject;
    }


    public void setResearchObject(ResearchObject researchObject) {
        this.researchObject = researchObject;
    }


    public URI getSource() {
        return source;
    }


    public void setSource(URI source) {
        this.source = source;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public DateTime getPublished() {
        return published;
    }


    public void setPublished(DateTime published) {
        this.published = published;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }

}
