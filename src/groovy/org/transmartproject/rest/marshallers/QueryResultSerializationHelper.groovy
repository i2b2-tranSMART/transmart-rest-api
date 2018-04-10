package org.transmartproject.rest.marshallers

import grails.rest.Link
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.transmartproject.core.querytool.QueryResult

import static grails.rest.render.util.AbstractLinkingRenderer.RELATIONSHIP_SELF
import static org.transmartproject.rest.marshallers.MarshallerSupport.getPropertySubsetForSuperType

/**
 * Serialization of {@link QueryResult} objects.
 */
@CompileStatic
class QueryResultSerializationHelper extends AbstractHalOrJsonSerializationHelper<QueryResult> {

	final Class<QueryResult> targetType = QueryResult
	final String collectionName = null // will never be in collection

	Collection<Link> getLinks(QueryResult queryResult) {
		[new Link(RELATIONSHIP_SELF, '/patient_sets/' + queryResult.id)]
	}

	@CompileDynamic
	Map<String, Object> convertToMap(QueryResult queryResult) {
		Map<String, Object> map = getPropertySubsetForSuperType(queryResult, QueryResult)
		map.status = map.status.name()
		map
	}

	Set<String> getEmbeddedEntities(QueryResult object) {
		['patients'] as Set
	}
}
