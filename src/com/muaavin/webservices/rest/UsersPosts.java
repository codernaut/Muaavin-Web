package com.muaavin.webservices.rest;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mysql.jdbc.ResultSet;

@Path("/UsersPosts") 
public class UsersPosts {

	List<Post> Post_List = new ArrayList<>();
	boolean isPostOfSpecificUser;
	
	@POST
	@Path("/GetUsersPosts")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUsersPosts(@QueryParam("name") String Group_name,@QueryParam("user_id") String user_id,@QueryParam("isSpecificUserPost") boolean isPostOfSpecificUser, @QueryParam("TwitterUserID") String TwitterUserID) throws Exception
	{
		if(Group_name!=null) { Group_name = AesEncryption.decrypt(Group_name); }
		
		if(user_id!=null) { user_id = AesEncryption.decrypt(user_id); }
		
		if(TwitterUserID!= null) { TwitterUserID = AesEncryption.decrypt(TwitterUserID); }
		
		MySqlDb db = new MySqlDb();
		Connection conn = db.connect();
		Statement st = conn.createStatement();
		ResultSet rs = null;
			 
		if(isPostOfSpecificUser == true)
		{    
			 rs = getUserPosts(Group_name, rs, st, user_id, TwitterUserID, true);
			 Post_List = getresultantDataFromDB(rs, true, new ArrayList<Post>());
			 rs = getUserPosts(Group_name, rs, st, user_id, TwitterUserID, false);
			 Post_List = getresultantDataFromDB(rs, false,Post_List ); 
		}
		else
		{
			rs =  getAllPosts(st, Group_name,true); 
			Post_List = getresultantDataFromDB(rs, true, new ArrayList<Post>());
			rs =  getAllPosts(st, Group_name,false); 
			Post_List = getresultantDataFromDB(rs, false,Post_List );
		}
		return AesEncryption.encrypt(Post_List.toString());	
	}
	// Get Get All Posts
	public ResultSet getAllPosts(Statement st, String Group_name, boolean IsTwitterData) throws SQLException
	{
		ResultSet rs = null;
		if(IsTwitterData)
		{
			if(Group_name.equals("All")) { rs = (ResultSet) st.executeQuery("select distinct TweetID as id, message as post_Detail , ImageUrl as Post_Image from tweetTable;"); }
			else{	rs = (ResultSet) st.executeQuery("select distinct TweetID as id , message as post_Detail , ImageUrl as Post_Image from tweetTable where Group_Name  = '"+Group_name+"';"); }
		}
		else
		{
			if(Group_name.equals("All")){  rs = (ResultSet) st.executeQuery("select distinct  id  , post_Detail , Post_Image from postTable;"); }
				
			else{ rs = (ResultSet) st.executeQuery("select distinct id  , post_Detail , Post_Image from postTable where group_name ='"+Group_name+"';");  }			 			
		}   return rs; 
		 
	 }
	// Get User's Posts
	public ResultSet getUserPosts(String Group_name, ResultSet rs, Statement st, String user_id, String TwitterUserID, boolean IsTwitterData) throws SQLException
	{
		if(IsTwitterData)
		{
			if(Group_name.equals("All")) { rs = (ResultSet) st.executeQuery("select distinct TweetID as id, message as post_Detail , ImageUrl as Post_Image from tweetTable where User_ID = '"+TwitterUserID+"';"); }
			
			else {	rs = (ResultSet) st.executeQuery("select distinct TweetID as id , message as post_Detail , ImageUrl as Post_Image from tweetTable where Group_Name  = '"+Group_name+"' and User_ID = '"+TwitterUserID+"';"); }
		}
		else
		{
			if(Group_name.equals("All")){ rs = (ResultSet) st.executeQuery("select distinct(id)  , post_Detail , Post_Image from postTable where  User_ID = '"+user_id+"';"); }
				 		
			else { rs = (ResultSet) st.executeQuery("select  id  , post_Detail , Post_Image from postTable where group_name ='"+Group_name+"' and User_ID = '"+user_id+"';"); }
		}  System.out.println("IsTwitterData"+ IsTwitterData + " TwitterUserID :"+TwitterUserID); return rs;
		
	}
	
	public  List<Post> getresultantDataFromDB(ResultSet rs, boolean IsTwitterPost, List<Post> Posts) throws SQLException
	{
		while(rs.next()) 
		{ 
			  
			  String post_detail = rs.getString("post_Detail");
			  String post_id = rs.getString("id");
			  String post_image = rs.getString("Post_Image");
			  Posts.add(new Post(post_detail, post_id, post_image,IsTwitterPost));  
			  System.out.print("post_detail :"+post_detail + " IsTwitterPost :"+IsTwitterPost);
		 }
		return Posts;
	}
		
		
		
	
}
