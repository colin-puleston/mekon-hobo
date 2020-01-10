package uk.ac.manchester.cs.goblin.model;

import java.io.*;
import java.net.*;

/**
 * @author Colin Puleston
 */
public class DynamicId {

	static public DynamicId fromName(String name) {

		return new DynamicId(name, nameToLabel(checkNonEmptyName(name)));
	}

	static public DynamicId fromLabel(String label) {

		return new DynamicId(labelToName(checkNonEmptyLabel(label)), label);
	}

	static private String nameToLabel(String name) {

		StringBuilder label = new StringBuilder();

		name = decodeName(name);

		label.append(name.charAt(0));

		for (int i = 1 ; i < name.length() ; i++) {

			char c = name.charAt(i);

			if (Character.isUpperCase(c)) {

				if (nonUpperCase(name, i - 1) || nonUpperCaseOrDigit(name, i + 1)) {

					label.append(' ');

					if (!isUpperCase(name, i + 1)) {

						c = Character.toLowerCase(c);
					}
				}

				label.append(c);
			}
			else if (Character.isDigit(c)) {

				if (nonDigit(name, i - 1)) {

					label.append(' ');
				}

				label.append(c);

				if (nonDigit(name, i + 1) && nonUpperCase(name, i + 1)) {

					label.append(' ');
				}
			}
			else {

				label.append(c);
			}
		}

		return label.toString();
	}

	static private String labelToName(String label) {

		StringBuilder name = new StringBuilder();

		for (String word : label.split(" ")) {

			if (!word.isEmpty()) {

				name.append(Character.toUpperCase(word.charAt(0)));
				name.append(word.substring(1));
			}
		}

		return encodeName(name.toString());
	}

	static private String checkNonEmptyName(String name) {

		return checkNonEmpty(name, "name");
	}

	static private String checkNonEmptyLabel(String label) {

		return checkNonEmpty(label, "label");
	}

	static private String checkNonEmpty(String thing, String thingName) {

		if (thing.isEmpty()) {

			throw new RuntimeException(thingName + " is empty!");
		}

		return thing;
	}

	static private String encodeName(String name) {

		try {

			return URLEncoder.encode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {

			throw new Error(e);
		}
	}

	static private String decodeName(String name) {

		try {

			return URLDecoder.decode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {

			throw new Error(e);
		}
	}

	static private boolean isUpperCase(String name, int i) {

		return i < name.length() && Character.isUpperCase(name.charAt(i));
	}

	static private boolean nonUpperCaseOrDigit(String name, int i) {

		return nonUpperCase(name, i) && nonDigit(name, i);
	}

	static private boolean nonUpperCase(String name, int i) {

		return i < name.length() && !Character.isUpperCase(name.charAt(i));
	}

	static private boolean nonDigit(String name, int i) {

		return i < name.length() && !Character.isDigit(name.charAt(i));
	}

	private String name;
	private String label;

	public DynamicId(String name, String label) {

		this.name = encodeName(checkNonEmptyName(name));
		this.label = checkNonEmptyLabel(label);
	}

	public String toString() {

		return name + "(" + label + ")";
	}

	public String getName() {

		return name;
	}

	public String getLabel() {

		return label;
	}

	public boolean independentNameAndLabel() {

		return !nameToLabel(name).equals(label);
	}

	EntityId toEntityId(String namespace) {

		EntityId entityId = new EntityId(nameToURI(namespace), label);

		entityId.setDynamicId(this);

		return entityId;
	}

	private URI nameToURI(String namespace) {

		try {

			return new URI(namespace + '#' + name);
		}
		catch (URISyntaxException e) {

			throw new Error("Not a valid URI fragment: " + name);
		}
	}
}
