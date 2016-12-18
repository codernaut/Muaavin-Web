package com.muaavin.webservices.rest;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mysql.jdbc.ResultSet;

@Path("/Posts_Query")
public class GetPosts_QueryClass {

	
	List<Post> Post_List = new ArrayList<>();

	@POST
	@Path("/GetPosts")
	@Produces(MediaType.APPLICATION_JSON)
	public String get_InfringingUsers(@QueryParam("name") String Group_name) throws Exception
	{
		Group_name = AesEncryption.decrypt(Group_name);
		MySqlDb db = new MySqlDb();
		
		String response = "";
		
		try{
			 Connection conn = db.connect();
			
			 
			 Statement st = conn.createStatement();
			 response = "inside try statement :";
			 ResultSet rs = null;
		
			 if(Group_name.equals("All"))
			 {

				
				String sql = "select * from GroupAll_postDetail ;";
				rs = (ResultSet) st.executeQuery(sql);
				 
				 
			 }
			 
			 else if(Group_name.equals("A"))
			 {

				
				String sql = "select * from GroupA_postDetail ;";
				rs = (ResultSet) st.executeQuery(sql);
				 
				 
			 }
			
			 else if(Group_name.equals("B")){
				 String sql = "select * from GroupB_postDetail ;";
				 rs = (ResultSet) st.executeQuery(sql);
			 }
			 
			 else if(Group_name.equals("C")){
				 String sql = "select * from GroupC_postDetail ;";
				 rs = (ResultSet) st.executeQuery(sql);
			 }
			 
			 
			 int i = 1;
			 
			 
			  while(rs.next()) { 
				  System.out.println("afdg");

				  String post_id = rs.getString("id"); //User_ID
				  String post_detail = rs.getString("post_Detail");
				  String Parent_Comment_id  = rs.getString("Parent_Comment_id");
				  String Comment_ID   = rs.getString("Comment_ID");
				  String Comment   = rs.getString("Comment");
				  String Name   = rs.getString("Name");//Name 
				  String post_image   = rs.getString("Post_Image");
				  String infringingUser_ProfilePic = rs.getString("Profile_pic");//total_Unlikes
				  int total_Unlikes =  rs.getInt("total_Unlikes");
				  
				  
				  Post_List.add(new Post(post_id,post_detail,post_image,Parent_Comment_id,Name,infringingUser_ProfilePic,Comment_ID,Comment,total_Unlikes));
				    
				  
				 
				  
				  System.out.println("Post_ID  ... "+String.valueOf(post_id));
				  System.out.println("Post_Detail  ... "+String.valueOf(post_detail));
				 
				  i = i + 1;
				 // break;
				 }
			  System.out.println("QUERY SUCCESSFULL EXECUTED");
			
			 
			  conn.close();
			  
			}
			catch(Exception e)
			{
				  e.printStackTrace();

				response =  response +" in Eception Method";
				
			}

		return AesEncryption.encrypt(Post_List.toString());
		//return response;
	}
	
	@POST
	@Path("/DeletePosts")
	@Produces(MediaType.APPLICATION_JSON)
	public void deletePosts(@QueryParam("Post_id") String Post_id, @QueryParam("Group_name") String group_name, @QueryParam("User_id") String user_id, @QueryParam("isPostOfSpecificUser") boolean isPostOfSpecificUser) throws Exception
	{
		Post_id = AesEncryption.decrypt(Post_id);
		group_name = AesEncryption.decrypt(group_name);
		user_id = AesEncryption.decrypt(user_id);
		
		
		MySqlDb Db = new MySqlDb();
		
		
		try{
			 Connection conn = Db.connect();
			 Statement st = conn.createStatement();
			 String sql;
			 
			 //if(group_name.equals("All"))
			 if(getPostCount(Post_id) > 0 )
			 {
				 if(isPostOfSpecificUser == true) { deleteUsersPosts( st, Post_id , user_id  );  }
				 else { deleteAllPosts(st , Post_id  ); }
			 }
			 	
		}
			 
		catch(Exception e)
		{
			System.out.println("Inside outer try block!");
		}
		
		
	
	}
	
	
	public int getPostCount(String post_id)
	{
		MySqlDb Db = new MySqlDb();
		Connection conn = Db.connect();
		ResultSet rs = null;
		int PostCount = 0;
		 try 
		  {
			Statement st = conn.createStatement();
			String sql = "select count(*) AS PostCount from postTable where postTable.id = '"+post_id+"';";
			st.executeQuery(sql); 
			
			rs = (ResultSet) st.executeQuery(sql);
			
			while(rs.next())
			{ 
				PostCount =  rs.getInt("PostCount");
			}
			
		  }
		 catch (SQLException e)
		 {
		
			e.printStackTrace();
		 }
		
		return  PostCount ;
	}
	
	public void deleteUsersPosts( Statement st, String Post_id , String user_id  ) throws SQLException
	{
		{
			 String sql = "";
			 sql ="delete from infringingUsers where Post_ID =  '"+Post_id+"' and  Profile_Name ='"+user_id+"'; ";
			 st.executeUpdate(sql);
			 
			 sql = "delete from Comments where User_ID='"+user_id+"' and Comment_ID in (select Comment_ID from Posts_Comments_Table where Post_ID = '"+Post_id+"' );";
			 st.executeUpdate(sql);
			 
			 sql = "delete from ThumbsDown where post_id =  '"+Post_id+"' and user_id = '"+user_id+"';";
			 st.executeUpdate(sql);
			 
			 sql = "delete from postTable where id =  '"+Post_id+"' and User_ID ='"+user_id+"';";
			 st.executeUpdate(sql);
			 
			 sql ="delete from  UserFeedBack where post_id = '"+Post_id+"' and user_id ='"+user_id+"';";
			 st.executeUpdate(sql);
			 
			 sql = "delete from Posts_Comments_Table where  Post_ID = '"+Post_id+"';"; 
			 st.executeUpdate(sql);
		}
		
	}
	
	public void deleteAllPosts(Statement st, String Post_id   ) throws SQLException
	{
		{
			 String sql = "";
			 sql ="delete from infringingUsers where Post_ID =  '"+Post_id+"' ; ";
			 st.executeUpdate(sql);
			 
			 sql = "delete from Comments where Comment_ID in (select Comment_ID from Posts_Comments_Table where Post_ID = '"+Post_id+"' );";
			 st.executeUpdate(sql);
			 
			 sql = "delete from ThumbsDown where post_id =  '"+Post_id+"';";
			 st.executeUpdate(sql);
			 
			 sql = "delete from postTable where id =  '"+Post_id+"';";
			 st.executeUpdate(sql);
			 
			 sql ="delete from  UserFeedBack where post_id = '"+Post_id+"';";
			 st.executeUpdate(sql);
			 
			 sql = "delete from Posts_Comments_Table where  Post_ID = '"+Post_id+"';"; 
			 st.executeUpdate(sql);
		}
		
	}
}
