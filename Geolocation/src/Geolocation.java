import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

/**
 * Batch geololocation
 * 
 * @author Maciej Szymczak
 */
public class Geolocation  {
	
	public static void main(String[] args) throws  IOException, InterruptedException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, XPathExpressionException, ParserConfigurationException, SAXException
	   {
	    String sourceFile        = "C:\\Interfaces\\GeolocationUpdate\\data\\AccountsIn.csv";
	    String destFileLatLon    = "C:Interfaces\\GeolocationUpdate\\data\\AccountsOut.csv";
	    String destFileOverQuery = "C:Interfaces\\GeolocationUpdate\\data\\AccountsQverLimit.csv";
	    String client            = "gme-<your client id>";
		String keyString         = "<your keystring>";
	    	    
		if (args.length>0) {
		     sourceFile        = args[0];
		     destFileLatLon    = args[1];
		     destFileOverQuery = args[2];
		     client            = args[3];
		     keyString         = args[4];
		};
			
	        new Geolocation().readcsv(sourceFile, destFileLatLon, destFileOverQuery, client, keyString);
	   } 
	 
		private static FileReader fr;
		
		
		private static String replaceCharAt(String s, int i, char c) {
	        StringBuffer buf = new StringBuffer(s);
	        buf.setCharAt(i, c);
	        return buf.toString();
	    }
		
		public static String replaceNestedCommas(String a) {
			int quotaCnt = 0;
			
			int x=0;
			while (x< a.length()){
				//char34="
				if (a.charAt(x)==(char)34) {
					quotaCnt++;
				}
				//char44=,
				if (quotaCnt%2==1 && a.charAt(x)==(char)44) {
					//char94=^
					a = replaceCharAt(a,x,(char)94);
				}
				//System.out.println( a.charAt(x) + " " + quotaCnt%2);
				x++;
			}
			//System.out.println( a);
			return a;		
		}		
		
