/*
 * Copyright 2014 Janssen Research & Development, LLC.
 * Copyright 2015-2016 The Hyve B.V.
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
import org.transmartproject.core.dataquery.assay.Assay
import org.transmartproject.core.dataquery.highdim.HighDimensionDataTypeResource
import org.transmartproject.core.ontology.ConceptsResource
import org.transmartproject.core.ontology.OntologyTerm
import org.transmartproject.rest.marshallers.ContainerResponseWrapper
import org.transmartproject.rest.marshallers.HighDimSummary
import org.transmartproject.rest.marshallers.HighDimSummarySerializationHelper
import org.transmartproject.rest.marshallers.OntologyTermWrapper
import org.transmartproject.rest.misc.JsonParametersParser
import org.transmartproject.rest.misc.LazyOutputStreamDecorator
import org.transmartproject.rest.ontology.OntologyTermCategory

class HighDimController {

	static responseFormats = ['json', 'hal']

	ConceptsResource conceptsResourceService
	HighDimDataService highDimDataService
	StudyLoadingService studyLoadingServiceProxy

	def index(String dataType, String conceptId) {
		if (dataType) {
			// backwards compatibility
			// preferred to use /highdim/<data type> for download
			download dataType
			return
		}

		OntologyTerm concept = conceptsResourceService.getByKey(getConceptKey(conceptId))
		String conceptLink = studyLoadingServiceProxy.getOntologyTermUrl(concept)
		String selfLink = HighDimSummarySerializationHelper.getHighDimIndexUrl(conceptLink)

		respond wrapList(getHighDimSummaries(concept), selfLink)
	}

	def download(String dataType, String assayConstraints, String dataConstraints,
	             String conceptId, String projection) {
		assert dataType // ensured by url mapping
		Map<String, List> assayConstraintsSpec = JsonParametersParser.parseConstraints(assayConstraints)
		Map<String, List> dataConstraintsSpec = JsonParametersParser.parseConstraints(dataConstraints)

		String conceptKey = getConceptKey(conceptId)
		OutputStream out = new LazyOutputStreamDecorator(
				outputStreamProducer: { ->
					response.contentType = 'application/octet-stream'
					response.outputStream
				})

		try {
			highDimDataService.write conceptKey, dataType, projection, assayConstraintsSpec, dataConstraintsSpec, out
		}
		finally {
			out.close()
		}
	}

	private String getConceptKey(String concept) {
		OntologyTermCategory.keyFromURLPart(concept, studyLoadingServiceProxy.study)
	}

	private List<HighDimSummary> getHighDimSummaries(OntologyTerm concept) {
		Map<HighDimensionDataTypeResource, Collection<Assay>> resourceMap =
				highDimDataService.getAvailableHighDimResources(concept.key)

		resourceMap.collect { HighDimensionDataTypeResource hdr, Collection<Assay> assays ->
			new HighDimSummary(
					conceptWrapper: new OntologyTermWrapper(concept, false),
					name: hdr.dataTypeName,
					assayCount: assays.size(),
					supportedProjections: hdr.supportedProjections,
					supportedAssayConstraints: hdr.supportedAssayConstraints,
					supportedDataConstraints: hdr.supportedDataConstraints,
					// should be the same for all:
					genomeBuildId: assays.first().platform.genomeReleaseId
			)
		}
	}

	private ContainerResponseWrapper wrapList(List<HighDimSummary> source, String selfLink) {
		new ContainerResponseWrapper(
				container: source,
				componentType: HighDimSummary,
				links: [new Link(AbstractLinkingRenderer.RELATIONSHIP_SELF, selfLink)]
		)
	}
}
