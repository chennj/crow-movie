package org.crow.movie.user.common.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public abstract class AbstractBaseService<T> {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	AbstractBaseDao<T> baseDao;
	
	public BaseDao<T> getBaseDao(){
		
		return baseDao;
	}
	
	public void setBaseDao(AbstractBaseDao<T> baseDao) {

		this.baseDao = baseDao;
	}
	
	public List<T> getAll(){
		return baseDao.findAll();
	}
	
	public T getById(String id){
		return baseDao.get(id);
	}
	
	public T getById(Integer id){
		return baseDao.get(id);
	}
	
	public T modify(T t){
		return baseDao.update(t);
	}
	
	public void del(Serializable id){
		baseDao.delete(id);
	}
	
	public List<T> getList(String key,String value){
		Map<String, Object> eq = new HashMap<String, Object>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put(key, value);
			}
		};
		return baseDao.findList("", eq, null, null, null, null, null, null, null, null, null, null);
	}
	
	public T getUnique(String key, String value){
		
		HashMap<String, Object> eq = new HashMap<>();
		eq.put(key, value);
		return baseDao.findUnique(eq, null, null, null, null, null, null, null, null, null, null);
	}
	
	public T getSingle(String key, String value){
		
		HashMap<String, Object> eq = new HashMap<>();
		eq.put(key, value);
		List<T> results = baseDao.findList(null, eq, null, null, null, null, null, null, null, null, null, null);
		if (results != null && results.size()>0){
			return results.get(0);
		} else {
			return null;
		}
	}
	
	public T getSingle(String orderBy, Map<String,Object> eq){
		List<T> results = baseDao.findList(orderBy, eq, null, null, null, null, null, null, null, null, null, null);
		if (results != null && results.size()>0){
			return results.get(0);
		} else {
			return null;
		}
	}

	public <V> List<T> getList(String key, List<V> inList){
		
		Map<String, List<V>> in = new HashMap<>();
		in.put(key, inList);
		return baseDao.findList("", null, null, null, null, null, null, in, null, null, null, null);
	}

	public T add(T t){
		return baseDao.add(t);
	}
	
	public Page<?> page(String nativeSql,int page, int pageSize, Object... params){
		return baseDao.findPage(nativeSql, page, pageSize, params);
	}
	
	public Page<T> page(int page, int pageSize, Map<String, Object> eq){
		return baseDao.findPage(page, pageSize, null, eq, null, null, null, null, null, null, null, null, null, null);
	}

	public Page<T> page(int page, int pageSize, String order, Map<String, Object> eq){
		return baseDao.findPage(page, pageSize, order, eq, null, null, null, null, null, null, null, null, null, null);
	}

	public Page<T> pageLk(int page, int pageSize, Map<String, Object> like){
		boolean isDebug =
			     java.lang.management.ManagementFactory.getRuntimeMXBean().
			         getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;        
		if (isDebug){
			String s = "page like condition key=value:";
			for (Map.Entry<String, Object> one : like.entrySet()){
				s += one.getKey()+"="+one.getValue();
			}
			logger.info(s);
		}
		return baseDao.findPage(page, pageSize, null, null, null, like, null, null, null, null, null, null, null, null);
	}
	
	public Page<T> pageEqLk(int page, int pageSize, Map<String, Object> eq, Map<String, Object> like){

		return baseDao.findPage(page, pageSize, null, eq, null, like, null, null, null, null, null, null, null, null);
	}
	
	public void addList(List<T> list) throws Exception{
		baseDao.batchInsert(list);
	}
	
	public void modifyList(List<T> list){
		baseDao.batchUpdate(list);
	}
}
