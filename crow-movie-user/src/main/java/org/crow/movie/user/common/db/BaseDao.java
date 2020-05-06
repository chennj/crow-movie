package org.crow.movie.user.common.db;

import java.io.Serializable;
import java.util.List;

//import org.springframework.orm.hibernate5.HibernateTemplate;

public interface BaseDao<T> {

	/**	
	 * <b>function:</b>提供session使用
	 * @createDate 2016-11-25 下午03:39:22
	 * @author ending
	 * @return Session
	 * */
	//public Session session();
	/**	
	 * <b>function:</b>提供HibernateTemplate使用
	 * @createDate 2016-11-25 下午03:39:22
	 * @author ending
	 * @return HibernateTemplate
	 * */
	//public HibernateTemplate getTemplate();
	
	/*------------------------------------------------------*/
	
	/**
	 * 通过SQL查询
	 * @param hql
	 * @return
	 * @throws Exception
	 */
	//public <T> List<T> findBySql(String sql) throws Exception;
	
	/**
     * 添加实体类
     * @param t
     * @return
     */
    public T add(T t);
 
    /**
     * 更新实体类
     * @param t
     * @return
     */
    public T update(T t);
 
    /**
     * 根据主键ID删除实体类
     * @param id
     */
    public void delete(Serializable id);
 
    /**
     * 根据JPQL语句更新或删除操作
     * @param jpql
     * @param obj
     */
    public int executeUpdate(String jpql,Object... obj);
 
    /**
     * 根据主键ID查找单个实体类
     * @param id
     * @return
     */
    public T load(Serializable id);
 
    /**
     * 根据JPQL语句查询单个实体类
     * @param jpql
     * @param obj（参数可有可无）
     * @return
     */
    public T load(String jpql,Object... obj);
 
    /**
     * 查找所有的实体类
     * @return
     */
    public List<T> findAll();
 
    /**
     * 根据JPQL语句查询集合实体类
     * @param jpql
     * @param obj（参数可有可无）
     * @return
     */
    public List<T> find(String jpql,Object... obj);
 
    /**
     * 聚合查询
     * @param jpql
     * @param obj
     * @return
     */
    public Object findByAggregate(String jpql,Object... obj);
 
    /**
     * 查找总记录数
     * @return
     */
    public int count();
 
    /**
     * 根据JPQL语句查询记录数
     * @param jpql
     * @param obj
     * @return
     */
    public int count(String jpql,Object... obj);
 
    /**
     * 分页查询集合实体类
     * @param firstIndex
     * @param maxResults
     * @return
     */
    public List<T> findPage(Integer firstIndex, Integer maxResults);
 
    /**
     * 根据JPQL语句查询集合实体类
     * @param firstIndex
     * @param maxResults
     * @param jpql
     * @param obj
     * @return
     */
    public List<T> findPage(Integer firstIndex, Integer maxResults,String jpql,Object... obj);
 
 
    public Page<?> findPage(Page<?> page,String jpql, Object... obj);
 
}