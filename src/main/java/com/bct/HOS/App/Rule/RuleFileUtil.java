package com.bct.HOS.App.Rule;

import java.io.File;

import com.bct.HOS.App.BO.NotificationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class RuleFileUtil {

	public RuleFileUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param notificationName
	 * @param description
	 * @param condition
	 * @param actions
	 * @param filepath
	 * @return
	 */
	public boolean generateRuleFile(String notificationName, String description, String condition, String actions,
			String filepath) {
		boolean result = false;
		try {
			NotificationConfig nconfig = new NotificationConfig();
			nconfig.setNotificationName(notificationName);
			nconfig.setDescription(description);
			nconfig.setCondition(condition);
			nconfig.setActions(actions);
			ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
			objectMapper.writeValue(new File(filepath), nconfig);
			//System.out.println(objectMapper.writeValueAsString(nconfig));
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param filepath
	 * @return
	 */
	public NotificationConfig getRuleFile(String filepath) {
		NotificationConfig config = null;
		try {
			File file = new File(filepath);
			ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
			config = objectMapper.readValue(file, NotificationConfig.class);
			//System.out.println("Application config info " + config.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}

	public static void main(String args[]) {
		try {
			String notificationName = "12";
			String description = "12";
			String condition = "12";
			String actions = "aa";
			String filepath = "D://test.yml";
			new RuleFileUtil().generateRuleFile(notificationName, description, condition, actions, filepath);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
