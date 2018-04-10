package org.transmartproject.rest

import org.transmartproject.core.users.User
import org.transmartproject.db.log.AccessLogService

class AuditLogFilters {

	AccessLogService accessLogService
	User currentUserBean

	def filters = {
		lowDim(controller: 'observation', action: '*') {
			after = { model ->
				String fullUrl = getFullUrl()
				accessLogService.report currentUserBean, 'REST API Data Retrieval',
						eventMessage: "User (IP: ${ip}) got low dim. data with ${fullUrl}",
						requestURL: fullUrl
			}
		}

		highDim(controller: 'highDim', action: '*') {
			after = { model ->
				String fullUrl = getFullUrl()
				accessLogService.report currentUserBean, 'REST API Data Retrieval',
						eventMessage: "User (IP: ${ip}) got high dim. data with ${fullUrl}",
						requestURL: fullUrl
			}
		}
	}

	private String getIp() {
		request.getHeader('X-FORWARDED-FOR') ?: request.remoteAddr
	}

	private String getFullUrl() {
		request.forwardURI + (request.queryString ? '?' + request.queryString : '')
	}
}
