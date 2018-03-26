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

def defaultVMSettings = [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]

final String CLOVER_VERSION = '4.1.1'
def enableClover = System.getenv('CLOVER')

def dm
try {
	Class dmClass = new GroovyClassLoader().parseClass(
			new File('../transmart-dev/DependencyManagement.groovy'))
	dm = dmClass?.newInstance()
}
catch (ignored) {}

grails.project.fork = [test: [*: defaultVMSettings, daemon: true], console: defaultVMSettings]
grails.project.work.dir = 'target'
grails.servlet.version = '3.0'

grails.project.dependency.resolver = 'maven'
grails.project.dependency.resolution = {
	inherits 'global'
	log 'warn'

	if (!dm) {
		repositories {
			mavenLocal() // Note: use 'grails maven-install' to install required plugins locally
			grailsCentral()
			mavenCentral()
			mavenRepo 'http://ec2-35-170-59-132.compute-1.amazonaws.com:8080/artifactory/plugins-snapshots'
			mavenRepo 'http://ec2-35-170-59-132.compute-1.amazonaws.com:8080/artifactory/plugins-releases'
			mavenRepo 'https://repo.transmartfoundation.org/content/repositories/public/'
		}
	}
	else {
		dm.configureRepositories delegate
	}

	dependencies {
		compile 'com.google.protobuf:protobuf-java:2.5.0'
		compile 'org.transmartproject:transmart-core-api:16.2'

		runtime 'org.postgresql:postgresql:9.3-1100-jdbc41', { export = false }
		runtime 'com.oracle:ojdbc7:12.1.0.1', { export = false }

		test 'org.gmock:gmock:0.8.3', { transitive = false }
		test 'org.hamcrest:hamcrest-library:1.3'
		test 'org.hamcrest:hamcrest-core:1.3'
		test 'org.codehaus.groovy.modules.http-builder:http-builder:0.6', {
			excludes 'groovy', 'nekohtml'
			exported = false
		}
	}

	plugins {
		build ':release:3.1.2', ':rest-client-builder:2.1.1', {
			export = false
		}
		build ':tomcat:7.0.47', {
			export = false
		}

		compile ':spring-security-core:2.0.0'

		runtime ':hibernate:3.6.10.19', {
			export = false
		}

		test ':functional-test:2.0.0'

		if (!dm) {
			compile ':transmart-core:18.1-SNAPSHOT'
			test ':transmart-core-db-tests:18.1-SNAPSHOT'
		}
		else {
			dm.internalDependencies delegate
		}

		if (enableClover) {
			compile ":clover:$CLOVER_VERSION", {
				export = false
			}
		}
	}
}

if (enableClover) {
	grails.project.fork.test = false

	clover {
		on = true

		srcDirs = ['src/java', 'src/groovy', 'grails-app', 'test']
		excludes = ['**/conf/**', '**/plugins/**', '**/HighDimProtos.java']

		reporttask = { ant, binding, plugin ->
			String reportDir = "$binding.projectTargetDir/clover/report"
			ant.'clover-report' {
				ant.current(outfile: reportDir, title: 'transmart-rest-api') {
					format(type: 'html', reportStyle: 'adg')
					testresults(dir: 'target/test-reports', includes: '*.xml')
					ant.columns {
						lineCount()
						filteredElements()
						uncoveredElements()
						totalPercentageCovered()
					}
				}
				ant.current(outfile: "$reportDir/clover.xml") {
					format(type: 'xml')
					testresults(dir: 'target/test-reports', includes: '*.xml')
				}
			}
		}
	}
}

dm?.with {
	configureInternalPlugin 'runtime', 'transmart-core'
	configureInternalPlugin 'test', 'transmart-core'
	configureInternalPlugin 'test', 'transmart-core-db-tests'
}

dm?.inlineInternalDependencies grails, grailsSettings
