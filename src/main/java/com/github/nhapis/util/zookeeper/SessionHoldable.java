package com.github.nhapis.util.zookeeper;

import org.apache.zookeeper.ZooKeeper;

public interface SessionHoldable {
	
	public void set(ZooKeeper zk);
	
	public ZooKeeper get();

}
