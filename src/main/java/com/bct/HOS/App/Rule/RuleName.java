package com.bct.HOS.App.Rule;

import java.io.File;
import java.io.FileReader;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRuleFactory;

public class RuleName {
	public RuleName() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static void main(String args[]) {
		try {
			//MVELRuleFactory ruleFactory = new MVELRuleFactory(new YamlRuleDefinitionReader());
			//Rule weatherRule = ruleFactory.createRule(new FileReader("weather-rule.yml"));

			// define facts
	       
	        
		}catch(Exception e) {
			System.out.println("ErrOR!..");
			e.printStackTrace();
		}
	}

}