		public void readcsv(String sourceFile, String destFileLatLon, String destFileOverQuery, String client, String keyString) throws IOException, InterruptedException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, XPathExpressionException, ParserConfigurationException, SAXException {
			Integer phisicalLineNo=0;
			Integer recordNo=0;
		
			FileWriter writerLatLon = new FileWriter(destFileLatLon);
			FileWriter writerOverQuery = new FileWriter(destFileOverQuery);
			writerLatLon.append("Id,Geocoding_status__c,Lat__c,Lon__c,ShippingLatitude,ShippingLongitude\n");
			writerOverQuery.append("Id,Geocoding_status__c\n");
			
			try {
				//id,shippingstreet,shippingcity,shippingstate,shippingpostalcode,shippingcountry
				fr = new FileReader(sourceFile);
				BufferedReader br = new BufferedReader(fr);
				String phisicalLine;
				String recordLine;
				String accountId;
				String shippingstreet;
				String shippingcity;
				String shippingstate;
				String shippingpostalcode;
				String shippingcountry;
				String concatenatedAddress;
				while( (phisicalLine = br.readLine()) != null) {
					phisicalLineNo++;
					recordLine = phisicalLine;
					//System.out.println( "recordLine="+recordLine);	
					//System.out.println( "recordLine.length()="+recordLine.length()+"");	
					//System.out.println( "2="+recordLine.replace("\"","").length()+"");	
					//System.out.println( "calc="+(recordLine.length() - recordLine.replace("\"","").length()) % 2+"");	
					//process CRLFs
					
					
					while ( !((recordLine.length() - recordLine.replace("\"","").length()) % 2 == 0) ) {
						phisicalLine = br.readLine();
						phisicalLineNo++;
						recordLine = recordLine +" "+ phisicalLine;						
						//System.out.println( "recordLine="+recordLine);	
						//System.out.println( "recordLine.length()="+recordLine.length()+"");	
						//System.out.println( "2="+recordLine.replace("\"","").length()+"");	
					}
					
					recordLine = replaceNestedCommas (recordLine);
				    StringTokenizer st1 = new StringTokenizer(recordLine, ",");

				    //if (st1.hasMoreTokens()) {accountId = st1.nextToken();};
					accountId           = st1.nextToken().replace("\"", "").replace("^",",");
					shippingstreet      = st1.nextToken().replace("\"", "").replace("^",",");
					shippingcity        = st1.nextToken().replace("\"", "").replace("^",",");
					shippingstate       = st1.nextToken().replace("\"", "").replace("^",",");
					shippingpostalcode  = st1.nextToken().replace("\"", "").replace("^",",");
					shippingcountry     = st1.nextToken().replace("\"", "").replace("^",",");
				
					// VC Jnj specific {
					if (shippingcountry.equals("FR")) shippingcountry = "France";
					if (shippingcountry.equals("ES")) shippingcountry = "Spain";
					if (shippingcountry.equals("PT")) shippingcountry = "Portugal";
					
					// Sunrise specific			
					if (shippingcountry.equals("Poland")) 
						shippingpostalcode = shippingpostalcode.replace("X", " ");
					// }
					
					concatenatedAddress = 
						//accountId +" "+
						shippingstreet +", "+
						shippingcity +", "+
						shippingstate +", "+
						shippingpostalcode +", "+
						shippingcountry;

				    concatenatedAddress = concatenatedAddress
					    .replace((char)9+"","+")
					    .replace((char)10+"","+")
					    .replace((char)13+"","+")
					    .replace("%","%25")
					    .replace(" ","+")
					    .replace(",","%2C")
					    .replace("<","%3C")
					    .replace(">","%3E")
					    .replace("#","%23")
					    .replace("{","%7B")
					    .replace("}","%7D")
					    .replace("|","%7C")
					    .replace("\\","%5C")
					    .replace("^","%5E")
					    .replace("~","%7E")
					    .replace("[","%5B")
					    .replace("]","%5D")
					    .replace("`","%60")
					    .replace(";","%3B")
					    .replace("/","%2F")
					    .replace("?","%3F")
					    .replace(":","%3A")
					    .replace("@","%40")
					    .replace("=","%3D")
					    .replace("&","%26")
					    .replace("$","%24");

					recordNo++;
				    System.out.print( "Line :" + (recordNo+"")+"("+(phisicalLineNo+"")+") content:" + concatenatedAddress+" ");				    
                    //System.out.print( concatenatedAddress );
				    
				    if (recordNo==1) {
				    	System.out.println( "Skipped" );
				    }
				    else {
					    LatLonStatus latLonStatus;
					    latLonStatus = getLatLonStatus(concatenatedAddress, client, keyString);
					    
					    if (latLonStatus.Status.equals("OVER_QUERY_LIMIT")) 
					    {
					    	Thread.sleep(1000);
					    	latLonStatus = getLatLonStatus(concatenatedAddress, client, keyString);				    	
					    }
					    
					    System.out.println( latLonStatus.Status +" "+ latLonStatus.Lat +" "+ latLonStatus.Lon );
					    if (latLonStatus.Status.equals("OVER_QUERY_LIMIT")) {
						    //Id,Geocoding_status__c
					    	writerOverQuery.append(accountId+","+latLonStatus.Status+"\n");	    	
					    }
					    else {
						    //Id,Geocoding_status__c,Lat__c,Lon__c,ShippingLatitude,ShippingLongitude
						    writerLatLon.append(accountId+","+latLonStatus.Status+","+latLonStatus.Lat+","+latLonStatus.Lon+","+latLonStatus.Lat+","+latLonStatus.Lon+"\n");
					    }
				    }				    
				}
				fr.close();
				writerOverQuery.flush();
				writerOverQuery.close();		
				writerLatLon.flush();
				writerLatLon.close();		
			    System.out.println("*** Done");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		/*
		public void readcsv(String sourceFile, String destFile, String Client, String keyString) throws ParseException, IOException, InterruptedException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, XPathExpressionException, ParserConfigurationException, SAXException {
			Integer i=0;
		
			FileWriter writer = new FileWriter(destFile);
		    writer.append("ID,Status,Lat,Lon\n");
			
			try {
				fr = new FileReader(sourceFile);
				BufferedReader br = new BufferedReader(fr);
				String s;
				String csv_id;
				String csv_name;
				String csv_address;
				while( (s = br.readLine()) != null) {
					i++;
				    System.out.print( "Line :" + (i+"")+" ");
				    StringTokenizer st1 = new StringTokenizer(s, (char)9+"");
				    csv_id="";
				    csv_name="";
				    csv_address="";
				    if (st1.hasMoreTokens()) {csv_id = st1.nextToken();};
				    if (st1.hasMoreTokens()) {csv_name = st1.nextToken();};
				    if (st1.hasMoreTokens()) {csv_address = st1.nextToken().replace(" ", "+");};
				    
				    //replace("%","%25"
				    //replace(" ","%20"
				    csv_address = csv_address.replace(",","%2C")
				    .replace("<","%3C")
				    .replace(">","%3E")
				    .replace("#","%23")
				    .replace("{","%7B")
				    .replace("}","%7D")
				    .replace("|","%7C")
				    .replace("\\","%5C")
				    .replace("^","%5E")
				    .replace("~","%7E")
				    .replace("[","%5B")
				    .replace("]","%5D")
				    .replace("`","%60")
				    .replace(";","%3B")
				    .replace("/","%2F")
				    .replace("?","%3F")
				    .replace(":","%3A")
				    .replace("@","%40")
				    .replace("=","%3D")
				    .replace("&","%26")
				    .replace("$","%24");
				    
				    LatLonStatus latLonStatus;
				    latLonStatus = getLatLonStatus(csv_address, Client, keyString);
				    
				    if (latLonStatus.Status.equals("OVER_QUERY_LIMIT")) 
				    {
				    	Thread.sleep(1000);
				    	latLonStatus = getLatLonStatus(csv_address, Client, keyString);				    	
				    }
				    
				    System.out.println( latLonStatus.Status +" "+ latLonStatus.Lat +" "+ latLonStatus.Lon );
				    writer.append(csv_id+","+latLonStatus.Status+","+latLonStatus.Lat+","+latLonStatus.Lon);
				    writer.append('\n');
				}
				fr.close();
			    writer.flush();
			    writer.close();		
			    System.out.println("*** Done");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		*/
			
	   private LatLonStatus getLatLonStatus(String address, String Client, String keyString) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, IOException, XPathExpressionException, ParserConfigurationException, SAXException{
	   
		    LatLonStatus res;
		    res = new LatLonStatus();
		    res.Lat="";
		    res.Lon="";

			HttpsURLConnection con=null;
			//API for business uses client and signature parameters, not key parameter
			//more: https://developers.google.com/maps/documentation/business/webservices/auth
			String https_url = "https://maps.googleapis.com/maps/api/geocode/xml?address="+address+"&client="+Client;
			
			GetSignedString ss = new GetSignedString();
		    https_url=ss.getSignedString(https_url, keyString);		
			
			//System.out.println("Request:"+https_url); //debug
			
		    URL url;
		    try {
			   url = new URL(https_url);
			   con = (HttpsURLConnection)url.openConnection();
		    } catch (MalformedURLException e) {
			   e.printStackTrace();
		    } catch (IOException e) {
			   //e.printStackTrace();
		       con=null; //error
		    }
			   
			   
			if(con!=null){
	
			    StringBuffer response = new StringBuffer();		
		 		try {			
				   BufferedReader br = 
					new BufferedReader(
						new InputStreamReader(con.getInputStream()));
			 
				   String input;
			 
				   while ((input = br.readLine()) != null){
					   response.append(input);
				   }
				   br.close();
				   
					//System.out.println("Response:"+response.toString()); //debug
		
					Xpath x = new Xpath();
					x.init(response.toString());
					res.Status = x.getValue("/GeocodeResponse/status/text()","N");
					if (res.Status.equals("OK")) {
						res.Lat= x.getValue("/GeocodeResponse/result[1]/geometry/location/lat/text()","N");
						res.Lon= x.getValue("/GeocodeResponse/result[1]/geometry/location/lng/text()","N");
						String cnt = x.getValue("/GeocodeResponse/result/geometry/location/lng/text()","Y");
						if (!cnt.equals("1")) {
							res.Status="COUNT:"+cnt;
						}
							
					}
					
					/*
					// source: http://www.mkyong.com/java/json-simple-example-read-and-write-json/
					JSONParser parser = new JSONParser();
					Object obj = parser.parse(response.toString());
					JSONObject jsonObject = (JSONObject) obj;
					status = (String) jsonObject.get("status");
						
				    if (status.equals("OK")) { 
						String geometry="";
						JSONParser parser2 = new JSONParser();
						Object obj2 = parser2.parse(response.toString());
						JSONObject jsonObject2 = (JSONObject) obj2;
						JSONArray a = (JSONArray)jsonObject2.get("results");
						for (int i=0;i<a.size();i++) {
							geometry = ((JSONObject)a.get(i)).get("geometry").toString();			
						}
						//System.out.println( geometry );
						
						JSONParser parser3 = new JSONParser();
						Object obj3 = parser3.parse(geometry);
						JSONObject jsonObject3 = (JSONObject) obj3;
						JSONObject jsonObject4 = (JSONObject)jsonObject3.get("location");
						lat = (Double) jsonObject4.get("lat")+"";
						lng = (Double) jsonObject4.get("lng")+"";
				    }
				    */
			 
				} catch (IOException e) {
				   //e.printStackTrace();
					res.Status = "ERROR: Req:"+https_url+" Resp:"+response.toString();
					res.Lat = "";
					res.Lon = "";				
				}
		 
		    } //if(con!=null)
			else
		    {
				res.Status = "ERROR: Req:"+https_url;
				res.Lat = "";
				res.Lon = "";
		    }	
			return res;	 
	   }

	  	   
	
}

