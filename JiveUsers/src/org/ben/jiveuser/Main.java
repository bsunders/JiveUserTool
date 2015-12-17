package org.ben.jiveuser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;




public class Main {
	
	
	private static JiveUser[] users; // we will initialize this array once we know the no. of users
	private static String filename = "users.csv";
	static GetJiveUserData jive;

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String jive_usr = "ben.sunderland"; // default in case not set in UI.
		String jive_pwd = "demo"; 
		String jive_url = "https://jivedemo-apacdemo.jiveon.com";
		
		
		
		 if ((jive_usr == "") || (jive_pwd == "")	|| (jive_url == "")){
			 System.out.println("\n------- ERROR: Jive credentials are required..--------\n"); 
    		 return;
    	 }
    	 
    	 // get rid of any slash at end
    	 if (jive_url.endsWith("/"))
    	 {
    		 jive_url =jive_url.substring(0, jive_url.length() -1 );
    	 }
    	 
    	 
		 jive = new GetJiveUserData(jive_url, jive_usr, jive_pwd);
		try {
	
			jive.getJiveUserData(); // just stores each user and who they follow in an array
			
			// now we can size the array
			users = new JiveUser[jive.userIndex ];
			users = jive.myUsers;

			dumpUsersToFile();  // write all user data from array to csv, 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		


	}
	
	
	

	
	/**
	 * dumpUsersToFile
	 */
	public static void dumpUsersToFile(){

		try {

			BufferedWriter writer = null;
			writer = new BufferedWriter(new FileWriter(filename));
			for ( int i = 0; i < jive.userIndex ; i++)
			{   
				if( users[i].login == "") break;

					String row = "\"" + users[i].login + "\",\"" + users[i].id+"\"," ;
					row = row + "\"" + users[i].fullname  + "\",\"" + users[i].email+"\"";
					writer.write(row);
					
					System.out.println(row);
					writer.newLine();

			}
			writer.close();
			System.out.println("\nWritten Users to CSF File: \n"+ filename); 
			
		} catch(IOException ex) {
			ex.printStackTrace();
		}


	}
	
	
	
	
	
	

}
