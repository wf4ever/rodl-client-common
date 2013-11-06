package org.purl.wf4ever.rosrs.client.accesscontrol;

import java.beans.Transient;
import java.net.URI;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Mode model produced and consumed by Resource Mode API.
 * 
 * @author pejot
 * 
 */
@XmlRootElement(name = "mode")
public class AccessMode {

	/** Object location. */
	private URI uri;
	/** Research Object uri. */
	private String roUri;
	/** Research Object access mode. */
	private Mode mode;

	@XmlElement(name = "ro", required = true)
	public String getRo() {
		return roUri;
	}

	public void setRo(String roUri) {
		this.roUri = roUri;
	}

	@XmlElement(required = true)
	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	@Transient
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

}
