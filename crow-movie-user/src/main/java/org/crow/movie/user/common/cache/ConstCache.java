package org.crow.movie.user.common.cache;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.db.entity.AppCdn;
import org.crow.movie.user.common.db.entity.AppConfig;
import org.crow.movie.user.common.db.service.AppCdnService;
import org.crow.movie.user.common.db.service.AppConfigService;
import org.crow.movie.user.common.util.ApplicationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 固定时间间隔刷新永久缓存
 * @author chenn
 *
 */
public class ConstCache {

	private static final Logger logger = LoggerFactory.getLogger(ConstCache.class);
	
	/**
	 * appCdn 缓存
	 */
	private static volatile List<AppCdn> appCdnCache;
	
	/**
	 * appCdn 最后更新日期
	 */
	private static volatile Timestamp appCdnLastUpdateTime = new Timestamp(System.currentTimeMillis());
	
	/**
	 * appCdn 是否需要更新
	 */
	private static volatile AtomicBoolean appCdnNeedReset = new AtomicBoolean(true);
	
	/**
	 * appConfig 缓存
	 */
	private static volatile AppConfig appConfigCache;
	
	/**
	 * appCdn 最后更新日期
	 */
	private static volatile Timestamp appConfigLastUpdateTime = new Timestamp(System.currentTimeMillis());
	
	/**
	 * appCdn 是否需要更新
	 */
	private static volatile AtomicBoolean appConfigNeedReset = new AtomicBoolean(true);

	static {
		
		/**
		 * 初始化app cdn类别
		 */
		AppCdnService cdnService = ApplicationUtil.getBean(AppCdnService.class);
		if (null == cdnService){
			logger.error(">>>failed getting AppCdnService Bean");
		} else {
			appCdnCache = cdnService.getList("status", 1);
		}
		
		AppConfigService cfgService = ApplicationUtil.getBean(AppConfigService.class);
		if (null == cfgService){
			logger.error(">>>failed getting AppConfigService Bean");
		} else {
			appConfigCache = cfgService.getSingle("");
		}
	}
	
	public static List<AppCdn> appCdnCache(){
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Long diff = now.getTime() - appCdnLastUpdateTime.getTime();
		
		if (diff > Const.TS_AUTO_FIND_APP_CDN){
			
			if (appCdnNeedReset.compareAndSet(true, false)){
				
				try {
					appCdnLastUpdateTime = new Timestamp(System.currentTimeMillis());
					logger.info(">>>appCdn cache update");
					AppCdnService service = ApplicationUtil.getBean(AppCdnService.class);
					if (null == service){
						logger.error(">>>failed getting AppCdnService Bean");
					} else {
						appCdnCache = service.getList("status", 1);
					}
				} catch (Exception e){
					e.printStackTrace();
					logger.error(">>>failed getting appCdn Cache cache cause:",e.getMessage());
				} finally{
					appCdnNeedReset.compareAndSet(false, true);
				}
				
			}
		}
		
		return appCdnCache;
	}
	
	public static AppConfig appConfigCache(){
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Long diff = now.getTime() - appConfigLastUpdateTime.getTime();
		
		if (diff > Const.TS_AUTO_FIND_APP_CONFIG){
			
			if (appConfigNeedReset.compareAndSet(true, false)){
				
				try {
					appConfigLastUpdateTime = new Timestamp(System.currentTimeMillis());
					logger.info(">>>appConfig cache update");
					AppConfigService service = ApplicationUtil.getBean(AppConfigService.class);
					if (null == service){
						logger.error(">>>failed getting AppConfigService Bean");
					} else {
						appConfigCache = service.getSingle("");
					}
				} catch (Exception e){
					e.printStackTrace();
					logger.error(">>>failed getting appConfig cache cause:",e.getMessage());
				} finally{
					appConfigNeedReset.compareAndSet(false, true);
				}
				
			}
		}
		
		return appConfigCache;
	}
}
