package com.github.nhapis.util.web.page;


import com.github.nhapis.util.control.workflow.WorkflowNode;

public abstract class AbstractWebPageParser extends WorkflowNode {
	
	public AbstractWebPageParser(String node_name) {
		super(node_name);
	}

	public abstract Object processData(Object input);
	
}
