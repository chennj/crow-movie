package org.crow.movie.user.common.cache;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.crow.movie.user.common.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DynamicCache {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ConcurrentHashMap<Object, Object> cache;
	
	/**
	 * 定时器
	 */
	private Timer timer;
	
	/**
	 * 用于防止定时任务叠加执行
	 */
	private static boolean isRun = false;
	
	/**
	 * 定时器清理间隔（默认30分钟执行一次）
	 */
	private Long DEFAULT_TASK_DELAY = 30 * 60000L;
	
	/**
	 * 存储有效期,默认存储60分钟 
	 */
	private Long DEFAULT_TIMEOUT = 60000 * 60L;
	
	/**
	 * 锁
	 */
	private byte[] lock;
	
	/**
	 * 单例模式
	 */
	private static volatile DynamicCache instance_ = null;
	
	public static DynamicCache instance(){
		
		if (null == instance_){
			synchronized(DynamicCache.class){
				if (null == instance_){
					instance_ = new DynamicCache();
				}
			}
		}
		
		return instance_;
	}
	
	private DynamicCache(){
		
		cache = new ConcurrentHashMap<Object, Object>();
		lock = new byte[0];
        timer = new Timer();  
        timer.schedule(new TimerTask() {  
            @Override  
            public void run() {  
                clearCache();  
            }  
        }, 0, DEFAULT_TASK_DELAY);  
	}
	
	@SuppressWarnings("rawtypes")
	private void clearCache() {
		
        if (!isRun) {  
            synchronized (lock) {  
                try {
                    isRun = true; 
                    int clearSize=0;
                    long start = System.currentTimeMillis();  
                    if (!cache.isEmpty()) {  
                        Iterator<Entry<Object, Object>> it = cache.entrySet().iterator();  
                        while (it.hasNext()) {  
                        	Entry e = it.next();  
                            if (e.getValue() == null) {  
                            	cache.remove(e.getKey());  
                            	clearSize++;
                            } else {  
                                if (((CacheValue) e.getValue()).isTimeout()) {  
                                	cache.remove(e.getKey()); 
                                	clearSize++;
                                }  
                            }  
                        }  
                    }
                    logger.info("本次清理：" + clearSize + ",当前剩余：" + cache.size() + ",清理用时：" + (System.currentTimeMillis() - start) + "毫秒");  
                } finally {  
                    isRun = false;  
                }  
            }  
        }
	}
	
	public Object get(String key){
		
		if (StrUtil.isEmpty(key))return null;
		CacheValue cv = (CacheValue) instance().cache.get(key);
		return (null==cv?null:cv.getValue());
	}
	
	/**
	 * 默认缓存 30分钟 
	 * @param key
	 * @param value
	 * @return
	 */
	public Object put(String key, Object value){
		
		return put(key, value, DEFAULT_TIMEOUT);
	}
	
	/**
	 * 单位默认是秒,传入小于0的值则使用默认值(30分钟)，传入0 则永久有效
	 * @param key
	 * @param value
	 * @param timeout 单位:秒
	 * @return
	 */
	public Object put(Object key, Object value, Long timeout){
		
        CacheValue cvalue = new CacheValue();  
        cvalue.setKey(key);  
        if (timeout > 0) {  
            cvalue.setTimeout(timeout * 1000L);  
        } else if (timeout < 0) {  
            cvalue.setTimeout(DEFAULT_TIMEOUT);  
        } else {  
            cvalue.setTimeout(0L);  
        }  
        cvalue.setValue(value);  
        
		return cache.put(key, cvalue); 
	}
	
	public boolean constainsKey(String key){
		
		return get(key)!=null;
	}
	
    public void remove(String key) {  
        cache.remove(key);  
    }  
  
    public long size() {  
        return cache.size();  
    }  
    
    public void setDefaultTimeout(Long timeout) {  
        if (timeout > 0) {  
            DEFAULT_TIMEOUT = timeout;  
        }  
    }  
  
    public void setDefaultClearDelay(Long delay) {  
        if (delay >= 500) {  
            DEFAULT_TASK_DELAY = delay;  
        }  
    }  
  
    public Long getDefaultTimeout() {  
        return DEFAULT_TIMEOUT;  
    }  
  
    public Long getDefaultClearDelay() {  
        return DEFAULT_TASK_DELAY;  
    }  
    
	class CacheValue implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
        private Object key;  
        private Long createTime;  
        private Long timeout;  
        private Object value;  
		
        public CacheValue() {
        	// 将创建时间提前100毫秒，防止定时回收任务因上一次执行时间过长导致回收滞后  
            createTime = System.currentTimeMillis() - 100;
        }  
  
        /** 
         * 判断对象是否已经失效 
         *  
         * @return 返回 true 失效 false 没有失效 
         */  
        public boolean isTimeout() {  
            boolean ret = true;  
            if (value != null) {  
                if (timeout == 0) {  
                    return false;  
                } else {  
                    ret = System.currentTimeMillis() - createTime >= timeout;  
                    if (ret) {  
                        value = null;  
                    }  
                }  
            }  
            return ret;  
        }  
  
        public Object getKey() {  
            return key;  
        }  
  
        public void setKey(Object key) {  
            this.key = key;  
        }  
  
        public Long getCreateTime() {  
            return createTime;  
        }  
        
  
        public Long getTimeout() {  
            return timeout;  
        }  
  
        public void setTimeout(Long timeout) {  
            this.timeout = timeout;  
        }  
  
        public Object getValue() {  
    		if (isTimeout())return null; 
    		//每次被查询，重置缓存开始时间
			this.createTime = System.currentTimeMillis() - 100;
			//logger.info(key+":缓存时间被重置"); 
            return value;  
        }  
  
        public void setValue(Object value) {  
            this.value = value;  
        }  
	}
}
