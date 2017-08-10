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

package uk.ac.manchester.cs.mekon.remote.server.xml;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.remote.util.*;

/**
 * @author Colin Puleston
 */
class ModelActions extends ServerActions<RModelActionType> {

	private CModel model;

	private IInstanceParser assertionParser;
	private IInstanceParser queryParser;

	private class GetFrameHierarchyAction extends Action {

		RModelActionType getActionType() {

			return RModelActionType.GET_FRAME_HIERARCHY;
		}

		void perform(XRequestParser request, XResponseRenderer response) {

			response.setCFrameHierarchyResponse(model.getRootFrame());
		}
	}

	private abstract class InitialiseInstanceAction extends Action {

		void perform(XRequestParser request, XResponseRenderer response) {

			IInstanceParseInput parseInput = request.getInstanceParameterParseInput(0);
			CFrame type = getInstanceParser().parseRootFrameType(parseInput);

			response.setInstanceResponse(type.instantiate());
		}

		abstract IInstanceParser getInstanceParser();
	}

	private class InitialiseAssertionAction extends InitialiseInstanceAction {

		RModelActionType getActionType() {

			return RModelActionType.INITIALISE_ASSERTION;
		}

		IInstanceParser getInstanceParser() {

			return assertionParser;
		}
	}

	private class InitialiseQueryAction extends InitialiseInstanceAction {

		RModelActionType getActionType() {

			return RModelActionType.INITIALISE_QUERY;
		}

		IInstanceParser getInstanceParser() {

			return queryParser;
		}
	}

	private abstract class UpdateInstanceAction extends Action {

		void perform(XRequestParser request, XResponseRenderer response) {

			IInstanceParseInput parseInput = request.getInstanceParameterParseInput(0);
			IFrame inAndOut = parseParameter(parseInput);
			IInstanceRenderInput renderInput = new IInstanceRenderInput(inAndOut);

			renderInput.setFrameXDocIds(parseInput.getFrameXDocIds());
			response.setInstanceResponse(renderInput);
		}

		abstract IFrame parseParameter(IInstanceParseInput input);
	}

	private class UpdateAssertionAction extends UpdateInstanceAction {

		RModelActionType getActionType() {

			return RModelActionType.UPDATE_ASSERTION;
		}

		IFrame parseParameter(IInstanceParseInput input) {

			return assertionParser.parse(input);
		}
	}

	private class UpdateQueryAction extends UpdateInstanceAction {

		RModelActionType getActionType() {

			return RModelActionType.UPDATE_QUERY;
		}

		IFrame parseParameter(IInstanceParseInput input) {

			return queryParser.parse(input);
		}
	}

	ModelActions(CModel model) {

		this.model = model;

		assertionParser = new IInstanceParser(model, IFrameFunction.ASSERTION);
		queryParser = new IInstanceParser(model, IFrameFunction.QUERY);

		new GetFrameHierarchyAction();
		new InitialiseAssertionAction();
		new InitialiseQueryAction();
		new UpdateAssertionAction();
		new UpdateQueryAction();
	}

	RActionCategory getActionCategory() {

		return RActionCategory.MODEL;
	}

	RModelActionType getRequestActionType(XRequestParser request) {

		return request.getModelActionType();
	}
}
