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
import groovy.transform.Canonical
import groovy.transform.CompileStatic

// wrapper for collections of core-api helper so we can target a marshaller to them
@CompileStatic
class ContainerResponseWrapper {
	private ContainerResponseWrapper() {}

	List<entry> containers
	List<Link> links

	ContainerResponseWrapper(Map args) {
		def container = args.container
		Class componentType = args.componentType
		String key = args.key
		links = (args.links ?: []) as List<Link>

		containers = ((args.containers ?: []) as List<entry>) + [new entry(key, componentType, container)]
	}

	static ContainerResponseWrapper asMap(Map<String, List> args, List<Link> links) {
		ContainerResponseWrapper wrapper = new ContainerResponseWrapper()
		wrapper.containers = args.collect { Map.Entry<String, List> it ->
				new entry(key: it.key, componentType: (Class) it.value[0], container: it.value[1])
		}
		wrapper.links = links
		return wrapper
	}

	@Canonical
	@CompileStatic
	static class entry {
		String key = null
		Class componentType
		def container  // in the general sense. Can be Iterator
	}
}
