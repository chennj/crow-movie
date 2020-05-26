package org.crow.movie.user.common.cache;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.db.entity.AppCdn;
import org.crow.movie.user.common.db.entity.AppConfig;
import org.crow.movie.user.common.db.entity.AppLevel;
import org.crow.movie.user.common.db.model.NVPair;
import org.crow.movie.user.common.db.service.AppCdnService;
import org.crow.movie.user.common.db.service.AppConfigService;
import org.crow.movie.user.common.db.service.AppLevelService;
import org.crow.movie.user.common.util.ApplicationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 固定时间间隔刷新永久缓存
 * @author chenn
 *
 */
public class FixedCache {

	private static final Logger logger = LoggerFactory.getLogger(FixedCache.class);
	
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
	 * appConfig 最后更新日期
	 */
	private static volatile Timestamp appConfigLastUpdateTime = new Timestamp(System.currentTimeMillis());
	
	/**
	 * appConfig 是否需要更新
	 */
	private static volatile AtomicBoolean appConfigNeedReset = new AtomicBoolean(true);

	/**
	 * appLevel map 缓存
	 */
	private static volatile Map<Integer,AppLevel> appLevelCache;
	
	/**
	 * appLevel map 最后更新日期
	 */
	private static volatile Timestamp appLevelLastUpdateTime = new Timestamp(System.currentTimeMillis());
	
	/**
	 * appLevel map 是否需要更新
	 */
	private static volatile AtomicBoolean appLevelNeedReset = new AtomicBoolean(true);

	/**
	 * appLevel list缓存
	 */
	private static volatile List<AppLevel> appLevelListCache;
	
	/**
	 * appLevel list 最后更新日期
	 */
	private static volatile Timestamp appLevelListLastUpdateTime = new Timestamp(System.currentTimeMillis());
	
	/**
	 * appLevel list 是否需要更新
	 */
	private static volatile AtomicBoolean appLevelListNeedReset = new AtomicBoolean(true);
	
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
		
		/**
		 * 初始化app config类别
		 */
		AppConfigService cfgService = ApplicationUtil.getBean(AppConfigService.class);
		if (null == cfgService){
			logger.error(">>>failed getting AppConfigService Bean");
		} else {
			appConfigCache = cfgService.getSingle("");
		}
		
		/**
		 * 初始化app level map类别
		 */
		AppLevelService appLevelService = ApplicationUtil.getBean(AppLevelService.class);
		if (null == appLevelService){
			logger.error(">>>failed getting AppLevelService Bean");
		} else {
			List<AppLevel> appLevels = appLevelService.getAll();
			appLevelCache = new HashMap<>();
			for (AppLevel one : appLevels){
				appLevelCache.put(one.getId(), one);
			}
		}
		
		/**
		 * 初始化app level list类别
		 */
		if (null == appLevelService){
			logger.error(">>>failed getting AppLevelService Bean");
		} else {
			List<NVPair> conditions = new ArrayList<NVPair>();
			conditions.add(new NVPair("promoLimit",">", 0));
			appLevelListCache = appLevelService.getList("grade asc", conditions);
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
					appCdnCache.clear();
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
	
	public static Map<Integer, AppLevel> appLevelCache(){
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Long diff = now.getTime() - appLevelLastUpdateTime.getTime();
		
		if (diff > Const.TS_AUTO_FIND_APPLEVEL){
			
			if (appLevelNeedReset.compareAndSet(true, false)){
				
				try {
					appLevelLastUpdateTime = new Timestamp(System.currentTimeMillis());
					logger.info(">>>appLevel cache update");
					AppLevelService service = ApplicationUtil.getBean(AppLevelService.class);
					appLevelCache.clear();
					if (null == service){
						logger.error(">>>failed getting AppLevelService Bean");
					} else {
						List<AppLevel> appLevels = service.getAll();
						appLevelCache = new HashMap<>();
						for (AppLevel one : appLevels){
							appLevelCache.put(one.getId(), one);
						}
					}
				} catch (Exception e){
					e.printStackTrace();
					logger.error(">>>failed getting appLevel cache cause:",e.getMessage());
				} finally{
					appLevelNeedReset.compareAndSet(false, true);
				}
				
			}
		}
		
		return appLevelCache;
	}
	
	public static List<AppLevel> appLevelListCache(){
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Long diff = now.getTime() - appLevelListLastUpdateTime.getTime();
		
		if (diff > Const.TS_AUTO_FIND_APPLEVEL_LIST){
			
			if (appLevelListNeedReset.compareAndSet(true, false)){
				
				try {
					appLevelListLastUpdateTime = new Timestamp(System.currentTimeMillis());
					logger.info(">>>appLevelList cache update");
					AppLevelService service = ApplicationUtil.getBean(AppLevelService.class);
					appLevelListCache.clear();
					if (null == service){
						logger.error(">>>failed getting AppLevelService Bean");
					} else {
						List<NVPair> conditions = new ArrayList<NVPair>();
						conditions.add(new NVPair("promoLimit",">", 0));
						appLevelListCache = service.getList("grade asc", conditions);
					}
				} catch (Exception e){
					e.printStackTrace();
					logger.error(">>>failed getting appLevelList cache cause:",e.getMessage());
				} finally{
					appLevelListNeedReset.compareAndSet(false, true);
				}
				
			}
		}
		
		return appLevelListCache;
	}
}
