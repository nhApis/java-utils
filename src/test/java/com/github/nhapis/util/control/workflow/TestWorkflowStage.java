package com.github.nhapis.util.control.workflow;

import com.alibaba.fastjson.JSONObject;

public class TestWorkflowStage {
	
	public static void main(String [] args) {
		WorkflowStage stage = new WorkflowStage();
		stage.setInput(null);
		stage.setOutput(null);
		stage.setWorkflowNode(null);
		System.out.println(stage.toString());
		
		stage = new WorkflowStage(JSONObject.parseObject("{\"a\" : 1, \"b\" : \"bb\"}"),
				null, null);
		System.out.println(stage.toString());
	}

}
