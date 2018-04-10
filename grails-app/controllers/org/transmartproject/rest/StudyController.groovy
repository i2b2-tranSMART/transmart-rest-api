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

package org.transmartproject.rest

import grails.rest.Link
import grails.rest.render.util.AbstractLinkingRenderer
import org.transmartproject.core.ontology.StudiesResource
import org.transmartproject.core.ontology.Study
import org.transmartproject.rest.marshallers.ContainerResponseWrapper

import javax.annotation.Resource

class StudyController {

	static responseFormats = ['json', 'hal']

	@Resource
	StudiesResource studiesResourceService

	/**
	 * GET request on /studies/
	 * Return studies where each study will be rendered in its short format
	 */
	def index() {
		respond wrapStudies(studiesResourceService.studySet)
	}

	/**
	 * GET request on /studies/${id}.
	 * Returns the single study by name.
	 *
	 * @param name the name of the study
	 */
	def show(String id) {
		respond studiesResourceService.getStudyById(id)
	}

	private wrapStudies(source) {
		new ContainerResponseWrapper(
				container: source,
				componentType: Study,
				links: [new Link(AbstractLinkingRenderer.RELATIONSHIP_SELF, '/studies')])
	}
}
