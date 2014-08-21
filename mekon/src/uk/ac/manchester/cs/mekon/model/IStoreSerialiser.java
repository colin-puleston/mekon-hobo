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

/**
 * @author Colin Puleston
 */
interface IStoreSerialiser {

	static final String DEFAULT_FILE_NAME = "mekon-istore.xml";

	static final String ROOT_ID = "Store";
	static final String INSTANCE_ID = "Instance";
	static final String FRAME_ID = "Value";
	static final String SLOT_ID = "Slot";

	static final String IDENTITY_ATTR = "id";
	static final String LABEL_ATTR = "label";
	static final String VALUE_TYPE_ATTR = "valueType";
	static final String NUMBER_TYPE_ATTR = "numberType";
	static final String NUMBER_VALUE_ATTR = "numberValue";
}

/*
	<Store>
		<Instance
			id="http://mekon/demo.owl#Bob-Bell">
			<Value
				id="http://mekon/demo.owl#ContractorProfile">
				<Slot
					id="http://mekon/demo.owl#salary"
					valueType="INumber"
					numberType="Float"
					numberValue="3.1"/>
				<Slot
					id="http://mekon/demo.owl#jobs"
					valueType="IFrame">
					<Value
						id="http://mekon/demo.owl#Job">
						<Slot
							id="http://mekon/demo.owl#location">
							<Value
								id="http://mekon/demo.owl#North"/>
						</Slot>
					</Value>
				</Slot>
				<Slot
					id="http://mekon/demo.owl#jobs"
					valueType="CFrame">
					<Value
						id="http://mekon/demo.owl#Job">
					</Value>
				</Slot>
			</Value>
		</Instance>
	</Store>

*/