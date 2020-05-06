package org.crow.movie.user.common.db;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.crow.movie.user.common.db.model.NVPair;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;


@Repository
//public abstract class AbstractBaseDao<T> extends HibernateDaoSupport implements BaseDao<T> {
public abstract class AbstractBaseDao<T> implements BaseDao<T> {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/*-----------------------------使用 HibernateTemplate---------------------------*/
	/*
	@Resource(name="sessionFactory") 
    public void setSessionFactoryOverride(SessionFactory sessionFactory)
    { 
		super.setSessionFactory(sessionFactory); 
    }
	
	@Override
	public Session session() {
		
		//return this.currentSession();
		return this.getHibernateTemplate().getSessionFactory().getCurrentSession();
	}

	@Override
	public HibernateTemplate getTemplate()
	{
		return this.getHibernateTemplate();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findBySql(String Sql) throws Exception {
		// TODO Auto-generated method stub
		try{
			return session().createNativeQuery(Sql).list();
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}*/
	
	/*-----------------------------使用 EntityManager---------------------------*/
	/**
	 * 已经采Hibernate Session 的项目如何获得EM对象？
	 * hibernateTemplate.getSessionFactory().createEntityManager();
	 */
	
	/*-----------------------------base tool------------------------------------*/
	
    @PersistenceContext
    private EntityManager em;
 
    private Class<T> clazz = null;
 
    @SuppressWarnings("unchecked")
	public AbstractBaseDao(){
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.clazz = (Class<T>) pt.getActualTypeArguments()[0];
    }
 
 
    @Override
    public T add(T t) {
        em.persist(t);
        return t;
    }
 
    @Override
    public T update(T t) {
        return em.merge(t);
    }
 
 
    @Override
    public void delete(Serializable id) {
        T t = em.getReference(clazz, id);
        em.remove(t);
    }
 
    @Override
    public int executeUpdate(String jpql, Object... obj) {
        Query query = em.createQuery(jpql);
        if(obj.length > 0){
            for (int i = 0; i < obj.length; i++) {
                query.setParameter((i+1),obj[i]);
            }
        }
        return query.executeUpdate();
    }
 
    @Override
    public T load(Serializable id) {
        return em.find(clazz, id);
    }
 
    @SuppressWarnings("unchecked")
	@Override
    public T load(String jpql, Object... obj) {
        try{
            Query query = em.createQuery(jpql);
            if(obj.length > 0){
                for (int i = 0; i < obj.length; i++) {
                    query.setParameter((i+1),obj[i]);
                }
            }
            return (T) query.getSingleResult();
        }catch (Exception e){
          return null;
        }
    }
 
    @SuppressWarnings("unchecked")
	@Override
    public List<T> findAll() {
        return em.createQuery("from "+clazz.getSimpleName()).getResultList();
    }
 
    @SuppressWarnings("unchecked")
	@Override
    public List<T> find(String jpql, Object... obj) {
        try{
            Query query = em.createQuery(jpql);
            if(obj.length > 0){
                for (int i = 0; i < obj.length; i++) {
                    query.setParameter((i+1),obj[i]);
                }
            }
           return query.getResultList();
        }catch (Exception e){
            return null;
        }
    }
 
    @Override
    public Object findByAggregate(String jpql, Object... obj) {
        Query query = em.createQuery(jpql);
        if(obj.length > 0){
            for (int i = 0; i < obj.length; i++) {
                query.setParameter((i+1),obj[i]);
            }
        }
        Object result = query.getSingleResult();
        return result;
    }
 
    @Override
    public int count() {
        Long num = (Long) em.createQuery("select count(1) from "+clazz.getSimpleName()).getSingleResult();
        return num.intValue();
    }
 
    @Override
    public int count(String jpql,Object... obj) {
        try{
            Query query = em.createQuery(jpql);
            if(obj.length > 0){
                for (int i = 0; i < obj.length; i++) {
                    query.setParameter((i+1),obj[i]);
                }
            }
            Long num = (Long)query.getSingleResult();
            return num.intValue();
        }catch (Exception e){
            return 0;
        }
    }
 
    @SuppressWarnings("unchecked")
	@Override
    public List<T> findPage(Integer firstIndex, Integer maxResults) {
        return em.createQuery("from "+clazz.getSimpleName()).setFirstResult(firstIndex).setMaxResults(maxResults).getResultList();
    }
 
    @SuppressWarnings("unchecked")
	@Override
    public List<T> findPage(Integer firstIndex, Integer maxResults,String jpql,Object... obj) {
        try{
            Query query = em.createQuery(jpql);
            if(obj.length > 0){
                for (int i = 0; i < obj.length; i++) {
                    query.setParameter((i+1),obj[i]);
                }
            }
            query.setFirstResult(firstIndex).setMaxResults(maxResults);
            return query.getResultList();
        }catch (Exception e){
            return null;
        }
 
    }
 
