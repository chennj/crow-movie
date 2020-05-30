package org.crow.movie.user;

import java.io.Serializable;

import javax.annotation.Resource;

import org.crow.movie.user.common.plugin.redis.RedisService;
import org.crow.movie.user.model.User_t;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试redis
 * @author chenn
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

	private Logger logger = LoggerFactory.getLogger(RedisTest.class);
	
	@Resource
    private RedisTemplate<String, Serializable> redisTemplate;
	
	@Resource
	private RedisService redisService;

    @Test
    public void test11() {
        String key = "user:11";
        redisTemplate.opsForValue().set(key, new User_t(1,"pjmike",20));
        User_t user = (User_t) redisTemplate.opsForValue().get(key);
        logger.info("redis test11 >>> uesr: "+user.toString());
    }
    
    @Test
    public void test12() {
        String key = "user:12";
        redisService.set(key, "{id:1,age:20,username:pjmike}");
        Object user = redisService.get(key);
        logger.info("redis test12 >>> uesr: "+user);
    }
    
    @Test
    public void test21() {
        String key = "user:21";
        redisService.set(key, new User_t(1,"pjmike",20));
        User_t user = (User_t) redisService.get(key);
        logger.info("redis test21 >>> uesr: "+user.toString());
    }
    
    @Test
    public void test22() {
        String key = "user:22";
        redisService.set(key, "{id:1,age:20,username:pjmike}");
        Object user = redisService.get(key);
        logger.info("redis test21 >>> uesr: "+user);
    }
}
