import org.springframework.aop.scope.ScopedProxyFactoryBean
import org.springframework.stereotype.Component
import org.transmartproject.rest.marshallers.MarshallersRegistrar
import org.transmartproject.rest.marshallers.TransmartRendererRegistry

class TransmartRestApiGrailsPlugin {
	def version = '16.2'
	def grailsVersion = '2.3 > *'
	def title = 'Transmart Rest Api Plugin'
	def author = 'Transmart Foundation'
	def authorEmail = 'admin@transmartproject.org'
	def description = 'Adds rest api to transmart application'
	def documentation = 'https://wiki.thehyve.nl/'
	def license = 'GPL3'
	def organization = [name: 'The Hyve', url: 'http://www.thehyve.nl/']
	def developers = [
		[name: 'Ruslan Forostianov', email: 'ruslan@thehyve.nl'],
		[name: 'Jan Kanis', email: 'jan@thehyve.nl'],
		[name: 'Burt Beckwith', email: 'burt_beckwith@hms.harvard.edu']
	]
	def issueManagement = [system: 'JIRA', url: 'https://jira.thehyve.nl/browse/CHERKASY']
	def scm = [url: 'https://fisheye.ctmmtrait.nl/browse/transmart_rest_api']

	def doWithSpring = {
		xmlns context: 'http://www.springframework.org/schema/context'

		context.'component-scan'('base-package': 'org.transmartproject.rest') {
			context.'include-filter'(type: 'annotation', expression: Component.canonicalName)
		}

		studyLoadingServiceProxy(ScopedProxyFactoryBean) {
			targetBeanName = 'studyLoadingService'
		}

		marshallersRegistrar(MarshallersRegistrar) {
			packageName = 'org.transmartproject.rest.marshallers'
		}

		// override bean
		rendererRegistry(TransmartRendererRegistry) {
			modelSuffix = application.config.grails.scaffolding.templates.domainSuffix ?: ''
		}
	}

	def doWithApplicationContext = { ctx ->
		// Force the bean being initialized
		ctx.getBean 'marshallersRegistrar'
	}
}
