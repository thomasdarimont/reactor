/*
 * Copyright (c) 2011-2015 Pivotal Software Inc., Inc. All Rights Reserved.
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

package reactor.core.support;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Juergen Hoeller
 */
public class LinkedCaseInsensitiveMapTests {

	private LinkedCaseInsensitiveMap<String> map;

	@Before
	public void setUp() {
		map = new LinkedCaseInsensitiveMap<String>();
	}

	@Test
	public void putAndGet() {
		map.put("key", "value1");
		map.put("key", "value2");
		map.put("key", "value3");
		assertEquals(1, map.size());
		assertEquals("value3", map.get("key"));
		assertEquals("value3", map.get("KEY"));
		assertEquals("value3", map.get("Key"));
	}

	@Test
	public void putWithOverlappingKeys() {
		map.put("key", "value1");
		map.put("KEY", "value2");
		map.put("Key", "value3");
		assertEquals(1, map.size());
		assertEquals("value3", map.get("key"));
		assertEquals("value3", map.get("KEY"));
		assertEquals("value3", map.get("Key"));
	}

}