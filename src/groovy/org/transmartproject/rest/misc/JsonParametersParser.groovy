package org.transmartproject.rest.misc

import grails.converters.JSON
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import org.transmartproject.core.exceptions.InvalidArgumentsException

class JsonParametersParser {

	static Map<String, List> parseConstraints(String paramValue) {
		if (!paramValue) {
			return [:]
		}

		JSONElement constraintsElement
		try {
			constraintsElement = JSON.parse(paramValue)
		}
		catch (ConverterException e) {
			throw new InvalidArgumentsException('Failed parsing as JSON: ' + paramValue, e)
		}
		catch (StackOverflowError e) { // *sigh*
			throw new InvalidArgumentsException('Failed parsing as JSON: ' + paramValue, e)
		}

		if (!constraintsElement instanceof JSONObject) {
			throw new InvalidArgumentsException('Expected constraints to be JSON map')
		}

		// normalize [constraint_name: [ param1: foo ]] to
		//           [constraint_name: [[ param1: foo ]]]
		((JSONObject) constraintsElement).collectEntries { String constraintName, value ->
			if (!(value instanceof Map || value instanceof List)) {
				throw new InvalidArgumentsException(
						"Invalid parameters for contraint $constraintName: $value (should be a list or a map)")
			}
			else if (value instanceof Map) {
				[constraintName, [value]]
			}
			else { // List
				[constraintName, value] // entry unchanged
			}
		}
	}
}
