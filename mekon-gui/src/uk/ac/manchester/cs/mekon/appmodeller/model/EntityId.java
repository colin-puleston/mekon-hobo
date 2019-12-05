package uk.ac.manchester.cs.mekon.appmodeller.model;

import java.net.*;

/**
 * @author Colin Puleston
 */
public class EntityId {

	private URI uri;

	public EntityId(URI uri) {

		this.uri = uri;
	}

	public boolean equals(Object other) {

		return other instanceof EntityId && uri.equals(((EntityId)other).uri);
	}

	public int hashCode() {

		return uri.hashCode();
	}

	public String toString() {

		return getLabel();
	}

	public URI getURI() {

		return uri;
	}

	public String getLabel() {

		return uri.getFragment();
	}
}
