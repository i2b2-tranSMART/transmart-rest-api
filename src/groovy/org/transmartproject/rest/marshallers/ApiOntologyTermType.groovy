package org.transmartproject.rest.marshallers

import groovy.transform.CompileStatic

/**
 * Values for the type key on the onotlogy term response.
 */
@CompileStatic
enum ApiOntologyTermType {
	STUDY,
	HIGH_DIMENSIONAL,
	NUMERIC,
	CATEGORICAL_OPTION,
	FOLDER,      // reserved
	CATEGORICAL, // reserved
	UNKNOWN,
}
