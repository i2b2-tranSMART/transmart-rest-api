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
import org.transmartproject.core.ontology.ConceptsResource
import org.transmartproject.core.ontology.OntologyTerm
import org.transmartproject.rest.marshallers.ContainerResponseWrapper
import org.transmartproject.rest.marshallers.OntologyTermWrapper
import org.transmartproject.rest.ontology.OntologyTermCategory

class ConceptController {

	static responseFormats = ['json', 'hal']

	ConceptsResource conceptsResourceService
	StudyLoadingService studyLoadingServiceProxy

	/**
	 * GET request on /studies/XXX/concepts/
	 * Returns concepts where each is rendered in its short format
	 */
	def index() {
		List<OntologyTerm> concepts = studyLoadingServiceProxy.study.ontologyTerm.allDescendants
		List<OntologyTermWrapper> conceptWrappers = concepts.collect { new OntologyTermWrapper(it, false) }
		respond wrapConcepts(conceptWrappers)
	}

	/**
	 * GET request on /studies/XXX/concepts/${id}.
	 * Returns the single requested entity.
	 *
	 * @param id The id for which to return study information.
	 */
	def show(String id) {
		String key = OntologyTermCategory.keyFromURLPart(id, studyLoadingServiceProxy.study)
		respond new OntologyTermWrapper(
				conceptsResourceService.getByKey(key),
				id == OntologyTermCategory.ROOT_CONCEPT_PATH)
	}

	/**
	 * @return CollectionResponseWrapper so we can provide a proper HAL response
	 */
	private ContainerResponseWrapper wrapConcepts(List<OntologyTermWrapper> source) {
		new ContainerResponseWrapper(
				container: source,
				componentType: OntologyTermWrapper,
				links: [new Link(
						AbstractLinkingRenderer.RELATIONSHIP_SELF,
						'/studies/' + studyLoadingServiceProxy.studyLowercase + '/concepts')]
		)
	}
}
