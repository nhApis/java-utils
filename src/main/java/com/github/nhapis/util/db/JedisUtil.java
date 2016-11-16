package com.github.nhapis.util.db;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.nhapis.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nhapis.util.StringUtil;
import com.github.nhapis.util.conf.Config;
import com.github.nhapis.util.uri.ServerURI;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedis;
import redis.clients.util.Pool;

public class JedisUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(JedisUtil.class);
	
	public void createShardedJedisPool(String redis_infos) {
		/* broken window (will be used)
		 * 
		// shard info
		List<JedisShardInfo> jdsInfoList = new ArrayList<JedisShardInfo>();
		
		String redis_list = conf.get("redis.list");
		if (redis_infos != null) {
			String [] arr = redis_list.split(",");
			if (arr != null && arr.length > 0) {
				for (int i = 0; i < arr.length; ++i) {
					String [] redis_info = arr[i].split(":");
					if (redis_info != null && redis_info.length == 3) {
						JedisShardInfo info = new JedisShardInfo(redis_info[0], Integer.parseInt(redis_info[1]));
						if (!StringUtil.isEmpty(redis_info[2])) {
							info.setPassword(redis_info[2]);
						}
						info.setConnectionTimeout(conf.get("redis.connection.timeout", 10));
						
						jdsInfoList.add(info);
					}
				}
			} else {
				logger.warn("redis.list is not valid : " + redis_list);
			}
		}
		*/
	}
	
	// one HostAndPort is ok
	public static JedisCluster createJedisCluster(Config conf) {
		JedisCluster jedis_cluster = null;
		
		try {
			Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		
			for (Pair<String, Integer> pair : ServerURI.parse(conf.getString("redis.servers"))) {
				nodes.add(new HostAndPort(pair.first, pair.second));
			}
			
			jedis_cluster = new JedisCluster(nodes);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		
		return jedis_cluster;
	}
	
	public static JedisCluster createJedisCluster(String redis_conf_file) {
		Config conf = new Config();
		conf.parse(redis_conf_file);
		return JedisUtil.createJedisCluster(conf);
	}
	
	public static Jedis createJedis(Config conf) {
		Jedis jedis = null;
		
		try {
			List<Pair<String, Integer>> nodes = ServerURI.parse(conf.getString("redis.servers"));
			jedis = new Jedis(nodes.get(0).first, nodes.get(0).second);
			String passwd = conf.getString("redis.password");
			if (!StringUtil.isEmpty(passwd)) {
				jedis.auth(passwd);
			}
			
			jedis.connect();
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		
		return jedis;
	}
	
	public static Jedis createJedis(String redis_conf_file) {
		Config conf = new Config();
		conf.parse(redis_conf_file);
		return JedisUtil.createJedis(conf);
	}
	
	public static Pool<Jedis> createJedisPool(Config conf) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(conf.getInt("jedis.pool.max.resources", 100));
		config.setMaxIdle(conf.getInt("jedis.pool.max.idle", 40));
		config.setMaxWaitMillis(conf.getInt("jedis.pool.get_resource.timeout", 10 * 1000));
		config.setTestOnBorrow(true);
		
		Pool<Jedis> pool = null;
		
		try {
			List<Pair<String, Integer>> nodes = ServerURI.parse(conf.getString("redis.servers"));
			String redis_server = nodes.get(0).first;
			int redis_port = nodes.get(0).second;
			String redis_passwd = conf.getString("redis.password");
			int connection_timeout = conf.getInt("redis.connection.timeout", 10 * 1000);
			if (StringUtil.isEmpty(redis_passwd)) {
				pool = new JedisPool(config, redis_server, redis_port, connection_timeout);
			} else {
				pool = new JedisPool(config, redis_server, redis_port, connection_timeout, redis_passwd);
			}
			
			if (!JedisUtil.validatePool(pool)) {
				JedisUtil.closeJedisPool(pool);
				pool = null;
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			JedisUtil.closeJedisPool(pool);
			pool = null;
		}
		
		return pool;
	}
	
	public static Pool<Jedis> createJedisPool(String redis_conf_file) {
		Pool<Jedis> jedis_pool = null;
		
		Config conf = new Config();
		if (conf.parse(redis_conf_file)) {
			jedis_pool = JedisUtil.createJedisPool(conf);
		} else {
			logger.warn("parse {} failed", redis_conf_file);
		}
		
		return jedis_pool;
	}
	
	public static void returnSource(Pool<Jedis> pool, Jedis jedis, boolean is_ok) {
		if (pool != null) {
			if (is_ok) {
				try {
					pool.returnResource(jedis);
				} catch (Exception e) {
					logger.warn(e.getMessage());
				}
			} else {
				try {
					pool.returnBrokenResource(jedis);
				} catch (Exception e) {
					logger.warn(e.getMessage());
				}
			}
		}
	}
	
	public static void returnSource(Pool<ShardedJedis> pool, ShardedJedis jedis, boolean is_ok) {
		if (pool != null) {
			if (is_ok) {
				try {
					pool.returnResource(jedis);
				} catch (Exception e) {
					logger.warn(e.getMessage());
				}
			} else {
				try {
					pool.returnBrokenResource(jedis);
				} catch (Exception e) {
					logger.warn(e.getMessage());
				}
			}
		}
	}
	
	public static boolean validatePool(Pool<Jedis> pool) {
		boolean ret = false;
		Jedis jedis = null;
		
		try {
			jedis = pool.getResource();
			ret = (jedis != null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
		
		if (ret) {
			returnSource(pool, jedis, ret);
		}
		
		return ret;
	}
	
	public static void closeJedis(Jedis jedis) {
		if (jedis != null) {
			try {
				jedis.disconnect();
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}
	
	public static void closeJedisCluster(JedisCluster jedis_cluster) {
		if (jedis_cluster != null) {
			try {
				jedis_cluster.close();
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}
	
	public static void closeJedisPool(Pool<Jedis> pool) {
		if (pool != null) {
			try {
				pool.close();
			} catch (Exception e) {
				logger.warn(e.getMessage());
			}
		}
	}
	
	public synchronized static List<String> consumeListByRangeSync(Jedis jedis, String redis_key, 
			long start, long end) {
		List<String> result = jedis.lrange(redis_key, start, end);
		if (result != null) {
			jedis.ltrim(redis_key, result.size(), -1);
		}
		return result;
	}

}
