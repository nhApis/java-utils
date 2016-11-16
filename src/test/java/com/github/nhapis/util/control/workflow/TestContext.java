package com.github.nhapis.util.control.workflow;

import com.alibaba.fastjson.JSONObject;

public class TestContext {
	
	public static void main(String [] args) {
		Context context = new Context();
		context.initInput(JSONObject.parseObject("{\"a\" : 1, \"b\" : \"bb\"}"));
		System.out.println(context.toString());
		
		WorkflowStage stage = new WorkflowStage(null, null, null);
		context.appendStage(stage);
		System.out.println(context.toString());
	}

}
