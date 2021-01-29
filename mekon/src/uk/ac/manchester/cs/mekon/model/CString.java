/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.mekon.model;

import java.net.*;

import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * Represents a string value-type, which optionally may come with
 * specific value constraints.
 *
 * @author Colin Puleston
 */
public class CString extends CDataValue<IString> {

	/**
	 * Singleton object defining string values with format
	 * {@link CStringFormat#FREE}
	 */
	static public final CString FREE = new FreeFormatConfig().string;

	/**
	 * Singleton object defining string values with format
	 * {@link CStringFormat#URI_VALUE}
	 */
	static public final CString URI_VALUE = new URIFormatConfig().string;

	/**
	 * Singleton object defining string values with format
	 * {@link CStringFormat#URL_VALUE}
	 */
	static public final CString URL_VALUE = new URLFormatConfig().string;

	static private boolean validURIText(String text) {

		try {

			new URI(text);

			return true;
		}
		catch (URISyntaxException e) {

			return false;
		}
	}

	static private boolean validURLText(String text) {

		try {

			new URL(text);

			return validURIText(text);
		}
		catch (MalformedURLException e) {

			return false;
		}
	}

	static private abstract class StandardFormatConfig implements CStringConfig {

		final CString string = new CString(getFormat(), this);

		public CString combineWith(CString other) {

			if (other.format == CStringFormat.CUSTOM) {

				return other.combineWith(string);
			}

			return subsumes(other) ? other : string;
		}

		abstract CStringFormat getFormat();

		abstract boolean subsumes(CString other);
	}

	static private class FreeFormatConfig extends StandardFormatConfig {

		public String describeValidityCriteria() {

			return "Any string value";
		}

		public boolean validValueText(String text) {

			return true;
		}

		CStringFormat getFormat() {

			return CStringFormat.FREE;
		}

		boolean subsumes(CString other) {

			return true;
		}
	}

	static private class URIFormatConfig extends StandardFormatConfig {

		public String describeValidityCriteria() {

			return "URI value";
		}

		public boolean validValueText(String text) {

			return validURIText(text);
		}

		CStringFormat getFormat() {

			return CStringFormat.URI_VALUE;
		}

		boolean subsumes(CString other) {

			return other.config instanceof URLFormatConfig;
		}
	}

	static private class URLFormatConfig extends StandardFormatConfig {

		public String describeValidityCriteria() {

			return "URL value";
		}

		public boolean validValueText(String text) {

			return validURLText(text);
		}

		CStringFormat getFormat() {

			return CStringFormat.URL_VALUE;
		}

		boolean subsumes(CString other) {

			return false;
		}
	}

	private CStringFormat format;
	private CStringConfig config;

	/**
	 * {@inheritDoc}
	 */
	public Class<IString> getValueType() {

		return IString.class;
	}

	/**
	 * Stipulates that this string value-type defines specific
	 * constraints on the string values that it defines if and only
	 * if it does not have format {@link CStringFormat#FREE} (see
	 * {@link #getFormat}.
	 *
	 * @return True if constraints defined on string values
	 */
	public boolean constrained() {

		return format != CStringFormat.FREE;
	}

	/**
	 * Provides the unconstrained version of this string value-type,
	 * which will always be the singleton value {@link #FREE}.
	 *
	 * @return Unconstrained version of this string value-type
	 */
	public CString toUnconstrained() {

		return FREE;
	}

	/**
	 * Stipulates that the string value-type never defines a default
	 * string value.
	 *
	 * @return False always.
	 */
	public boolean hasDefaultValue() {

		return false;
	}

	/**
	 * Stipulates that the string value-type never defines only a
	 * single possible value.
	 *
	 * @return False always.
	 */
	public boolean onePossibleValue() {

		return false;
	}

	/**
	 * Tests whether this value-type-entity subsumes another
	 * specified value-type-entity, which will be the case if and
	 * only if this string value-type has format
	 * {@link CStringFormat#FREE} and the other value-type-entity
	 * is a <code>CString</code>, which can have any format.
	 *
	 * @param other Other value-type-entity to test for subsumption
	 * @return True if this value-type-entity subsumes other
	 * value-type-entity
	 */
	public boolean subsumes(CValue<?> other) {

		return other instanceof CString && format == CStringFormat.FREE;
	}

	/**
	 * Provides the required format for the content of the string
	 * values that the string value-type defines.
	 *
	 * @return Required format for defined string values
	 */
	public CStringFormat getFormat() {

		return format;
	}

	/**
	 * Provides description of the criteria by which values are
	 * deemed valid for the string value-type.
	 *
	 * @return Description of relevant validity criteria
	 */
	public String describeValidityCriteria() {

		return config.describeValidityCriteria();
	}

	/**
	 * Test whether specified text represents a valid value for
	 * the string value-type.
	 *
	 * @param text Text to test for validity
	 * @return True if supplied text represents valid value
	 */
	public boolean validValueText(String text) {

		return config.validValueText(text);
	}

	/**
	 * Combines this format with another specified format, if
	 * possible. Where both formats are of the provided singleton
	 * format-values ({@link #FREE}, {@link #URI_VALUE} and
	 * {@link #URL_VALUE}) then the later-defined value will
	 * be returned, since the values are ordered so that later
	 * values are restrictions of earlier values.
	 *
	 * @param other Format with which to combine this one
	 * @return Combined format, or null if formats cannot be
	 * combined
	 */
	public CString combineWith(CString other) {

		return config.combineWith(other);
	}

	/**
	 * Creates instantiation of the string value-type.
	 *
	 * @param text Text representing required value
	 * @return Instantiation containing supplied text
	 * @throws KAccessException if supplied text does not represent
	 * valid value for string value-type
	 */
	public IString instantiate(String text) {

		if (validValueText(text)) {

			return new IString(text);
		}

		throw new KAccessException("Invalid value text: " + text);
	}

	CString(CStringFormat format, CStringConfig config) {

		this.format = format;
		this.config = config;
	}

	CValue<?> update(CValue<?> other) {

		return other instanceof CString ? combineWith((CString)other) : null;
	}

	void acceptVisitor(CValueVisitor visitor) throws Exception {

		visitor.visit(this);
	}

	IString getDefaultValueOrNull() {

		return null;
	}

	boolean validTypeValue(IString value) {

		return validValueText(value.get());
	}

	String getDataValueDescription() {

		return String.class.getSimpleName();
	}
}
