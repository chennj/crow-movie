package org.crow.movie.user.common.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
//import org.apache.commons.codec.binary.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
	
	public static String encryptPwd(String password){
		
		password = authCode(password,"ENCODE","",0);
		password = password
				.replaceAll("/","_a")
				.replaceAll("\\+","_b")
				.replaceAll("=","_c");
		
		return password;
	}
	
	public static String authCode(String data, String operation, String key, int expiry){
		
		byte[] chrs = null;
		int ckeyLen = 4;
		key = encryptMd5(StrUtil.isEmpty(key)?AUTH_KEY:key);
		String keya = encryptMd5(key.substring(0,16));
		String keyb = encryptMd5(key.substring(16,32));
		String keyc = ckeyLen>0 ? ("DECODE".equals(operation) ? Php2JavaUtil.substr(data, 0, ckeyLen) : Php2JavaUtil.substr(encryptMd5(Php2JavaUtil.microtime()),-ckeyLen)): "";
		//String keyc = "1234";
		String cryptkey = keya + encryptMd5(keya+keyc);
		int keyLen = cryptkey.length();
		if ("DECODE".equals(operation)){
			chrs = Base64.getDecoder().decode(data.substring(4).getBytes());
		} else {
			data = String.format(
					"%010d", 
					expiry>0 ? 
							expiry + new Date().getTime() 
							: 0)
					+ encryptMd5(data+keyb).substring(0,16) + data;
			chrs = data.getBytes();
		}
		
		int dataLen = chrs.length;
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
		char[] bsdecode = new char[dataLen];
		byte[] bsencode = new byte[dataLen];
		//System.out.println("==============");
		for(a = j = i = 0; i < dataLen; i++) {
			a = (a + 1) % 256;
			j = (j + box[a]) % 256;
			int tmp = box[a];
			box[a] = box[j];
			box[j] = tmp;
			
			//转换为无符号数
			int ich = chrs[i];
			ich &= 0xff;
			
			if ("DECODE".equals(operation)){
				bsdecode[i] = Php2JavaUtil.chr(ich ^ (box[(box[a] + box[j]) % 256]));
				result.append(bsdecode[i]);
			} else {
				bsencode[i] = (byte)(ich ^ (box[(box[a] + box[j]) % 256]));
			}
		}
		//System.out.println("==============");

		if ("DECODE".equals(operation)) {
			if ((Integer.valueOf(Php2JavaUtil.substr(result.toString(), 0, 10)) == 0 || Php2JavaUtil.sumStrAscii(Php2JavaUtil.substr(result.toString(), 0, 10)) - System.currentTimeMillis() > 0) &&
					Php2JavaUtil.substr(result.toString(), 10, 16).equals(Php2JavaUtil.substr(encryptMd5(Php2JavaUtil.substr(result.toString(), 26)+ keyb), 0, 16))) {
				return Php2JavaUtil.substr(result.toString(), 26);
			} else {
				return "";
			}
		} else {
			//return keyc + new String(Base64.encodeBase64(result.toString().getBytes())).replaceAll("=", "");
			return keyc + new String(Base64.getEncoder().encode(bsencode)).replaceAll("=", "");
		}
	}
	
	public static String random(Object...objects){
		
		int length = 6;
		String type = "string";
		
		if (objects.length == 1){
			length = CommUtil.o2i(objects[0]);
		}
		
		if (objects.length == 2){
			length = CommUtil.o2i(objects[0]);
			type = objects[1].toString();
		}
		
		Map<String, String> config = new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				this.put("number", "1234567890");
				this.put("lower", "abcdefghijklmnopqrstuvwxyz");
				this.put("upper", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
				this.put("upper_number", "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
				this.put("letter", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
				this.put("string", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
			}
		};
		
		String str = config.get(type);
		StringBuilder code = new StringBuilder();
		Random rand = new Random();
		int len = str.length() - 1;
		for (int i = 0; i < length; i++) {
			code.append(str.charAt(rand.nextInt(len)));
		}
		
		return code.toString();
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException{
		System.out.println("php encode password：\n"+encryptPwd("qqqqqq"));
		System.out.println("php decode password：\n"+decryptPwd("1b10EyyyC_bqBy2P41l6BVZzWL_bTdJyb22uOnNaEKUtxTxWg"));

		//System.out.println(Php2JavaUtil.chr(48));
		
//		System.out.println("string replace："+
//				"d059RWJkzOsKLrA_bwNJ4MXftsiJVEybyg_avtrI1Vzt_at4HhV1JSq7L0"
//				.replaceAll("_a", "/")
//				.replaceAll("_b", "+")
//				.replaceAll("_c", "="));
		
//		String sec = String.valueOf(System.currentTimeMillis() / 1000);
//		String non = String.valueOf(System.nanoTime()).substring(0,8);		
//		String microtime = "0." + non + " " + sec;
//		System.out.println("micro sec: "+microtime);
		
		//System.out.println("base 64 encode"+Base64.getDecoder().decode("d059RWJkzOsKLrA_bwNJ4MXftsiJVEybyg_avtrI1Vzt_at4HhV1JSq7L0".substring(0, 4)));
		//System.out.println("base64 encode："+Base64.encodeBase64String("chennj123!$%^-fdd".getBytes()));
		//System.out.println("php decode password："+encryptPwd("movie-chennj"));
		//System.out.println("1 xor 2："+(1^2));
	}
}
