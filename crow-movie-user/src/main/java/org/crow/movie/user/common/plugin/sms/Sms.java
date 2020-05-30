package org.crow.movie.user.common.plugin.sms;

import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

public class Sms {

    private static final String SMS_Url = "http://smssh1.253.com/msg/send/json";
    
    private static final String SMS_ACCOUNT = "N7406260";
    
    private static final String SMS_KEY = "O9GZWJHiM13181";


    /**
     * @param Uid SMS用户id  ： lvfang123
     * @param Key 接口秘钥：SMS登录可查（非登录密码）
     * @param sendPhoneNum 短信发送目标号码
     * @param desc 短信内容
     * @return Integer(1：成功码，其他失败，具体参见注释)
     */
    public static String send(String mobile, String content){

    	RestTemplate client = new RestTemplate(); 
        HttpHeaders headers = new HttpHeaders();
        //headers.add("Content-Type","application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //设置参数
        JSONObject jo = new JSONObject();
        jo.put("account", 	SMS_ACCOUNT);
        jo.put("key", 		SMS_KEY);//秘钥
        jo.put("msg", 		content);
        jo.put("phone", 	mobile);
        jo.put("report", 	true);

        try {
	        HttpEntity<String> entity = new HttpEntity<>(jo.toString(), headers);
	        ResponseEntity<JSONObject> exchange = client.exchange(
	        		SMS_Url, 
	        		HttpMethod.POST, 
	        		entity, 
	        		JSONObject.class);
	        String result = new String(exchange.getBody().getBytes("utf-8"));
	        return result;
        } catch (Exception e){
        	throw new RuntimeException("短信发送失败："+e.getMessage());
        }

    }
    /**
     *  -1  没有该用户账户
     -2 接口密钥不正确 [查看密钥]不是账户登陆密码
     -21    MD5接口密钥加密不正确
     -3 短信数量不足
     -11    该用户被禁用
     -14    短信内容出现非法字符
     -4 手机号格式不正确
     -41    手机号码为空
     -42    短信内容为空
     -51    短信签名格式不正确接口签名格式为：【签名内容】
     -6 IP限制
	     大于0    短信发送数量
	     以上作为补充
     */
    public static String getMessage(String code){
        String message = "";
        
        return message;
    }

    /**
     * 随机生成6位验证码
     * @return
     */
    public static String getRandomCode(Integer code){
        Random random = new Random();
        StringBuffer result= new StringBuffer();
        for (int i=0;i<code;i++){
            result.append(random.nextInt(10));
        }
        return result.toString();
    }
}
