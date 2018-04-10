package org.transmartproject.rest.marshallers

import grails.rest.Link
import groovy.transform.CompileStatic

/**
 * Abstract impl of HalOrJsonSerializationHelper, just to implement common defaults and absorb future interface changes
 */
@CompileStatic
abstract class AbstractHalOrJsonSerializationHelper<T> implements HalOrJsonSerializationHelper<T> {

	Collection<Link> getLinks(T object) {
		[]
	}

	Set<String> getEmbeddedEntities(T object) {
		[] as Set
	}

	Set<String> getAggregatedLinkRelations() {
		[] as Set
	}

	protected String lowerCaseAncodeAsUrl(String s) {
		URLEncoder.encode s.toLowerCase(Locale.ENGLISH), 'UTF-8'
	}
}
