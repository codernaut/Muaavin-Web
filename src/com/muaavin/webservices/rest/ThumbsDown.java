package com.muaavin.webservices.rest;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mysql.jdbc.ResultSet;

@Path("/ThumbsDown") 
public class ThumbsDown {
	

	@POST
	@Path("/Add_ThumbsDown")
	@Produces(MediaType.APPLICATION_JSON)
	public String Add_ThumbsDown(@QueryParam("user_id") String user_id, @QueryParam("post_id") String post_id ) throws Exception
	{
		user_id = AesEncryption.decrypt(user_id);
		post_id = AesEncryption.decrypt(post_id);
		
		MySqlDb Db = new MySqlDb();
		Connection conn = Db.connect();
		Statement st = conn.createStatement();
		boolean RecordPresent = false;
			 
		ResultSet rs = (ResultSet) st.executeQuery("select* from ThumbsDown;");
		if(RecordPresent = checkIfRecordAlreadyPresent(rs, post_id, user_id) == true)
		{
			st.executeUpdate("delete from ThumbsDown where user_id = '"+user_id+"' and post_id = '"+post_id+"';");	 
		}
		else
		{
			st.executeUpdate("INSERT INTO ThumbsDown(user_id,  post_id) VALUES('"+user_id+"','"+post_id+"');");
			System.out.println("User id!"+ user_id +"Post id" +post_id);
		}
		return AesEncryption.encrypt("record successfully inserted");
		
	}
	
	public boolean checkIfRecordAlreadyPresent(ResultSet rs , String post_id, String user_id) throws SQLException
	{
		boolean RecordPresent = false;
		while(rs.next()) 
	  	{ 
		  String Post_id = rs.getString("post_id");  String User_id = rs.getString("user_id");
		  if((Post_id.equals(post_id )&&(user_id.equals(User_id)))) { RecordPresent = true; break; }
		}
		return RecordPresent;
	}
	
	
	
}
