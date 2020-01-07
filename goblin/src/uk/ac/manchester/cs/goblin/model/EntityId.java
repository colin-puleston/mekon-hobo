package uk.ac.manchester.cs.goblin.model;

import java.net.*;

/**
 * @author Colin Puleston
 */
public class EntityId {

	static private String getDefaultLabel(URI uri) {

		String fragment = uri.getFragment();

		return fragment != null ? fragment : uri.toString();
	}

	private URI uri;
	private String label;

	public EntityId(URI uri) {

		this(uri, getDefaultLabel(uri));
	}

	public EntityId(URI uri, String label) {

		this.uri = uri;
		this.label = label;
	}

	public boolean equals(Object other) {

		return other instanceof EntityId && uri.equals(((EntityId)other).uri);
	}

	public int hashCode() {

		return uri.hashCode();
	}

	public String toString() {

		return label;
	}

	public URI getURI() {

		return uri;
	}

	public String getLabel() {

		return label;
	}

	public EntityIdSpec toSpec() {

		return new EntityIdSpec(deriveName(), label);
	}

	public String deriveName() {

		String name = uri.getFragment();

		if (name != null) {

			return name;
		}

		throw new RuntimeException("Cannot derive name from URI with no fragment: " + uri);
	}
}
