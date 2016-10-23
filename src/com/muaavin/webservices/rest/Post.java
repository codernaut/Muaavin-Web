package com.muaavin.webservices.rest;


import java.util.ArrayList;
import java.util.List;

import java.sql.*;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.muaavin.db.mysqldb.MySqlDb;
import com.mysql.jdbc.PreparedStatement;

@Path("/posts") 
public class Post {

	
	String Post_ID;
	String Group_Name;
	String post_Detail;
	double User_ID;
	
	Post[] parr = new Post[20];
	List<Post> arr = new ArrayList<Post>();
	Post p;
	

	
	
	public Post()
	{
		
		
		
	}
	
	@POST
	@Path("/Insert_Post")
	@Produces(MediaType.APPLICATION_JSON)
	public String Insert_Posts(@QueryParam("Post_id") String P_id,@QueryParam("Group_id") int G_id,@QueryParam("Group_name") String G_name , @QueryParam("Profile_name") String profile_name,@QueryParam("user_id") double user_id, @QueryParam("Post_Det") String Post_Det)
	{
		MySqlDb Db = new MySqlDb();
		//id++;
		String response = "";
		try{
			 Connection conn = Db.connect();
			 Statement st = conn.createStatement();
			 
			 
			 
			 response = "inside try statement :";
		
			 System.out.print("P_id "+P_id+" G_id "+G_id+" Gname "+G_name+" Profile name "+profile_name + " User ID "+user_id+ " Post Detail "+Post_Det);
			
			 
			
			 String sql = "INSERT INTO infringingUsers(Post_ID, Group_ID,Profile_Name,User_ID) VALUES ('"+P_id+"','"+G_id+"','"+profile_name+"',"+user_id+");";
			

			
			 st.executeUpdate(sql);
			 
	
			
			 
			 String sql1 = "INSERT INTO postTable(id,group_name, post_Detail,User_ID)"+"VALUES('"+P_id+"','"+G_name+"','"+Post_Det+"','"+user_id+"');";
		     st.executeUpdate(sql1);
			 
			 
			 /////////////
			 
			System.out.print("Record inserted Successfully");
			  response = "Record inserted Successfully";
			 
			  conn.close();
			}
			catch(Exception e)
			{
				
				response =  response +"not inserted";
				e.printStackTrace();
			}
		
		return response;
	}
	
	
}
