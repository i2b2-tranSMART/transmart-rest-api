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

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder
import org.transmartproject.core.exceptions.AccessDeniedException
import org.transmartproject.core.exceptions.InvalidArgumentsException
import org.transmartproject.core.ontology.OntologyTerm
import org.transmartproject.core.ontology.Study
import org.transmartproject.db.ontology.StudiesResourceService
import org.transmartproject.rest.misc.CurrentUser
import org.transmartproject.rest.ontology.OntologyTermCategory

import static org.transmartproject.core.users.ProtectedOperation.WellKnownOperations.API_READ

@CompileStatic
@Slf4j('logger')
class StudyLoadingService {

	static scope = 'request'
	static transactional = false

	public static final String STUDY_ID_PARAM = 'studyId'

	CurrentUser currentUser
	StudiesResourceService studiesResourceService

	private Study cachedStudy

	Study getStudy() {
		if (!cachedStudy) {
			cachedStudy = fetchStudy()
		}

		cachedStudy
	}

	Study fetchStudy() {
		GrailsWebRequest webRequest = (GrailsWebRequest) RequestContextHolder.currentRequestAttributes()
		String studyId = webRequest.params[STUDY_ID_PARAM]

		if (!studyId) {
			throw new InvalidArgumentsException('Could not find a study id')
		}

		Study study = studiesResourceService.getStudyById(studyId)
		if (!checkAccess(study)) {
			throw new AccessDeniedException("Denied access to study ${study.id}")
		}

		study
	}

	private boolean checkAccess(Study study) {
		boolean canPerform = currentUser.canPerform(API_READ, study)
		if (!canPerform) {
			logger.warn 'User {} denied access to study {}', currentUser.username, study.id
		}

		canPerform
	}

	String getStudyLowercase() {
		study.id.toLowerCase Locale.ENGLISH
	}

	/**
	 * @param term ontology term
	 * @return url for given ontology term and request study or concept study
	 */
	String getOntologyTermUrl(OntologyTerm term) {
		String studyId
		String pathPart

		try {
			studyId = study.id
			pathPart = OntologyTermCategory.encodeAsURLPart(term, study)
		}
		catch (InvalidArgumentsException ignored) {
			//studyId not in params: either /studies or a study controller
			studyId = term.study.id
			if (term.level == 1) {
				//we are handling a study, which is mapped to $id (can we rename the param to $studyId for consistency?)
				pathPart = 'ROOT'
			}
			else {
				pathPart = OntologyTermCategory.encodeAsURLPart(term, term.study)
			}
		}

		studyId = URLEncoder.encode(studyId.toLowerCase(Locale.ENGLISH), 'UTF-8')

		"/studies/$studyId/concepts/$pathPart"
	}
}
