package org.transmartproject.rest.marshallers

import groovy.transform.CompileStatic
import org.grails.plugins.web.rest.render.DefaultRendererRegistry
import org.transmartproject.rest.misc.ComponentIndicatingContainer

/**
 * Customized the {@link DefaultRendererRegistry} by making it aware of the
 * {@link ComponentIndicatingContainer}.
 */
@CompileStatic
class TransmartRendererRegistry extends DefaultRendererRegistry {

	protected Class<?> getTargetClassForContainer(Class containerClass, object) {
		if (object instanceof ComponentIndicatingContainer) {
			object.componentType
		}
		else {
			super.getTargetClassForContainer containerClass, object
		}
	}
}
