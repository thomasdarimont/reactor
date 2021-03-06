/* * Copyright (c) 2011-2015 Pivotal Software Inc., Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reactor.groovy

import groovy.transform.CompileStatic
import reactor.bus.Event
import reactor.core.config.DispatcherType
import reactor.core.dispatch.SynchronousDispatcher
import reactor.groovy.config.GroovyEnvironment
import reactor.rx.broadcast.Broadcaster

import static reactor.bus.selector.Selectors.matchAll
import static reactor.bus.selector.Selectors.object

@CompileStatic
class StaticConfiguration {

	static GroovyEnvironment test() {
		GroovyEnvironment.create {
			environment {
				defaultDispatcher = "test"

				dispatcher('test') {
					type = DispatcherType.SYNCHRONOUS
				}
			}
		}
	}

	static GroovyEnvironment test2() {
		GroovyEnvironment.create {
			reactor('test1') {
				on('test') {
				}

				reactor('child_test1') {
					ext 'a', 'rw'
				}
			}
			reactor('test2') {
				ext 'a', '2'
			}
		}
	}

	static GroovyEnvironment test3() {
		GroovyEnvironment.create {
			reactor('test1') {
				dispatcher = new SynchronousDispatcher()
				on('test') {
					reply it
				}

			}
		}
	}

	static GroovyEnvironment test4() {
		def parentEnvironment = GroovyEnvironment.create {
			environment {
				defaultDispatcher = 'testDispatcher'

				dispatcher 'testDispatcher', new SynchronousDispatcher()
			}

			reactor('test1') {
				dispatcher 'testDispatcher'
				routingStrategy 'random'
				on('test') {
					reply it
				}
			}
		}

		GroovyEnvironment.create {
			include parentEnvironment

			reactor('test1') {
				on('test2') {
					reply it
				}
			}
			reactor('test2') {
				dispatcher 'testDispatcher'
			}
		}
	}

	static GroovyEnvironment test5() {
		GroovyEnvironment.create {
			environment {
				defaultDispatcher = 'testDispatcher'
				dispatcher 'testDispatcher', new SynchronousDispatcher()
			}

			def stream = Broadcaster.<Event> create().
			    map { Event ev ->
						ev.copy(ev.data.toString().startsWith('intercepted') ? ev.data : 'intercepted')
					}.combine()

			def stream2 = Broadcaster.<Event> create().
					map { Event ev ->
						ev.copy("$ev.data twice")
					}.combine()

			reactor('test1') {
				processor matchAll(), stream
				processor object('test'), stream2


				on('test') {
					reply it
				}
				on('test2') {
					reply it
				}

			}

			reactor('test2') {
				processor 'test', Broadcaster.<Event<?>> create().filter{false}.combine()

				on('test') {
					reply it
					throw new Exception('never')
				}
				on('test2') {
					reply it
				}

			}
		}
	}
}