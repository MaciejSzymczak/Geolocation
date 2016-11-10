import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class GetSignedString {
	
	  public String getSignedString (String urlString, String keyString) throws IOException,
	    InvalidKeyException, NoSuchAlgorithmException {

		// This variable stores the binary key, which is computed from the string (Base64) key
		byte[] key;

		// Convert the string to a URL so we can parse it
	    URL url = new URL(urlString);

	    keyString = keyString.replace('-', '+');
	    keyString = keyString.replace('_', '/');
	    key = Base64.decodeBase64(keyString);

	    String request = signRequest(url.getPath(),url.getQuery(), key);
	    return url.getProtocol() + "://" + url.getHost() + request;
	  }

	  public String signRequest(String path, String query, byte[] key) throws NoSuchAlgorithmException,
	    InvalidKeyException {

	    // Retrieve the proper URL components to sign
	    String resource = path + '?' + query;

	    // Get an HMAC-SHA1 signing key from the raw key bytes
	    SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

	    // Get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
	    Mac mac = Mac.getInstance("HmacSHA1");
	    mac.init(sha1Key);

	    // compute the binary signature for the request
	    byte[] sigBytes = mac.doFinal(resource.getBytes());

	    // base 64 encode the binary signature
	    String signature = Base64.encodeBase64String(sigBytes);

	    // convert the signature to 'web safe' base 64
	    signature = signature.replace('+', '-');
	    signature = signature.replace('/', '_');

	    return resource + "&signature=" + signature;
	  }		
	
}
