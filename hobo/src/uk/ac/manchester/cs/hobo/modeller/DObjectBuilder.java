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

package uk.ac.manchester.cs.hobo.modeller;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.hobo.model.*;

/**
 * Responsible for constructing and initialising the fields
 * associated with a specific object in the Object Model (OM) (i.e
 * an instantiation of some extension of {@link DObjectShell}).
 * <p>
 * Constructs the associated fields and, where required, the
 * field-viewers that enable the OM to provide the client with
 * un-editable views of specific fields where appropriate.
 * <p>
 * The final field-configuration operations, involving the
 * setting-up of appropriate slot-bindings, are not performed until
 * after the object has been constructed, since certain attributes
 * involved in this process are by default derived from the Java
 * variables to which the fields are assigned after construction.
 * <p>
 * Hence, any required field-initialisations (setting of initial
 * values or addition of listeners) must be performed via the
 * {@link DObjectInitialiser#initialise} method on an appropriate
 * {@link DObjectInitialiser} object, which can be specified via
 * the {@link #addInitialiser} method.
 * <p>
 * In order for a Java variable to provide the default
 * attribute-values for a specific field, the variable in question
 * must be an instance-variable on the OM object itself, must be
 * both "public" and "final", and the value must be either the field
 * itself or a viewer for the field.
 * <p>
 * For each field, unless the "container-class", "field-name" and
 * "editability" attribute-values have been explicitly specified
 * (via the appropriate methods) then they will be derived
 * automatically from the relevant Java variable, assuming that it
 * exists. The field-name will be taken directly from the variable
 * name, and the editability status will be derived from the variable
 * type ({@link DField} implies {@link CEditability#DEFAULT},
 * {@link DFieldViewer} implies {@link CEditability#QUERY_ONLY}).
 * The "slot-label" attribute (for which values can also be explicitly
 * provided) will by default be set equal to the field-name.
 *
 * @author Colin Puleston
 */
public interface DObjectBuilder {

	/**
	 * Creates a single-valued OM field with concept-level-frame
	 * values of any type.
	 *
	 * @return Created field
	 */
	public DCell<DConcept<DObject>> addConceptCell();

	/**
	 * Creates a single-valued OM field with concept-level-frame
	 * values, where valid values are defined via a specific
	 * root-frame.
	 *
	 * @param <D> Generic version of value-class
	 * @param valueClass OM class that is bound to root-frame
	 * defining valid field values
	 * @return Created field
	 */
	public <D extends DObject>DCell<DConcept<D>> addConceptCell(Class<D> valueClass);

	/**
	 * Creates a multi-valued OM field with concept-level-frame
	 * values, with no constraints on valid values.
	 *
	 * @return Created field
	 */
	public DArray<DConcept<DObject>> addConceptArray();

	/**
	 * Creates a multi-valued OM field with concept-level-frame
	 * values, where valid values are defined via a specific
	 * root-frame.
	 *
	 * @param <D> Generic version of value-class
	 * @param valueClass OM class that is bound to root-frame
	 * defining valid field values
	 * @return Created field
	 */
	public <D extends DObject>DArray<DConcept<D>> addConceptArray(Class<D> valueClass);

	/**
	 * Creates a single-valued OM field with OM-object values of the
	 * specified type.
	 *
	 * @param <D> Generic version of value-class
	 * @param valueClass OM class of field values
	 * @return Created field
	 */

	public <D extends DObject>DCell<D> addObjectCell(Class<D> valueClass);

	/**
	 * Creates a multi-valued OM field with OM-object values of the
	 * specified type.
	 *
	 * @param <D> Generic version of value-class
	 * @param valueClass OM class of field values
	 * @return Created field
	 */
	public <D extends DObject>DArray<D> addObjectArray(Class<D> valueClass);

	/**
	 * Creates a single-valued OM field with integer-type values,
	 * with no additional value-constraints.
	 *
	 * @return Created field
	 */
	public DCell<Integer> addIntegerCell();

	/**
	 * Creates a single-valued OM field with long-type values,
	 * with no additional value-constraints.
	 *
	 * @return Created field
	 */
	public DCell<Long> addLongCell();

