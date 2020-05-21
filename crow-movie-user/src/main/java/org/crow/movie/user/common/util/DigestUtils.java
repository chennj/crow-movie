package org.crow.movie.user.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

public class DigestUtils {

	public static final String AUTH_KEY="";
	
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
	
	public static String encryptPwd(String password){
		
		password = password
				.replaceAll("/", "_a")
				.replaceAll("\\+", "_b")
				.replaceAll("\\=", "_c");
		return authCode(password,"ENCODE","",0);
	}
	
	public static String authCode(String data, String operation, String key, int expiry){
		
		int ckeyLen = 4;
		key = encryptMd5(StrUtil.isEmpty(key)?AUTH_KEY:key);
		String keya = encryptMd5(key.substring(0,16));
		String keyb = encryptMd5(key.substring(16,32));
		String keyc = ckeyLen>0 ? ("DECODE".equals(operation) ? Php2JavaUtil.substr(data, 0, ckeyLen) : String.valueOf(System.currentTimeMillis()).substring(String.valueOf(System.currentTimeMillis()).length()-ckeyLen)) : "";
		String cryptkey = keya + encryptMd5(keya+keyc);
		int keyLen = cryptkey.length();
		data = "DECODE".equals(operation) ? new String(Base64.getDecoder().decode(data.substring(0, ckeyLen))) :
			System.out.printf("%010d", expiry>0 ? expiry + new Date().getTime() : 0)
			+ encryptMd5(data+keyb).substring(0,16) + data;
		
		int dataLen = data.length();
		String result = "";
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
		for(a = j = i = 0; i < dataLen; i++) {
			a = (a + 1) % 256;
			j = (j + box[a]) % 256;
			int tmp = box[a];
			box[a] = box[j];
			box[j] = tmp;
			result += Php2JavaUtil.chr(Php2JavaUtil.ord(data.charAt(i)) ^ (box[(box[a] + box[j]) % 256]));
		}
		
		if ("DECODE".equals(operation)) {
			if ((StrUtil.isEmpty(Php2JavaUtil.substr(result, 0, 10)) || Php2JavaUtil.sumStrAscii(Php2JavaUtil.substr(result, 0, 10)) - System.currentTimeMillis() > 0) &&
					Php2JavaUtil.substr(result, 10, 16).equals(Php2JavaUtil.substr(encryptMd5(Php2JavaUtil.substr(result, 26)+ keyb), 0, 16))) {
				return Php2JavaUtil.substr(result, 26);
			} else {
				return "";
			}
		} else {
			return keyc + new String(Base64.getEncoder().encode(result.getBytes())).replaceAll("=", "");
		}
	}
	
	public static void main(String[] args){
		System.out.println("php encode password："+encryptPwd("movie-chennj"));
	}
}
