/*
 * Copyright 2014 Janssen Research & Development, LLC.
 *
 * This file is part of REST API: transMART's plugin exposing tranSMART's
 * data via an HTTP-accessible RESTful API.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version, along with the following terms:
 *
 *   1. You may convey a work based on this program in accordance with
 *      section 5, provided that you retain the above notices.
 *   2. You may convey verbatim copies of this program code as you receive
 *      it, in any medium, provided that you retain the above notices.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.transmartproject.rest.marshallers

import grails.converters.JSON
import grails.rest.Link
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller

import static grails.rest.render.util.AbstractLinkingRenderer.DEPRECATED_ATTRIBUTE
import static grails.rest.render.util.AbstractLinkingRenderer.HREFLANG_ATTRIBUTE
import static grails.rest.render.util.AbstractLinkingRenderer.HREF_ATTRIBUTE
import static grails.rest.render.util.AbstractLinkingRenderer.TEMPLATED_ATTRIBUTE
import static grails.rest.render.util.AbstractLinkingRenderer.TITLE_ATTRIBUTE
import static grails.rest.render.util.AbstractLinkingRenderer.TYPE_ATTRIBUTE

class CoreApiObjectMarshaller implements ObjectMarshaller<JSON> {

	public static final String LINKS_ATTRIBUTE = '_links'
	public static final String EMBEDDED_ATTRIBUTE = '_embedded'

	HalOrJsonSerializationHelper serializationHelper

	Class<?> getTargetType() {
		serializationHelper.targetType
	}

	boolean supports(object) {
		serializationHelper.targetType.isAssignableFrom object.getClass()
	}

	void marshalObject(object, JSON json) throws ConverterException {
		Map<String, Object> mapRepresentation = serializationHelper.convertToMap(object)

		if (json.contentType.contains('hal')) {
			mapRepresentation[LINKS_ATTRIBUTE] = getLinks(object)
			segregateEmbedded mapRepresentation, object
		}

		json.value mapRepresentation
	}

	/**
	 * @return map of relationship to link value. Value is either a Link (simple) or a List<Link> (aggregated)
	 */
	private Map<String, Object> getLinks(Object object) {

		Map<String, Object> result = [:]
		Map<String, List<Link>> grouped = serializationHelper.getLinks(object).groupBy { it.rel }

		grouped.each { String key, List<Link> list ->
			if (serializationHelper.aggregatedLinkRelations.contains(key)) {
				result[key] = list.collect { Link link -> convertLink link }
			}
			else {
				//only the first element will be picked. Its not supposed to have more than one anyway
				result[key] = convertLink(list[0])
			}
		}

		result
	}

	private Map<String, Object> convertLink(Link link) {
		Map<String, Object> res = [(HREF_ATTRIBUTE): link.href]
		if (link.hreflang) {
			res[HREFLANG_ATTRIBUTE] = link.hreflang
		}
		if (link.title) {
			res[TITLE_ATTRIBUTE] = link.title
		}
		if (link.contentType) {
			res[TYPE_ATTRIBUTE] = link.contentType
		}
		if (link.templated) {
			res[TEMPLATED_ATTRIBUTE] = true
		}
		if (link.deprecated) {
			res[DEPRECATED_ATTRIBUTE] = true
		}
		res
	}

	private void segregateEmbedded(Map<String, Object> map, originalObject) {
		def embedded = serializationHelper.
				getEmbeddedEntities(originalObject).
				collectEntries { String it ->
					def association = map.remove(it)
					[it, association]
				}

		if (embedded) {
			map[EMBEDDED_ATTRIBUTE] = embedded
		}
	}
}