    @SuppressWarnings("unchecked")
	@Override
    public Page<T> findPage(@SuppressWarnings("rawtypes") Page page, String jpql, Object... obj) {
        Query query = em.createQuery(jpql);
        if(obj.length > 0){
            for (int i = 0; i < obj.length; i++) {
                query.setParameter((i+1),obj[i]);
            }
        }
        int total = ((Long)query.getSingleResult()).intValue();
        int pageIndex = page.getPage();
        page.init(pageIndex, total);
        
        query.setFirstResult(page.getLimit()).setMaxResults(page.getPageSize());
        List<T> list = query.getResultList();
        page.setResults(list);
        return page;
    }
 
    /*-----------------------------extends tool------------------------------------*/
    
    private final static int BATCH_SIZE = 20;
    
    public T get(String id){
    	return (T)em.getReference(clazz, id);
    }
    
    @SuppressWarnings("unchecked")
	protected <V> List<T> findList(
    		String order, 
			Map<String, Object> eq, Map<String, Object> not,
			Map<String, Object> like, Map<String, Object> notlike,
			Map<String, Object> leftlike, Map<String, Object> rightlike,
			Map<String, List<V>> in, Map<String, List<V>> notin,
			Map<String, List<Object>> between,Map<String, List<Object>> notbetween,
			List<NVPair> custCondition){

		String hql = "from " + clazz.getName() + " where 1 = 1";
		
		Query query = buildQuery(hql, order,eq,not,like,notlike,leftlike,rightlike,in,notin,between,notbetween,custCondition);
		
		return query.getResultList();
    }
    
	protected Query createQuery(final String queryString, final Map<String,Object> params) {
		Assert.hasText(queryString,"Query String Is Empty");
		Query query = em.createQuery(queryString);
		if (params != null) {
			for (String key : params.keySet()){
				query.setParameter(key, params.get(key));
			}
		}
		return query;
	}

	protected <V> Query buildQuery(
			String hql, String order, 
			Map<String, Object> eq, Map<String, Object> not,
			Map<String, Object> like, Map<String, Object> notlike,
			Map<String, Object> leftlike, Map<String, Object> rightlike,
			Map<String, List<V>> in, Map<String, List<V>> notin,
			Map<String, List<Object>> between,Map<String, List<Object>> notbetween,
			List<NVPair> custCondition){
		
		int idx = 0;
		Map<String,Object> map = new HashMap<String, Object>();
		if (null != eq && eq.size()>0){
			idx = 0;
			for (String key : eq.keySet()) {
	
				hql += " and " + key + " = :eq"+idx;
				map.put("eq"+idx, eq.get(key));
				idx++;
			}
		}

		if (null != not && not.size()>0){
			idx = 0;
			for (String key : not.keySet()) {
	
				hql += " and " + key + " != :not"+idx;
				map.put("not"+idx, not.get(key));
				idx++;
			}
		}

		if (null != like && like.size()>0){
			idx = 0;
			for (String key : like.keySet()) {
	
				hql += " and " + key + " like :lk"+idx;
				map.put("lk"+idx, "%"+like.get(key)+"%");
				idx++;
			}
		}

		if (null != notlike && notlike.size()>0){
			idx = 0;
			for (String key : notlike.keySet()) {
	
				hql += " and " + key + " not like :notlk"+idx;
				map.put("notlk"+idx, "%"+notlike.get(key)+"%");
				idx++;
			}
		}
		if (null != leftlike && leftlike.size()>0){
			idx = 0;
			for (String key : leftlike.keySet()) {
	
				hql += " and " + key + " like :llk"+idx;
				map.put("llk"+idx, leftlike.get(key)+"%");
				idx++;
			}
		}
		if (null != rightlike && rightlike.size()>0){
			idx = 0;
			for (String key : rightlike.keySet()) {
	
				hql += " and " + key + " like :rlk"+idx;
				map.put("rlk"+idx, "%"+rightlike.get(key));
				idx++;
			}
		}
		
		if (null != in && in.size()>0){
			idx = 0;
			for (String key : in.keySet()) {
	
				hql += " and " + key + " in( :in"+idx+" )";
				map.put("in"+idx, in.get(key));
				idx++;
			}
		}
		
		if (null != notin && notin.size()>0){
			idx = 0;
			for (String key : notin.keySet()) {
	
				hql += " and " + key + " not in( :notin"+idx+" )";
				map.put("notin"+idx, notin.get(key));
				idx++;
			}
		}
		
		if (null != between && between.size()>0){
			idx = 0;
			for (String key : between.keySet()) {
				
				hql += " and " + key + " >= :start"+idx;
				hql += " and " + key + " < :end"+idx;
				map.put("start"+idx, between.get(key).get(0));
				map.put("end"+idx, between.get(key).get(1));
				idx++;
			}
		}

		if (null != notbetween && notbetween.size()>0){
			idx = 0;
			for (String key : notbetween.keySet()) {
				
				hql += " and " + key + " < :notstart"+idx;
				hql += " and " + key + " > :notend"+idx;
				map.put("notstart"+idx, notbetween.get(key).get(0));
				map.put("notend"+idx, notbetween.get(key).get(1));
				idx++;
			}
		}
		
		if (null != custCondition && custCondition.size() > 0){
			idx = 0;
			for (NVPair pair : custCondition){
				
				if (null == pair.getValue() ){
					hql += " and " + pair.getName() + " " + pair.getOp() + " null ";
				} else {
					hql += " and " + pair.getName() + " " + pair.getOp() + " :custome"+idx;
					map.put("custome"+idx, pair.getValue());
				}
				idx++;
			}
		}
		
		hql += StringUtils.isEmpty(order) ? "" : " order by " + order;

		logger.info("hql>>>"+hql);
		Query query = createQuery(hql, map);
		
		return query;
	}
	
