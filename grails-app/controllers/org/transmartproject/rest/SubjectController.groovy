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
import org.transmartproject.core.dataquery.Patient
import org.transmartproject.core.dataquery.clinical.PatientsResource
import org.transmartproject.core.exceptions.NoSuchResourceException
import org.transmartproject.core.ontology.ConceptsResource
import org.transmartproject.core.ontology.OntologyTerm
import org.transmartproject.rest.marshallers.ContainerResponseWrapper
import org.transmartproject.rest.ontology.OntologyTermCategory

class SubjectController {

	static responseFormats = ['json', 'hal']

	ConceptsResource conceptsResourceService
	PatientsResource patientsResourceService
	StudyLoadingService studyLoadingServiceProxy

	/**
	 * GET request on /studies/XXX/subjects/
	 * Returns subjects for certain study, where each subject will be rendered in its short format
	 */
	def index() {
		respond wrapSubjects(studyLoadingServiceProxy.study.patients, selfLinkForStudy())
	}

	/**
	 * GET request on /studies/XXX/subjects/${id}.
	 * Returns the single subject for certain study.
	 *
	 * @param id The is for which to return Data information.
	 */
	def show(Integer id) {
		Patient patient = patientsResourceService.getPatientById(id)

		String studyId = studyLoadingServiceProxy.study.id
		if (patient.trial != studyId) {
			throw new NoSuchResourceException(
					"The patient with id $id does not belong to the study '$studyId'")
		}

		respond patient
	}

	/**
	 * GET request on /studies/XXX/concepts/YYY/subjects
	 *
	 * @return list of subjects for study XXX and Data YYY
	 */
	def indexByConcept(String conceptId) {
		String ontologyTermKey = OntologyTermCategory.keyFromURLPart(conceptId, studyLoadingServiceProxy.study)
		OntologyTerm ontologyTerm = conceptsResourceService.getByKey(ontologyTermKey)

		respond wrapSubjects(ontologyTerm.patients, selfLinkForConcept(ontologyTerm))
	}

	private String selfLinkForStudy() {
		'/studies/' + studyLoadingServiceProxy.studyLowercase + '/subjects'
	}

	private String selfLinkForConcept(OntologyTerm term) {
		'/studies/' + studyLoadingServiceProxy.studyLowercase + '/concepts/' +
				OntologyTermCategory.encodeAsURLPart(term, studyLoadingServiceProxy.study) +
				'/subjects'
	}

	private ContainerResponseWrapper wrapSubjects(source, String selfLink) {
		new ContainerResponseWrapper(
				container: source,
				componentType: Patient,
				links: [new Link(AbstractLinkingRenderer.RELATIONSHIP_SELF, selfLink)])
	}
}
