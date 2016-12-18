package com.muaavin.webservices.rest;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
	public String getUsersPosts(@QueryParam("name") String Group_name,@QueryParam("user_id") String user_id,@QueryParam("isSpecificUserPost") boolean isPostOfSpecificUser) throws Exception
	{
		if(Group_name!=null)
		Group_name = AesEncryption.decrypt(Group_name);
		if(user_id!=null)
		user_id = AesEncryption.decrypt(user_id);
		
		
		
		MySqlDb db = new MySqlDb();
		String response = "";
		
		try{
			Connection conn = db.connect();
			 Statement st = conn.createStatement();
			 response = "inside try statement :";
			 ResultSet rs = null;
			 
		if(isPostOfSpecificUser == true)
		{
			 if(Group_name.equals("A"))
			 {
				String sql = "select id , postTable.post_Detail , Post_Image from postTable where postTable.group_name ='"+Group_name+"' and postTable.User_ID = '"+user_id+"';";
				rs = (ResultSet) st.executeQuery(sql);
				 
			 }
			
			 else if(Group_name.equals("B")){
				 String sql = "select id , postTable.post_Detail , Post_Image from postTable where postTable.group_name ='"+Group_name+"' and postTable.User_ID = '"+user_id+"';";
					rs = (ResultSet) st.executeQuery(sql);
			 }
			 
			 else if(Group_name.equals("C")){
				 String sql  = "select  id  , post_Detail , Post_Image from postTable where group_name ='"+Group_name+"' and User_ID = '"+user_id+"';";
				 rs = (ResultSet) st.executeQuery(sql);
			 }
			 else if(Group_name.equals("All")){
				 String sql  = "select distinct  id  , post_Detail , Post_Image from postTable where  User_ID = '"+user_id+"';";
				 rs = (ResultSet) st.executeQuery(sql);
			 }
		}
		
		else{ rs =  getAllPosts(st, Group_name); }
			 
			 while(rs.next()) { 
				  

				  String post_detail = rs.getString("post_Detail");
				  String post_id = rs.getString("id");
				  String post_image = rs.getString("Post_Image");
				  
				  
				  Post_List.add(new Post(post_detail, post_id, post_image));
				  
			 }
			 
		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();

			response =  response +" in Eception Method";
			
		}
		return AesEncryption.encrypt(Post_List.toString());
		
		}
	
	public ResultSet getAllPosts(Statement st, String Group_name) throws SQLException
	{
		ResultSet rs = null;
		 
		 if(Group_name.equals("A"))
		 {
			String sql = "select distinct id , postTable.post_Detail , Post_Image from postTable where postTable.group_name ='"+Group_name+"';";
			rs = (ResultSet) st.executeQuery(sql);	 
		 }
		 else if(Group_name.equals("B")){
			 String sql = "select distinct id , postTable.post_Detail , Post_Image from postTable where postTable.group_name ='"+Group_name+"';";
			rs = (ResultSet) st.executeQuery(sql);
		 }
		 
		 else if(Group_name.equals("C")){
			 String sql  = "select distinct id  , post_Detail , Post_Image from postTable where group_name ='"+Group_name+"';";
			 rs = (ResultSet) st.executeQuery(sql);
		 }
		 
		 else if(Group_name.equals("All")){
			 String sql  = "select distinct  id  , post_Detail , Post_Image from postTable;";
			 rs = (ResultSet) st.executeQuery(sql);
		 }
		 
		 return rs;
		 
		 
	}
		
		
		
	
}
