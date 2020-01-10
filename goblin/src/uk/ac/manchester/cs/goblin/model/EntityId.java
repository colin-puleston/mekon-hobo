package uk.ac.manchester.cs.goblin.model;

import java.net.*;

/**
 * @author Colin Puleston
 */
public class EntityId {

	static private final char[] URI_FINAL_SEPARATOR_CHARS = new char[]{'#', '/', ':'};

	static private String getDefaultLabel(URI uri) {

		String u = uri.toString();

		for (char c : URI_FINAL_SEPARATOR_CHARS) {

			int i = u.lastIndexOf(c);

			if (i != -1 && i != u.length() - 1) {

				return u.substring(i + 1);
			}
		}

		return u;
	}

	private URI uri;
	private String label;

	private DynamicId dynamicId = null;

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

	public DynamicId toDynamicId() {

		return dynamicId;
	}

	EntityId(URI uri) {

		this(uri, getDefaultLabel(uri));
	}

	EntityId(URI uri, String label) {

		this.uri = uri;
		this.label = label;
	}

	void setDynamicId(DynamicId dynamicId) {

		this.dynamicId = dynamicId;
	}
}
