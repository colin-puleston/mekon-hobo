package uk.ac.manchester.cs.mekon.appmodeller.model;

/**
 * @author Colin Puleston
 */
public class Link {

	private EntityId propertyId;
	private Concept value;

	public Link(EntityId propertyId, Concept value) {

		this.propertyId = propertyId;
		this.value = value;
	}

	public EntityId getPropertyId() {

		return propertyId;
	}

	public Concept getValue() {

		return value;
	}

	Link deriveSubLink(Concept subValue) {

		checkSubValue(subValue);

		return new Link(propertyId, subValue);
	}

	void checkSubValue(Concept subValue) {

		if (!subValue.descendantOf(value)) {

			throw new RuntimeException(
						"Value-concept \"" + subValue + "\""
						+ " not a descendant-concept of \"" + value + "\"");
		}
	}

	boolean subLinkOf(Link test) {

		return samePropertyIdAs(test) && value.descendantOf(test.value);
	}

	boolean samePropertyIdAs(Link test) {

		return test.propertyId.equals(propertyId);
	}
}
