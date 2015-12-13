package mo.pay.post_it.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

import mo.pay.post_it.defs.PasswordEncryptKey;

public class PinCodeUtil 
{
	public static final String TAG = "PinCodeUtil";
	
	public static String encodePINCode(String orgPIN)
	{
		 SecretKeySpec spec = new SecretKeySpec(selfKey(PasswordEncryptKey.ALBUM_ENCRYPT_KEY).getBytes(), "AES");
		 
		 Cipher cipher;
	     try 
	     {
	    	 cipher = Cipher.getInstance("AES");
	         cipher.init(Cipher.ENCRYPT_MODE, spec);
	         return Base64.encodeToString(cipher.doFinal(orgPIN.getBytes()), android.util.Base64.NO_WRAP);
	     }catch (Exception e) {Log.e(TAG, e.toString());}
		
		return orgPIN;
	}
	
	public static String decodePINCode(String encodePIN)
	{
		SecretKeySpec spec = new SecretKeySpec(selfKey(PasswordEncryptKey.ALBUM_ENCRYPT_KEY).getBytes(), "AES");
	    Cipher cipher;
	       
	    try 
	    {
	    	cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.DECRYPT_MODE, spec);
	        return new String( cipher.doFinal(Base64.decode(encodePIN, android.util.Base64.NO_WRAP)) );
	     }catch (Exception e) {Log.e(TAG, e.toString());}
		
		return encodePIN;
	}
	
	
	public static String selfKey(String key) 
	{
	       String k = new String(key);
	       int length = k.length();
	       if( length < 16 ) {
	              for( int i=length ;i<16; ++i )
	                     k += i%10;
	              return k;
	       } else if ( length < 24 ) {
	              for( int i=length ;i<24; ++i )
	                     k += i%10;
	              return k;
	       } else if ( length < 32 ) {
	              for( int i=length ;i<32; ++i )
	                     k += i%10;
	              return k;
	       }
	       return key.substring(0, 32);
	}
	
}
