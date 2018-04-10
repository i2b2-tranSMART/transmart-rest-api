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

import grails.rest.Link

/**
 * All implementations of this interface are automatically registered by {@link MarshallersRegistrar} at startup, so
 * they can be used to convert objects to JSON in `foo as JSON` expressions.
 *
 * @param < T >  The type that this class can serialize
 */
interface HalOrJsonSerializationHelper<T> {

	Class<T> getTargetType()

	Collection<Link> getLinks(T object)

	Set<String> getAggregatedLinkRelations()

	Map<String, Object> convertToMap(T object)

	Set<String> getEmbeddedEntities(T object)

	String getCollectionName()
}
