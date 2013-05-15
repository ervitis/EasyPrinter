
package com.easyprinter;

import android.util.Log;
import java.io.*;
import java.security.MessageDigest;

public class CheckSum {
	public static byte[] createCheckSum(File file) throws Exception{
		byte[] buffer = new byte[8192];
		int n;
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		
		try{
			MessageDigest complete = MessageDigest.getInstance("MD5");
			
			n = 0;
			while ( n < file.length() ){
				n += bufferedInputStream.read(buffer);
				if ( n > 0 ){
					complete.update(buffer, 0, n);
				}
			}
			
			bufferedInputStream.close();
			return complete.digest();
		}catch(Exception ex){
			Log.e("createCheckSum", ex.getMessage());
			return null;
		}
	}
	
	public static String getMD5CheckSum(File file) throws Exception{
		byte[] b = createCheckSum(file);
		
		String result = "";
		
		for(int i=0;i<b.length;i++){
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		
		return result;
	}
}