	/**
	 * Creates a single-valued OM field with float-type values,
	 * with no additional value-constraints.
	 *
	 * @return Created field
	 */
	public DCell<Float> addFloatCell();

	/**
	 * Creates a single-valued OM field with double-type values,
	 * with no additional value-constraints.
	 *
	 * @return Created field
	 */
	public DCell<Double> addDoubleCell();

	/**
	 * Creates a single-valued OM field with number-type values,
	 * with specified value-constraints.
	 *
	 * @param <N> Value-type of number-cell
	 * @param range Specific value-constraints
	 * @return Created field
	 */
	public <N extends Number>DCell<N> addNumberCell(DNumberRange<N> range);

	/**
	 * Enables the explicit specification of the "container-class"
	 * attribute for a particular field that has been constructed
	 * by this fields-factory.
	 *
	 * @param field Field whose attribute is to be set
	 * @param containerClass Value for attribute
	 * @throws KAccessException if the field was not constructed
	 * by this fields-factory
	 */
	public void setContainerClass(
					DField<?> field,
					Class<? extends DObject> containerClass);

	/**
	 * Enables the explicit specification of the "field-name"
	 * attribute for a particular field that has been constructed
	 * by this fields-factory.
	 *
	 * @param field Field whose attribute is to be set
	 * @param fieldName Value for attribute
	 * @throws KAccessException if the field was not constructed
	 * by this fields-factory
	 */
	public void setFieldName(DField<?> field, String fieldName);

	/**
	 * Enables the explicit specification of the "slot-label"
	 * attribute for a particular field that has been constructed
	 * by this fields-factory.
	 *
	 * @param field Field whose attribute is to be set
	 * @param slotLabel Value for attribute
	 * @throws KAccessException if the field was not constructed
	 * by this fields-factory
	 */
	public void setSlotLabel(DField<?> field, String slotLabel);

	/**
	 * Enables the explicit specification of the "editability"
	 * attribute for the slot associated with a particular field
	 * that has been constructed by this fields-factory.
	 *
	 * @param field Field whose attribute is to be set
	 * @param editability Value for attribute
	 * @throws KAccessException if the field was not constructed
	 * by this fields-factory
	 */
	public void setEditability(DField<?> field, CEditability editability);

	/**
	 * Enables the explicit specification of the "unique-types"
	 * attribute for a particular array that has been constructed
	 * by this fields-factory. If this attribute is set then the
	 * array cannot have any values with value-types that subsume
	 * the value-types of any of it's other values. The default
	 * value for is false.
	 *
	 * @param array Array whose attribute is to be set
	 * @param uniqueTypes Value for attribute
	 * @throws KAccessException if the array was not constructed
	 * by this fields-factory
	 */
	public void setUniqueTypes(DArray<?> array, boolean uniqueTypes);

	/**
	 * Registers an initialiser to perform any required
	 * post-construction field-initialisations.
	 *
	 * @param initialiser Initialiser to register
	 */
	public void addInitialiser(DObjectInitialiser initialiser);

	/**
	 * Provides the model with which the object is associated.
	 *
	 * @return Model with which object is associated
	 */
	public DModel getModel();

	/**
	 * Provides the editor object for the model-instantiation with
	 * which the object is associated, which provides the OM classes
	 * with access to mechanisms for performing internal updates
	 * that cannot be performed by the client code.
	 *
	 * @return Editor for model-instantiation with which object is
	 * associated
	 */
	public DEditor getEditor();

	/**
	 * Provides the instance-level frame to which the object is bound.
	 *
	 * @return Frame to which object is bound
	 */
	public IFrame getFrame();

	/**
	 * Provides a viewer for the specified single-valued OM field.
	 *
	 * @param <V> Cell value-type
	 * @param cell Cell to be viewed
	 * @return Viewer for cell
	 */
	public <V>DCellViewer<V> getViewer(DCell<V> cell);

	/**
	 * Provides a viewer for the specified multi-valued OM field.
	 *
	 * @param <V> Array value-type
	 * @param array Array to be viewed
	 * @return Viewer for array
	 */
	public <V>DArrayViewer<V> getViewer(DArray<V> array);
}
