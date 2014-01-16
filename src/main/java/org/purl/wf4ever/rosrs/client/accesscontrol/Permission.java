package org.purl.wf4ever.rosrs.client.accesscontrol;

import java.beans.Transient;
import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data produced/received by permission API.
 * 
 * @author pejot
 * 
 */
@XmlRootElement(name = "permission")
public class Permission implements Serializable{

	/** Serialization. */
	private static final long serialVersionUID = 1L;
	/** Unique id. */
	private int id;
	/** Research Object uri. */
	private String roUri;
	/** Object location. */
	private URI uri;
	/** User role. */
	private Role role;
	/** User login. */
	private String user;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlElement(name = "ro", required = true)
	public String getRo() {
		return roUri;
	}

	public void setRo(String roUri) {
		this.roUri = roUri;
	}

	/**
	 * JSON user field getter.
	 * 
	 * @return user id
	 */
	@XmlElement(name = "user", required = true)
	public String getUserLogin() {
		return user;
	}

	/**
	 * JSON user field setter.
	 * 
	 * @param user
	 *            user profile
	 */
	public void setUserLogin(String user) {
		this.user = user;
	}

	@XmlElement(required = true)
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Transient
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
}
