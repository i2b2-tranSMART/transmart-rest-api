package org.transmartproject.rest.misc

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.transmart.plugin.shared.SecurityService
import org.transmartproject.core.exceptions.AccessDeniedException
import org.transmartproject.core.users.ProtectedOperation
import org.transmartproject.core.users.ProtectedResource
import org.transmartproject.core.users.User
import org.transmartproject.core.users.UsersResource

/**
 * Spring request-scoped bean that makes it easy to fetch the logged in user.
 */
@CompileStatic
@Component
@Scope(value = 'request', proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j('logger')
class CurrentUser implements User {

	@Autowired private SecurityService securityService
	@Autowired private UsersResource usersResourceService

	@Autowired(required = false)
	private SpringSecurityService springSecurityService

	private Closure<User> lazyUser = { ->
		if (springSecurityService == null || !SpringSecurityUtils.securityConfig.active) {
			logger.warn 'Spring security service not available or inactive, returning dummy user administrator'
			return new DummyAdministrator()
		}

		if (!springSecurityService.isLoggedIn()) {
			logger.warn 'User is not logged in; throwing'
			throw new AccessDeniedException('User is not logged in')
		}

		usersResourceService.getUserFromUsername securityService.currentUsername()
	}
	@Lazy private User delegate = lazyUser()

	Long getId() {
		delegate.id
	}

	String getUsername() {
		delegate.username
	}

	String getRealName() {
		delegate.realName
	}

	boolean canPerform(ProtectedOperation operation, ProtectedResource protectedResource) {
		delegate.canPerform operation, protectedResource
	}

	void checkAccess(ProtectedOperation operation, ProtectedResource resource) {
		if (!canPerform(operation, resource)) {
			throw new AccessDeniedException(
					"Denied user $this permission to effect operation $operation on resource $resource")
		}
	}

	String toString() {
		'CurrentUser(' + delegate + ')'
	}

	@CompileStatic
	static class DummyAdministrator implements User {

		// These correspond to the properties of the default transmart administrator user
		final Long id = 1
		final String username = 'admin'
		final String realName = 'Sys Admin'

		boolean canPerform(ProtectedOperation operation, ProtectedResource protectedResource) {
			true
		}
	}
}