	@SuppressWarnings("unchecked")
	public <V>T findUnique( 
			Map<String, Object> eq, Map<String, Object> not,
			Map<String, Object> like, Map<String, Object> notlike,
			Map<String, Object> leftlike, Map<String, Object> rightlike,
			Map<String, List<V>> in, Map<String, List<V>> notin,
			Map<String, List<Object>> between,Map<String, List<Object>> notbetween,
			List<NVPair> custCondition) {
		
		String hql = "from " + clazz.getName() + " where 1 = 1";
		
		Query query = buildQuery(hql, "" ,eq,not,like,notlike,leftlike,rightlike,in,notin,between,notbetween,custCondition);

		Object o = query.getSingleResult();
		
		return (T) o;
	}

	public <V>Long findCount( 
			Map<String, Object> eq, Map<String, Object> not,
			Map<String, Object> like, Map<String, Object> notlike,
			Map<String, Object> leftlike, Map<String, Object> rightlike,
			Map<String, List<V>> in, Map<String, List<V>> notin,
			Map<String, List<Object>> between,Map<String, List<Object>> notbetween,
			List<NVPair> custCondition) {
		
		String hql = "select count(1) from " + clazz.getName() + " where 1 = 1 ";
		
		Query query = buildQuery(hql, "" ,eq,not,like,notlike,leftlike,rightlike,in,notin,between,notbetween,custCondition);

		Object o = query.getSingleResult();
		
		return (Long) o;
	}
	
	public <V> Page<T> findPage(
			int pgIndex,
			int pgSize,
			String order, 
			Map<String, Object> eq, Map<String, Object> not,
			Map<String, Object> like, Map<String, Object> notlike,
			Map<String, Object> leftlike, Map<String, Object> rightlike,
			Map<String, List<V>> in, Map<String, List<V>> notin,
			Map<String, List<Object>> between,Map<String, List<Object>> notbetween,
			List<NVPair> custCondition){				

		String hql = "from " + clazz.getName() + " where 1 = 1";
		
		Query query = buildQuery(hql, order,eq,not,like,notlike,leftlike,rightlike,in,notin,between,notbetween,custCondition);

		int total =  findCount(eq,not,like,notlike,leftlike,rightlike,in,notin,between,notbetween,custCondition).intValue();

		Page<T> page = new Page<T>();
		page.setPage(pgIndex);
		page.setPageSize(pgSize);
		page.init(pgIndex, total);

		@SuppressWarnings("unchecked")
		List<T> list = query.setFirstResult(page.getLimit())
				.setMaxResults(page.getPageSize()).getResultList();
		page.setResults(list);

		return page;
	}
	
	public Page<?> findPage(String nativeSql, int pgIndex, int pgSize, Object...params){
		
		Query query = em.createNativeQuery(nativeSql);
		
		int total = ((Long)query.getSingleResult()).intValue();
		
		Page<T> page = new Page<T>();
		page.setPage(pgIndex);
		page.setPageSize(pgSize);
		page.init(pgIndex, total);
		
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> mapResult = 
				query.unwrap(NativeQueryImpl.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.setFirstResult(page.getLimit())
				.setMaxResults(page.getPageSize())
				.list();
		
		page.setMapResult(mapResult);
		
		return page;
	}
	
	public void batchInsert(List<T> list){
		
		int size = list.size();
		//em.setFlushMode(FlushModeType.COMMIT);
		//em.getTransaction().begin();
		if (size < BATCH_SIZE){
			for (int i=0; i<size; i++){
				em.persist(list.get(i));
			}
			em.flush();
			em.clear();
		} else {
			for (int i=0; i<size; i++){
				em.persist(list.get(i));
				if (i % BATCH_SIZE == 0 && i > 0){
					em.flush();
					em.clear();
				}
			}
			
			//em.getTransaction().commit();
			//em.close();
		}
	}
	
	public void batchUpdate(List<T> list){
		
		int size = list.size();
		//em.setFlushMode(FlushModeType.COMMIT);
		//em.getTransaction().begin();
		for (int i=0; i<size; i++){
			T t = em.merge(list.get(i));
			em.persist(t);
			if (i % BATCH_SIZE == 0 && i > 0) {
                em.flush();
                em.clear();
            }
		}
		//em.getTransaction().commit();
        //em.close();
	}
}