package com.muaavin.webservices.rest;

import java.sql.Connection;
import java.sql.Statement;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;



@Path("/FeedBack") 
public class User_FeedBack {
	

	@POST
	@Path("/Add_FeedBack")
	@Produces(MediaType.APPLICATION_JSON)
	public String Add_FeedBack(@QueryParam("user_id") String user_id, @QueryParam("post_id") String post_id,@QueryParam("comment") String comment ,@QueryParam("IsTwitterFeedBack") boolean IsTwitterFeedBack) throws Exception
	{
		user_id = AesEncryption.decrypt(user_id); post_id = AesEncryption.decrypt(post_id); comment = AesEncryption.decrypt(comment);
		
		MySqlDb Db = new MySqlDb();
		Connection conn = Db.connect();
		Statement st = conn.createStatement();
		if(IsTwitterFeedBack) { st.executeUpdate("INSERT INTO twitterfeedback(user_id,  TweetID, Message) VALUES ('"+user_id+"','"+post_id+"','"+comment+"');"); }
		
		else { st.executeUpdate("INSERT INTO userFeedBack(user_id,  post_id, comment) VALUES ('"+user_id+"','"+post_id+"','"+comment+"');"); }
		
		return AesEncryption.encrypt("Feedback successfully sent");
		
	}
}