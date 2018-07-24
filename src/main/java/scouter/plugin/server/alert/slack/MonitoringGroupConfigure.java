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

import scouter.server.Configure;

/**
 * <p>a class that helps to find a property value which matches to a specific monitoring_group_type key(aks obj_type).
 *  * You can defined a montioring_group_type values like below</p>
 * 
 * {objType}.{remain value}ext_plugin_slack_...
 *  
 * ex) 
 *   ext_plugin_slack_channel=general_monitoring       
 *   order_jvm.ext_plugin_slack_channel=order_monitoring
 *   stock_jvm.ext_plugin_slack_channel=stock_monitoring
 * 
 * @author Gookeun Lim (passion.lim@gmail.com) on 2018.07.24
 *
 */
public class MonitoringGroupConfigure {
	private Configure conf;

	public MonitoringGroupConfigure(Configure config) {
		this.conf = config;
	}

	public String getValue(String key, String objType) {
		return this.getValue(key, objType, null);
	}

	public String getGroupKey(String originalKey, String objType) {
		if (originalKey == null) {
			return originalKey;
		}
		
		return objType+"."+originalKey;
	}

	public String getValue(String key, String objType, String defaultValue) {
		String groupKey = getGroupKey(key, objType);
		String value = conf.getValue(groupKey);
		if (value != null && value.trim().length() > 0) {
			return value;
		}
		// default key value
		value = conf.getValue(key);
		return value != null? value : defaultValue;
	}

	public Boolean getBoolean(String key, final String objType, Boolean defaultValue) {
		String groupKey = getGroupKey(key, objType);
		Boolean value = toBoolean(conf.getValue(groupKey));
		if (value != null) {
			return value;
		}
		// default key value
		value = toBoolean(conf.getValue(key));
		return value != null? value : defaultValue;
	}
	
	public int getInt(String key, String objType, int defaultValue) {
		String groupKey = getGroupKey(key, objType);
		Integer value = toInteger(conf.getValue(groupKey));
		if (value != null) {
			return value;
		}
		// default key value
		value = toInteger(conf.getValue(key));
		return value != null ? value : defaultValue;
	}

	public long getLong(String key, String objType, long defaultValue) {
		String groupKey = getGroupKey(key, objType);
		Long value = toLong(conf.getValue(groupKey));
		if (value != null) {
			return value;
		}
		// default key value
		value = toLong(conf.getValue(key));
		return value != null ? value : defaultValue;
	}

	
	private Long toLong(String value) {
		try {
			if (value != null) {
				return Long.parseLong(value);
			}
		} catch (Exception e) {
			// ignore exception
		}
		return null;
	}

	private Integer toInteger(String value) {
		try {
			if (value != null) {
				return Integer.parseInt(value);
			}
		} catch (Exception e) {
			// ignore exception
		}
		return null;
	}

	private Boolean toBoolean(String value) {
		try {
			if (value != null) {
				return Boolean.parseBoolean(value);
			}
		} catch (Exception e) {
			// ignore exception
		}
		return null;
	}
}