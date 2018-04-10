/*
 * Copyright (c) 2016 The Hyve B.V.
 *
 * This file is part of REST API: transMART's plugin exposing tranSMART's
 * data via an HTTP-accessible RESTful API.
 *
 * This code is licensed under the GNU General Public License,
 * version 3, or (at your option) any later version.
 */
package org.transmartproject.rest.marshallers

import grails.rest.Link
import groovy.transform.CompileStatic

@CompileStatic
class GenericWrapperSerializationHelper extends AbstractHalOrJsonSerializationHelper<GenericWrapper> {

	final Class targetType = GenericWrapper

	String getCollectionName() {
		throw new UnsupportedOperationException('No collection name for GenericWrapper')
	}

	Collection<Link> getLinks(GenericWrapper bm) {
		bm.links
	}

	Map<String, Object> convertToMap(GenericWrapper bm) {
		bm.values
	}

	Set<String> getEmbeddedEntities(GenericWrapper bm) {
		bm.embeddedEntities
	}
}
