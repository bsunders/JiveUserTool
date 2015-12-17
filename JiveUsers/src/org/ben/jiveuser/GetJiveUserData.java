package org.ben.jiveuser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



//helper class to store each Jive user data
class JiveUser {	
	public String login; // jive username
	public String id; // jive unique ID is a number
	public String fullname; // name.formatted 
	public String email; //  emails[0].value
	
	
	//private final Integer maxIdols=1000;
	//public String[]  idols = new String[maxIdols]; // assumes a user will follow no more than 1000 others.

	public JiveUser (String xlogin, String xid, String xfullname, String xemail) {
		this.login = xlogin; 
		this.id = xid;
		this.email = xemail;
		this.fullname = xfullname;

//		for (int i = 0; i < idols.length ; i++)
//			  this.idols[i] = "";		
	}
}


public class GetJiveUserData {

	public Integer numUsers = 0;
	private String username;
	private String password;
	private String urlBase;
	private String auth;
	
	public Integer userIndex;
	public JiveUser[] myUsers; 

	// constructor
	public GetJiveUserData (String _urlbase, String _username, String _password){
			this.urlBase=_urlbase;
			this.username=_username;
			this.password=_password;
			this.auth="";
			this.userIndex = 0;
			
	}



	/**
	 * getJiveUserData - most of the work is done here
	 * 
	 */
	public void getJiveUserData(){
		

		String count = "20";
		// first get list of all users
		System.out.println("Retrieving user data from Jive..."); 
		DefaultHttpClient httpClient = new DefaultHttpClient();

		String params; //  = "?sort=firstNameAsc&fields=name,jive.username,emails,-resources&count="+count+"&startIndex=0"; 
		HttpGet getRequest; // = new HttpGet(urlBase+"/api/core/v3/people/@all"+params);
		 
		
		String unhashedString = this.username + ":" + this.password;
		byte[] byteArray = Base64.encodeBase64(unhashedString.getBytes());
		this.auth = new String(byteArray);



		
			
		try {
			HttpResponse response; // = httpClient.execute(getRequest);
			String jsonOut = ""; //  = readStream(response.getEntity().getContent());
			
			//System.out.println("\nResponse (JSON):\n"+ jsonOut); 
			String nextPage = "0"; //getNextPage(jsonOut);
			
			
			myUsers = new JiveUser[5000];  //TODO NEEDS TO BE SIZED 
			
			//jsonOut = removeThrowLine(jsonOut);
		    //getAllUserElements(jsonOut);   // this add first page to array
			
			while (nextPage != "") {
				// Remove throw-line if present
				
				// now get subsequent pages
				params = "?sort=firstNameAsc&fields=name,jive.username,emails,-resources&count="+count+"&startIndex=" + nextPage; 
				getRequest = new HttpGet(urlBase+"/api/core/v3/people/@all"+params);
				getRequest.setHeader("Authorization", "Basic " + this.auth);
				System.out.println("\nREST call:\n"+ urlBase+"/api/core/v3/people/@all"+params);
				
				response = httpClient.execute(getRequest);  
				jsonOut = readStream(response.getEntity().getContent());
				
				jsonOut = removeThrowLine(jsonOut);
				getAllUserElements(jsonOut);   // this needs to ADD to our array.....
			    nextPage = getNextPage(jsonOut);
			    
			}	
			
		    
		    
	        
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\n------- Done getting graph data from Jive server--------\n"); 

	}
	
	
		
	// Extra user data from JSON into local array
	/**
	 * getAllUserElements
	 * @param d
	 */
	private void getAllUserElements(String jsonData){
	    
			try {
				JSONObject obj = new JSONObject(jsonData);
				JSONArray arr = obj.getJSONArray("list");
				
				
				
				for (int i = 0; i < arr.length(); i++)
				{
				    String ID = arr.getJSONObject(i).getString("id");
				    String  login = arr.getJSONObject(i).getJSONObject("jive").getString("username");
				    String fullname = arr.getJSONObject(i).getJSONObject("name").getString("formatted");
				    String email = arr.getJSONObject(i).getJSONArray("emails").getJSONObject(0).getString("value");
				    
				    if (ID != ""){
				    	myUsers[userIndex] = new JiveUser (login, ID,fullname,email);
				    	userIndex++;
				    }
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	    
	}
	
	// Extra user data from JSON into local array
	/**
	 * getAllUserElements
	 * @param d
	 */
	private void addUserElements(String jsonData){
	    
			try {
				JSONObject obj = new JSONObject(jsonData);
				JSONArray arr = obj.getJSONArray("list");
				
				//myUsers = new JiveUser[arr.length()];
				
				for (int i = 0; i < arr.length(); i++)
				{
				    String ID = arr.getJSONObject(i).getString("id");
				    String  login = arr.getJSONObject(i).getJSONObject("jive").getString("username");
				    String fullname = arr.getJSONObject(i).getJSONObject("name").getString("formatted");
				    String email = arr.getJSONObject(i).getJSONArray("emails").getJSONObject(0).getString("value");
				    
				    myUsers[i] = new JiveUser (login, ID,fullname,email); 
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	    
	}
	
	
	
	/**
	 * readStream
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public String readStream(InputStream is) throws IOException {
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	
	         StringBuffer sb = new StringBuffer();
	         String output;
	         while ((output = br.readLine()) != null) {
	             sb.append(output);
	         }   
	         return sb.toString();
	 }
	
	private String getNextPage(String strResponse)
	{

		String next = "";
		
		if (strResponse.indexOf("next") < 0)
			return "";
		
		
		
		try {
			JSONObject obj = new JSONObject(strResponse);
			
			next = obj.getJSONObject("links").getString("next");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		if (next.contains("startIndex"))
		{
			int pos1 = next.indexOf("startIndex=");
			next = next.substring(pos1+ 11,pos1 + 13);
		}
	
		
		
		return next;
	}
	
	
	
	private String removeThrowLine(String strResponse)
	{
		if (strResponse.contains("allowIllegalResourceCall"))
		{
			int posOfFirstParenthesis = strResponse.indexOf("{");
			strResponse = strResponse.substring(posOfFirstParenthesis);
		}
	
		return strResponse;
	}
	
	
	
	
}


