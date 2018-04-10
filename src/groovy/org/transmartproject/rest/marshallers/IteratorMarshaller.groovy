package org.transmartproject.rest.marshallers

import grails.converters.JSON
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import org.codehaus.groovy.grails.web.converters.marshaller.json.CollectionMarshaller
import org.codehaus.groovy.grails.web.json.JSONWriter
import org.springframework.stereotype.Component

/**
 * Variant of {@link CollectionMarshaller} that works with {@link Iterator}s
 * instead.
 */
@CompileStatic
@Component
class IteratorMarshaller implements ObjectMarshaller<JSON> {

	boolean supports(object) {
		object instanceof Iterator
	}

	void marshalObject(object, JSON converter) throws ConverterException {
		JSONWriter writer = (JSONWriter) converter.writer
		writer.array()
		((Iterator) object).each {
			converter.convertAnother it
		}
		writer.endArray()
	}
}
