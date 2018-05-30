import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Hashtable;
import java.util.Map;
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
					//instead this static list you can use this as well: https://restcountries.eu/#api-endpoints-list-of-codes
					Map<String, String> countryIsoCode = new Hashtable<String, String>();
					countryIsoCode.put("AF", "Afghanistan");
					countryIsoCode.put("AX", "Aland Islands");
					countryIsoCode.put("AL", "Albania");
					countryIsoCode.put("DZ", "Algeria");
					countryIsoCode.put("AS", "American Samoa");
					countryIsoCode.put("AD", "Andorra");
					countryIsoCode.put("AO", "Angola");
					countryIsoCode.put("AI", "Anguilla");
					countryIsoCode.put("AQ", "Antarctica");
					countryIsoCode.put("AG", "Antigua and Barbuda");
					countryIsoCode.put("AR", "Argentina");
					countryIsoCode.put("AM", "Armenia");
					countryIsoCode.put("AW", "Aruba");
					countryIsoCode.put("AU", "Australia");
					countryIsoCode.put("AT", "Austria");
					countryIsoCode.put("AZ", "Azerbaijan");
					countryIsoCode.put("BS", "Bahamas");
					countryIsoCode.put("BH", "Bahrain");
					countryIsoCode.put("BD", "Bangladesh");
					countryIsoCode.put("BB", "Barbados");
					countryIsoCode.put("BY", "Belarus");
					countryIsoCode.put("BE", "Belgium");
					countryIsoCode.put("BZ", "Belize");
					countryIsoCode.put("BJ", "Benin");
					countryIsoCode.put("BM", "Bermuda");
					countryIsoCode.put("BT", "Bhutan");
					countryIsoCode.put("BO", "Bolivia");
					countryIsoCode.put("BA", "Bosnia and Herzegovina");
					countryIsoCode.put("BW", "Botswana");
					countryIsoCode.put("BV", "Bouvet Island");
					countryIsoCode.put("BR", "Brazil");
					countryIsoCode.put("VG", "British Virgin Islands");
					countryIsoCode.put("IO", "British Indian Ocean Territory");
					countryIsoCode.put("BN", "Brunei Darussalam");
					countryIsoCode.put("BG", "Bulgaria");
					countryIsoCode.put("BF", "Burkina Faso");
					countryIsoCode.put("BI", "Burundi");
					countryIsoCode.put("KH", "Cambodia");
					countryIsoCode.put("CM", "Cameroon");
					countryIsoCode.put("CA", "Canada");
					countryIsoCode.put("CV", "Cape Verde");
					countryIsoCode.put("KY", "Cayman Islands");
					countryIsoCode.put("CF", "Central African Republic");
					countryIsoCode.put("TD", "Chad");
					countryIsoCode.put("CL", "Chile");
					countryIsoCode.put("CN", "China");
					countryIsoCode.put("HK", "Hong Kong, SAR China");
					countryIsoCode.put("MO", "Macao, SAR China");
					countryIsoCode.put("CX", "Christmas Island");
					countryIsoCode.put("CC", "Cocos (Keeling) Islands");
					countryIsoCode.put("CO", "Colombia");
					countryIsoCode.put("KM", "Comoros");
					countryIsoCode.put("CG", "Congo (Brazzaville)");
					countryIsoCode.put("CD", "Congo, (Kinshasa)");
					countryIsoCode.put("CK", "Cook Islands");
					countryIsoCode.put("CR", "Costa Rica");
					countryIsoCode.put("CI", "Côte d'Ivoire");
					countryIsoCode.put("HR", "Croatia");
					countryIsoCode.put("CU", "Cuba");
					countryIsoCode.put("CY", "Cyprus");
					countryIsoCode.put("CZ", "Czech Republic");
					countryIsoCode.put("DK", "Denmark");
					countryIsoCode.put("DJ", "Djibouti");
					countryIsoCode.put("DM", "Dominica");
					countryIsoCode.put("DO", "Dominican Republic");
					countryIsoCode.put("EC", "Ecuador");
					countryIsoCode.put("EG", "Egypt");
					countryIsoCode.put("SV", "El Salvador");
					countryIsoCode.put("GQ", "Equatorial Guinea");
					countryIsoCode.put("ER", "Eritrea");
					countryIsoCode.put("EE", "Estonia");
					countryIsoCode.put("ET", "Ethiopia");
					countryIsoCode.put("FK", "Falkland Islands (Malvinas)");
					countryIsoCode.put("FO", "Faroe Islands");
					countryIsoCode.put("FJ", "Fiji");
					countryIsoCode.put("FI", "Finland");
					countryIsoCode.put("FR", "France");
					countryIsoCode.put("GF", "French Guiana");
					countryIsoCode.put("PF", "French Polynesia");
					countryIsoCode.put("TF", "French Southern Territories");
					countryIsoCode.put("GA", "Gabon");
					countryIsoCode.put("GM", "Gambia");
					countryIsoCode.put("GE", "Georgia");
					countryIsoCode.put("DE", "Germany");
					countryIsoCode.put("GH", "Ghana");
					countryIsoCode.put("GI", "Gibraltar");
					countryIsoCode.put("GR", "Greece");
					countryIsoCode.put("GL", "Greenland");
					countryIsoCode.put("GD", "Grenada");
					countryIsoCode.put("GP", "Guadeloupe");
					countryIsoCode.put("GU", "Guam");
					countryIsoCode.put("GT", "Guatemala");
					countryIsoCode.put("GG", "Guernsey");
					countryIsoCode.put("GN", "Guinea");
					countryIsoCode.put("GW", "Guinea-Bissau");
					countryIsoCode.put("GY", "Guyana");
					countryIsoCode.put("HT", "Haiti");
					countryIsoCode.put("HM", "Heard and Mcdonald Islands");
					countryIsoCode.put("VA", "Holy See (Vatican City State)");
					countryIsoCode.put("HN", "Honduras");
					countryIsoCode.put("HU", "Hungary");
					countryIsoCode.put("IS", "Iceland");
					countryIsoCode.put("IN", "India");
					countryIsoCode.put("ID", "Indonesia");
					countryIsoCode.put("IR", "Iran, Islamic Republic of");
					countryIsoCode.put("IQ", "Iraq");
					countryIsoCode.put("IE", "Ireland");
					countryIsoCode.put("IM", "Isle of Man");
					countryIsoCode.put("IL", "Israel");
					countryIsoCode.put("IT", "Italy");
					countryIsoCode.put("JM", "Jamaica");
					countryIsoCode.put("JP", "Japan");
					countryIsoCode.put("JE", "Jersey");
					countryIsoCode.put("JO", "Jordan");
					countryIsoCode.put("KZ", "Kazakhstan");
					countryIsoCode.put("KE", "Kenya");
					countryIsoCode.put("KI", "Kiribati");
					countryIsoCode.put("KP", "Korea (North)");
					countryIsoCode.put("KR", "Korea (South)");
					countryIsoCode.put("KW", "Kuwait");
					countryIsoCode.put("KG", "Kyrgyzstan");
					countryIsoCode.put("LA", "Lao PDR");
					countryIsoCode.put("LV", "Latvia");
					countryIsoCode.put("LB", "Lebanon");
					countryIsoCode.put("LS", "Lesotho");
					countryIsoCode.put("LR", "Liberia");
					countryIsoCode.put("LY", "Libya");
					countryIsoCode.put("LI", "Liechtenstein");
					countryIsoCode.put("LT", "Lithuania");
					countryIsoCode.put("LU", "Luxembourg");
					countryIsoCode.put("MK", "Macedonia, Republic of");
					countryIsoCode.put("MG", "Madagascar");
					countryIsoCode.put("MW", "Malawi");
					countryIsoCode.put("MY", "Malaysia");
					countryIsoCode.put("MV", "Maldives");
					countryIsoCode.put("ML", "Mali");
					countryIsoCode.put("MT", "Malta");
					countryIsoCode.put("MH", "Marshall Islands");
					countryIsoCode.put("MQ", "Martinique");
					countryIsoCode.put("MR", "Mauritania");
					countryIsoCode.put("MU", "Mauritius");
					countryIsoCode.put("YT", "Mayotte");
					countryIsoCode.put("MX", "Mexico");
					countryIsoCode.put("FM", "Micronesia, Federated States of");
					countryIsoCode.put("MD", "Moldova");
					countryIsoCode.put("MC", "Monaco");
					countryIsoCode.put("MN", "Mongolia");
					countryIsoCode.put("ME", "Montenegro");
					countryIsoCode.put("MS", "Montserrat");
					countryIsoCode.put("MA", "Morocco");
					countryIsoCode.put("MZ", "Mozambique");
					countryIsoCode.put("MM", "Myanmar");
					countryIsoCode.put("NA", "Namibia");
					countryIsoCode.put("NR", "Nauru");
					countryIsoCode.put("NP", "Nepal");
					countryIsoCode.put("NL", "Netherlands");
					countryIsoCode.put("AN", "Netherlands Antilles");
					countryIsoCode.put("NC", "New Caledonia");
					countryIsoCode.put("NZ", "New Zealand");
					countryIsoCode.put("NI", "Nicaragua");
					countryIsoCode.put("NE", "Niger");
					countryIsoCode.put("NG", "Nigeria");
					countryIsoCode.put("NU", "Niue");
					countryIsoCode.put("NF", "Norfolk Island");
					countryIsoCode.put("MP", "Northern Mariana Islands");
					countryIsoCode.put("NO", "Norway");
					countryIsoCode.put("OM", "Oman");
					countryIsoCode.put("PK", "Pakistan");
					countryIsoCode.put("PW", "Palau");
					countryIsoCode.put("PS", "Palestinian Territory");
					countryIsoCode.put("PA", "Panama");
					countryIsoCode.put("PG", "Papua New Guinea");
					countryIsoCode.put("PY", "Paraguay");
					countryIsoCode.put("PE", "Peru");
					countryIsoCode.put("PH", "Philippines");
					countryIsoCode.put("PN", "Pitcairn");
					countryIsoCode.put("PL", "Poland");
					countryIsoCode.put("PT", "Portugal");
					countryIsoCode.put("PR", "Puerto Rico");
					countryIsoCode.put("QA", "Qatar");
					countryIsoCode.put("RE", "Réunion");
					countryIsoCode.put("RO", "Romania");
					countryIsoCode.put("RU", "Russian Federation");
					countryIsoCode.put("RW", "Rwanda");
					countryIsoCode.put("BL", "Saint-Barthélemy");
					countryIsoCode.put("SH", "Saint Helena");
					countryIsoCode.put("KN", "Saint Kitts and Nevis");
					countryIsoCode.put("LC", "Saint Lucia");
					countryIsoCode.put("MF", "Saint-Martin (French part)");
					countryIsoCode.put("PM", "Saint Pierre and Miquelon");
					countryIsoCode.put("VC", "Saint Vincent and Grenadines");
					countryIsoCode.put("WS", "Samoa");
					countryIsoCode.put("SM", "San Marino");
					countryIsoCode.put("ST", "Sao Tome and Principe");
					countryIsoCode.put("SA", "Saudi Arabia");
					countryIsoCode.put("SN", "Senegal");
					countryIsoCode.put("RS", "Serbia");
					countryIsoCode.put("SC", "Seychelles");
					countryIsoCode.put("SL", "Sierra Leone");
					countryIsoCode.put("SG", "Singapore");
					countryIsoCode.put("SK", "Slovakia");
					countryIsoCode.put("SI", "Slovenia");
					countryIsoCode.put("SB", "Solomon Islands");
					countryIsoCode.put("SO", "Somalia");
					countryIsoCode.put("ZA", "South Africa");
					countryIsoCode.put("GS", "South Georgia and the South Sandwich Islands");
					countryIsoCode.put("SS", "South Sudan");
					countryIsoCode.put("ES", "Spain");
					countryIsoCode.put("LK", "Sri Lanka");
					countryIsoCode.put("SD", "Sudan");
					countryIsoCode.put("SR", "Suriname");
					countryIsoCode.put("SJ", "Svalbard and Jan Mayen Islands");
					countryIsoCode.put("SZ", "Swaziland");
					countryIsoCode.put("SE", "Sweden");
					countryIsoCode.put("CH", "Switzerland");
					countryIsoCode.put("SY", "Syrian Arab Republic (Syria)");
					countryIsoCode.put("TW", "Taiwan, Republic of China");
					countryIsoCode.put("TJ", "Tajikistan");
					countryIsoCode.put("TZ", "Tanzania, United Republic of");
					countryIsoCode.put("TH", "Thailand");
					countryIsoCode.put("TL", "Timor-Leste");
					countryIsoCode.put("TG", "Togo");
					countryIsoCode.put("TK", "Tokelau");
					countryIsoCode.put("TO", "Tonga");
					countryIsoCode.put("TT", "Trinidad and Tobago");
					countryIsoCode.put("TN", "Tunisia");
					countryIsoCode.put("TR", "Turkey");
					countryIsoCode.put("TM", "Turkmenistan");
					countryIsoCode.put("TC", "Turks and Caicos Islands");
					countryIsoCode.put("TV", "Tuvalu");
					countryIsoCode.put("UG", "Uganda");
					countryIsoCode.put("UA", "Ukraine");
					countryIsoCode.put("AE", "United Arab Emirates");
					countryIsoCode.put("GB", "United Kingdom");
					countryIsoCode.put("US", "United States of America");
					countryIsoCode.put("UM", "US Minor Outlying Islands");
					countryIsoCode.put("UY", "Uruguay");
					countryIsoCode.put("UZ", "Uzbekistan");
					countryIsoCode.put("VU", "Vanuatu");
					countryIsoCode.put("VE", "Venezuela (Bolivarian Republic)");
					countryIsoCode.put("VN", "Viet Nam");
					countryIsoCode.put("VI", "Virgin Islands, US");
					countryIsoCode.put("WF", "Wallis and Futuna Islands");
					countryIsoCode.put("EH", "Western Sahara");
					countryIsoCode.put("YE", "Yemen");
					countryIsoCode.put("ZM", "Zambia");
					countryIsoCode.put("ZW", "Zimbabwe");
					if (countryIsoCode.containsKey(shippingcountry)) shippingcountry = countryIsoCode.get(shippingcountry);
					
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

					concatenatedAddress = URLEncoder.encode(concatenatedAddress, "UTF-8");
				    /* concatenatedAddress = concatenatedAddress
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
					    .replace("$","%24");*/

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

