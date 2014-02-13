package org.transmartproject.rest.marshallers

import org.transmartproject.core.ontology.OntologyTerm

class OntologyTermJsonMarshaller {

    static targetType = OntologyTerm

    def convert(OntologyTerm term) {
        [
                name:     term.name,
                key:      term.key,
                fullName: term.fullName,
        ]
    }
}