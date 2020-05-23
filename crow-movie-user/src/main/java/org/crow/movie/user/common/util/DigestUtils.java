package org.crow.movie.user.common.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DigestUtils {

	public static final String AUTH_KEY=StringUtils.EMPTY;
	
	public static String encryptMd5(String content){
		
		String md5 = null;
		
		if(null == content) return null;
		
		try {
			
		//Create MessageDigest object for MD5
		MessageDigest digest = MessageDigest.getInstance("MD5");
		
		//Update input string in message digest
		digest.update(content.getBytes(), 0, content.length());

		//Converts message digest value in base 16 (hex) 
		md5 = new BigInteger(1, digest.digest()).toString(16);

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}
		return md5;
	}
	
	public static String decryptPwd(String password) throws UnsupportedEncodingException{
		
		password = password
				.replaceAll("_a", "/")
				.replaceAll("_b", "+")
				.replaceAll("_c", "=");
		return authCode(password,"DECODE","",0);
	}
	
	public static String encryptPwd(String password) throws UnsupportedEncodingException{
		
		password = authCode(password,"ENCODE","",0);
		password = password
				.replaceAll("/","_a")
				.replaceAll( "/","_b")
				.replaceAll("=","_c");
		
		return password;
	}
	
	public static String authCode(String data, String operation, String key, int expiry) throws UnsupportedEncodingException{
		
		byte[] chrs = null;
		int ckeyLen = 4;
		key = encryptMd5(StrUtil.isEmpty(key)?AUTH_KEY:key);
		if (StrUtil.isEmpty(key))return "no data";
		System.out.println("null key："+key);
		String keya = encryptMd5(key.substring(0,16));
		String keyb = encryptMd5(key.substring(16,32));
		String keyc = ckeyLen>0 ? ("DECODE".equals(operation) ? Php2JavaUtil.substr(data, 0, ckeyLen) : String.valueOf(System.currentTimeMillis()).substring(String.valueOf(System.currentTimeMillis()).length()-ckeyLen)) : "";
		String cryptkey = keya + encryptMd5(keya+keyc);
		int keyLen = cryptkey.length();
		if ("DECODE".equals(operation)){
			//System.out.println(data.substring(4));
			chrs = Base64.decodeBase64(data.substring(4).getBytes());
		} else {
			data = System.out.printf(
					"%010d", 
					expiry>0 ? 
							expiry + new Date().getTime() 
							: 0)
					+ encryptMd5(data+keyb).substring(0,16) + data;
		}
		
		int dataLen = chrs.length;
		System.out.println("string len="+dataLen);
		StringBuilder result = new StringBuilder();
		int[] box = Php2JavaUtil.range(0, 256, 1);
		int[] rndkey = new int[256];
		for (int i = 0; i <= 255; i++) {
			rndkey[i] = Php2JavaUtil.ord(cryptkey.charAt(i % keyLen));
		}
		int i,j,a;
		for(j = i = 0; i < 256; i++) {
			j = (j + box[i] + rndkey[i]) % 256;
			int tmp = box[i];
			box[i] = box[j];
			box[j] = tmp;
		}
		char[] bs = new char[dataLen];
		for(a = j = i = 0; i < dataLen; i++) {
			a = (a + 1) % 256;
			j = (j + box[a]) % 256;
			int tmp = box[a];
			box[a] = box[j];
			box[j] = tmp;
			if ("DECODE".equals(operation)){
				
				//转换为无符号数
				int ich = chrs[i];
				ich &= 0xff;
				
				bs[i] = Php2JavaUtil.chr(ich ^ (box[(box[a] + box[j]) % 256]));
				result.append(bs[i]);
				//System.out.println(ich +"^"+ (box[(box[a] + box[j]) % 256])+"=>"+result);
			} else {
				result.append(Php2JavaUtil.chr(Php2JavaUtil.ord(data.charAt(i)) ^ (box[(box[a] + box[j]) % 256])));
			}
		}

		if ("DECODE".equals(operation)) {
			//System.out.println(Php2JavaUtil.substr(result.toString(), 10, 16));
			//System.out.println(Php2JavaUtil.substr(encryptMd5(Php2JavaUtil.substr(result.toString(), 26)+ keyb), 0, 16));
			if ((Integer.valueOf(Php2JavaUtil.substr(result.toString(), 0, 10)) == 0 || Php2JavaUtil.sumStrAscii(Php2JavaUtil.substr(result.toString(), 0, 10)) - System.currentTimeMillis() > 0) &&
					Php2JavaUtil.substr(result.toString(), 10, 16).equals(Php2JavaUtil.substr(encryptMd5(Php2JavaUtil.substr(result.toString(), 26)+ keyb), 0, 16))) {
				return Php2JavaUtil.substr(result.toString(), 26);
			} else {
				return "";
			}
		} else {
			return keyc + new String(Base64.encodeBase64(result.toString().getBytes())).replaceAll("=", "");
		}
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException{
		//System.out.println("md5(movie-chennj)："+encryptMd5("movie-chennj"));
		//System.out.println("php decode password："+decryptPwd("d059RWJkzOsKLrA_bwNJ4MXftsiJVEybyg_avtrI1Vzt_at4HhV1JSq7L0"));
		//System.out.println(Php2JavaUtil.chr(48));
//		System.out.println("string replace："+
//				"d059RWJkzOsKLrA_bwNJ4MXftsiJVEybyg_avtrI1Vzt_at4HhV1JSq7L0"
//				.replaceAll("_a", "/")
//				.replaceAll("_b", "+")
//				.replaceAll("_c", "="));
		
		//System.out.println("base 64 encode"+Base64.getDecoder().decode("d059RWJkzOsKLrA_bwNJ4MXftsiJVEybyg_avtrI1Vzt_at4HhV1JSq7L0".substring(0, 4)));
		//System.out.println("base64 encode："+Base64.encodeBase64String("chennj123!$%^-fdd".getBytes()));
		//System.out.println("php decode password："+encryptPwd("movie-chennj"));
		//System.out.println("1 xor 2："+(1^2));
	}
}
