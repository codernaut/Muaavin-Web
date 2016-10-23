package com.muaavin.webservices.rest;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;

import org.json.JSONObject;

import com.muaavin.db.mysqldb.MySqlDb;

//import coResultSet;

@Path("/Query") 
public class QueryClass {
	
	List<InfringingUser> infringing_users = new ArrayList<>();
	////////////////////////////////////////
	@POST
	@Path("/Highlights")
	@Produces(MediaType.APPLICATION_JSON)
	public String get_InfringingUsers(@QueryParam("name") String Group_name)
	{
		MySqlDb db = new MySqlDb();
		
		//id++;
		String response = "";
		
		try{
			 Connection conn = db.connect();
			 
			 
			 
			 Statement st = conn.createStatement();
			 response = "inside try statement :";
			 ResultSet rs;
			 ///////////////
			 if(Group_name == null)
			 { 
				 response = "No Parameters Passed..";
				// return response;
				 
			 }
			 
			 
			 if(Group_name.equals("All"))
			 {
				 
				String sql = "select infringingUsers.User_ID from groupTable,infringingUsers where groupTable.id=infringingUsers.Group_ID ;";
				rs = (ResultSet) st.executeQuery(sql);
				 
				 
			 }
			
			 else{
				 String sql = "select infringingUsers.User_ID from groupTable,infringingUsers where groupTable.id=infringingUsers.Group_ID and groupTable.name='"+Group_name+"';";
				 rs = (ResultSet) st.executeQuery(sql);
			 }
			 
			 
			 int i = 1;
			 
			 
			  while(rs.next()) { 
					 System.out.println("afdg");

				  Double id = rs.getDouble("User_ID"); 
				 
				
				  
				  infringing_users.add(new InfringingUser(id));
				    
				  
				 
				  
				  System.out.println(String.valueOf(id));
				 
				  i = i + 1;
				  break;
				 }
			  System.out.println("QUERY SUCCESSFULL EXECUTED");
			
			 
			  conn.close();
			  
			}
			catch(Exception e)
			{
				  e.printStackTrace();

				response =  response +" in Eception Method";
				
			}
		
		
		
		return infringing_users.toString();
		//return response;
	}
	
}
