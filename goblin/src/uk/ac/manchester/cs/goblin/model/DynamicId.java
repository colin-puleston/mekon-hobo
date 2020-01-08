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

		name = decodeName(name);

		if (name.length() < 3) {

			return name;
		}

		StringBuilder label = new StringBuilder();

		label.append(Character.toUpperCase(name.charAt(0)));

		for (int i = 1 ; i < name.length() ; i++) {

			char p = name.charAt(i - 1);
			char c = name.charAt(i);

			if (i == name.length() - 1) {

				if (upperCaseOrDigit(c) && !upperCaseOrDigit(p)) {

					label.append(' ');
				}

				label.append(c);

				break;
			}

			char n = name.charAt(i + 1);

			if (upperCaseOrDigit(c)) {

				if (upperCaseOrDigit(p)) {

					label.append(c);

					if (!upperCaseOrDigit(n)) {

						label.append(' ');
					}
				}
				else {

					label.append(' ');

					if (!upperCaseOrDigit(n)) {

						c = Character.toLowerCase(c);
					}

					label.append(c);
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
		boolean capitaliseNext = true;

		for (String word : label.split(" ")) {

			if (!word.isEmpty()) {

				if (capitaliseNext) {

					name.append(Character.toUpperCase(word.charAt(0)));
					name.append(word.substring(1));
				}
				else {

					name.append(word);
				}

				capitaliseNext = !upperCaseOrDigit(word.charAt(word.length() - 1));
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

	static private boolean upperCaseOrDigit(char c) {

		return Character.isUpperCase(c) || Character.isDigit(c);
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
