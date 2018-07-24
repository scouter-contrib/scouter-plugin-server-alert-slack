/*
 *  Copyright 2016 Scouter Project.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  
 *  @author Gookeun Lim
 */
package scouter.plugin.server.alert.slack;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import scouter.server.Configure;

/**
 * Unit Test For MonitoringGroupConfigure
 *  
 * @author Gookeun Lim (passion.lim@gmail.com) on 2018.07.24 
 */
public class MonitoringGroupConfigureTest {
	
	MonitoringGroupConfigure sut;

	@Test
	public void test_getValue() {
		String objType = "order_jvm";
		Configure conf = mock(Configure.class);
		sut = new MonitoringGroupConfigure(conf);
		
		// default value
		assertEquals("http://webhook.example.com/default", sut.getValue("ext_plugin_slack_webhook_url", objType, "http://webhook.example.com/default"));
		
		// conf value
		when(conf.getValue("ext_plugin_slack_webhook_url")).thenReturn("http://webhook.example.com/tomcat");
		assertEquals("http://webhook.example.com/tomcat", sut.getValue("ext_plugin_slack_webhook_url", objType));
		
		// appended value with groupKey
		when(conf.getValue(objType+".ext_plugin_slack_webhook_url")).thenReturn("http://webhook.example.com/order_jvm");
		assertEquals("http://webhook.example.com/order_jvm", sut.getValue("ext_plugin_slack_webhook_url", objType));
	}
	
	@Test
	public void test_getIntegerValue() {
		String objType = "order_jvm";
		Configure conf = mock(Configure.class);
		sut = new MonitoringGroupConfigure(conf);
		
		// default value
		assertEquals(1000, sut.getInt("ext_plugin_slack_elapsed_time_threshold", objType, 1000));
		
		// conf value
		when(conf.getValue("ext_plugin_slack_elapsed_time_threshold")).thenReturn("2000");
		assertEquals(2000, sut.getInt("ext_plugin_slack_elapsed_time_threshold", objType, 1000));
		
		// appended value with groupKey
		when(conf.getValue(objType+".ext_plugin_slack_elapsed_time_threshold")).thenReturn("3000");
		assertEquals(3000, sut.getInt("ext_plugin_slack_elapsed_time_threshold", objType, 1000));
		
	}

	@Test
	public void test_getLongValue() {
		String objType = "order_jvm";
		Configure conf = mock(Configure.class);
		sut = new MonitoringGroupConfigure(conf);
		
		// default value
		assertEquals(1000000000000L, sut.getLong("ext_plugin_slack_elapsed_time_threshold", objType, 1000000000000L));
		
		// conf value
		when(conf.getValue("ext_plugin_slack_elapsed_time_threshold")).thenReturn("2000000000000");
		assertEquals(2000000000000L, sut.getLong("ext_plugin_slack_elapsed_time_threshold", objType, 1000000000000L));
		
		// appended value with groupKey
		when(conf.getValue(objType+".ext_plugin_slack_elapsed_time_threshold")).thenReturn("3000000000000");
		assertEquals(3000000000000L, sut.getLong("ext_plugin_slack_elapsed_time_threshold", objType, 1000000000000L));
	}

	@Test
	public void test_getBooleanValue() {
		String objType = "order_jvm";
		Configure conf = mock(Configure.class);
		sut = new MonitoringGroupConfigure(conf);
		
		// default value
		assertEquals(Boolean.TRUE, sut.getBoolean("ext_plugin_slack_elapsed_time_threshold", objType, Boolean.TRUE));
		
		// conf value
		when(conf.getValue("ext_plugin_slack_elapsed_time_threshold")).thenReturn("false");
		assertEquals(Boolean.FALSE, sut.getBoolean("ext_plugin_slack_elapsed_time_threshold", objType, Boolean.TRUE));
		
		// appended value with groupKey
		when(conf.getValue(objType+".ext_plugin_slack_elapsed_time_threshold")).thenReturn("true");
		assertEquals(Boolean.TRUE, sut.getBoolean("ext_plugin_slack_elapsed_time_threshold", objType, Boolean.FALSE));
	}
	
}
