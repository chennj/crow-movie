package org.crow.movie.user.framework.registry;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ServiceRegistryImpl implements ServiceRegistry, Watcher{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static CountDownLatch latch = new CountDownLatch(1);
	
	public ZooKeeper zk;
	
	public static final int SESSION_TIMEOUT = 5000;
	
	public static final String REGISTRY_PATH = "/registry-movie-user";
	
	public ServiceRegistryImpl(){}
	
	public ServiceRegistryImpl(String zkServers){
		
		// 创建 zookeeper客户端
		try {
			zk = new ZooKeeper(zkServers, SESSION_TIMEOUT, this);
			latch.await();
			logger.debug("conntected to zookeeper");
		} catch (Exception e){
			logger.error("create zookeeper client failure", e);
		}
	}
	
	@Override
	public void process(WatchedEvent event) {
		
		if (event.getState() == Event.KeeperState.SyncConnected){
			latch.countDown();
		}
	}

	@Override
	public void registry(String serviceName, String serviceAddress) {
		
		try {
			// 创建根节点 （持久节点）
			String registryPath = REGISTRY_PATH;
			if (zk.exists(registryPath, false) == null){
				zk.create(registryPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				logger.debug("create registry node:{}",registryPath);
			}
			
			// 创建服务节点（持久节点）
			String servicePath = registryPath + "/" + serviceName;
			if (zk.exists(servicePath, false) == null){
				zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				logger.debug("create service node:{}",servicePath);
			}
			
			// 创建地址节点（临时顺序节点）
			String addressPath = servicePath + "address-";
			String addressNode = zk.create(
					addressPath, 
					serviceAddress.getBytes(), 
					ZooDefs.Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.debug("create address node: {}",addressPath);
		} catch (Exception e){
			logger.error("create node failure",e);
		}
	}

}
