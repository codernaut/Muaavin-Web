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
		Connection conn = db.connect();
		Statement st = conn.createStatement();
		ResultSet rs = null;
		
			 
		if(Group_name.equals("All"))
		{
			//GroupAll_postDetail
			String sql = "select  * from GroupAll_PostDetailWithFeedBack ;";
			rs = (ResultSet) st.executeQuery(sql);
			Post_List = getResultantFacebookPostDetailData( rs, new ArrayList<Post>(), false);
			rs = (ResultSet) st.executeQuery("select* from GroupAll_TweetDetailWithFeedBack");
			Post_List = getResultantTwitterPostDetailData(rs,Post_List,true);
			
		}
			 
		else if(Group_name.equals("A"))
		{
			// GroupA_postDetail 
			String sql = "select  * from GroupA_PostDetailWithFeedBack;";
			rs = (ResultSet) st.executeQuery(sql);	
			Post_List = getResultantFacebookPostDetailData( rs, new ArrayList<Post>(), false);
			rs = (ResultSet) st.executeQuery("select* from GroupA_TweetDetailWithFeedBack");
			Post_List = getResultantTwitterPostDetailData(rs,Post_List,true);
		}
			 
		else if(Group_name.equals("B"))
		{	
			//GroupB_postDetail
			String sql = "select  * from GroupB_PostDetailWithFeedBack ;";
			rs = (ResultSet) st.executeQuery(sql);
			Post_List = getResultantFacebookPostDetailData( rs, new ArrayList<Post>(), false);
			rs = (ResultSet) st.executeQuery("select* from GroupB_TweetDetailWithFeedBack");
			Post_List = getResultantTwitterPostDetailData(rs,Post_List,true);
		}
			 
		else if(Group_name.equals("C"))
		{
			//GroupC_postDetail 
			String sql = "select  * from GroupB_PostDetailWithFeedBack ;";
			rs = (ResultSet) st.executeQuery(sql);
			Post_List = getResultantFacebookPostDetailData( rs, new ArrayList<Post>(), false);
			rs = (ResultSet) st.executeQuery("select* from GroupC_TweetDetailWithFeedBack");
			Post_List = getResultantTwitterPostDetailData(rs,Post_List,true);
		}
		System.out.println("QUERY SUCCESSFULL EXECUTED");  conn.close();
		

		return AesEncryption.encrypt(Post_List.toString());
		
	}
	
	@POST
	@Path("/DeletePosts")
	@Produces(MediaType.APPLICATION_JSON)
	public void deletePosts(@QueryParam("Post_id") String Post_id, @QueryParam("Group_name") String group_name, @QueryParam("User_id") String user_id, @QueryParam("isPostOfSpecificUser") boolean isPostOfSpecificUser,@QueryParam("InfringingUserID") String InfringingUserID,@QueryParam("IsTwitterPost") boolean IsTwitterPost) throws Exception
	{
		if(Post_id!=null) Post_id = AesEncryption.decrypt(Post_id);
		if(group_name!=null) group_name = AesEncryption.decrypt(group_name);
		if(user_id!=null) user_id = AesEncryption.decrypt(user_id);
		
		MySqlDb Db = new MySqlDb();
		
		
		try{
			 Connection conn = Db.connect();
			 Statement st = conn.createStatement();
			 String sql;
			 
			 //if(group_name.equals("All"))
			 //if(getPostCount(Post_id) > 0 )
			 //{
				 if(isPostOfSpecificUser == true) { deleteUsersPosts( st, Post_id , user_id ,IsTwitterPost );  }
				 else { deleteAllPosts(st , Post_id ,InfringingUserID, IsTwitterPost ); }
			 //}
			 
			 	
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
		 catch (SQLException e) { e.printStackTrace(); }
		 
		
			  
		   
		
		return  PostCount ;
	}
	
	public void deleteUsersPosts( Statement st, String Post_id , String user_id, boolean  IsTwitterPost  ) throws SQLException
	{
		
		if(IsTwitterPost)
		{
			st.executeUpdate("delete from TweetTable where TweetID = '"+Post_id+"' and  User_ID = '"+user_id+"';");
				
			System.out.println("Tweet Deleted "+ "Tweet_id " + Post_id + " User id "+user_id);
			// If Infringing user id does not exist in tweet table
			// Delete from infringingUser
			// Delete from TwitterThumbsDown
			// Delete from TwitterFeedBack
		}
		else
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
		System.out.println("TwitterPOst "+IsTwitterPost);
		
		
	}
	
	public void deleteAllPosts(Statement st, String Post_id , String InfringingUserID, boolean  IsTwitterPost ) throws SQLException
	{
		
		String sql = "";  boolean RecordAlreadyPresent = false;
		if(IsTwitterPost)
		{
			 st.executeUpdate("delete from TweetTable where TweetID = '"+Post_id+"';");
			 st.executeUpdate("delete from TwitterFeedBack where TweetID = '"+Post_id+"';" );
			 st.executeUpdate("delete from TwitterThumbsDown where TweetID = '"+Post_id+"';");
			 //st.executeUpdate("delete from TwitterInfringingUsers where User_ID = '"+InfringingUserID+"';");
			 
			 ResultSet rs = (ResultSet) st.executeQuery("select * from TweetTable where Infringing_User_ID = '"+InfringingUserID+"';");
			 
			 while(rs.next()) {RecordAlreadyPresent = true; break; } 
			 
			 if(!RecordAlreadyPresent) { st.executeUpdate( "delete from  TwitterInfringingUsers  where id = '"+InfringingUserID+"';"); } 
		}
		else 
		{	 
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
			 
			 System.out.println("Post Deleted.");
			 System.out.println("IsTwitterPost :"+ IsTwitterPost);
		
		
	}
	
	public List<Post> getResultantFacebookPostDetailData( ResultSet rs, List<Post> PostDetails, boolean IsTwitterPostDetail) throws SQLException
	{
		while(rs.next()) { 
			  
			  String post_id = rs.getString("id"); //User_ID
			  String post_detail = rs.getString("post_Detail");
			  String Parent_Comment_id  = rs.getString("Parent_Comment_id");
			  String Comment_ID   = rs.getString("Comment_ID");
			  String Comment   = rs.getString("Comment");
			  String Name   = rs.getString("Name");//Name 
			  String post_image   = rs.getString("Post_Image");
			  String FeedBackMessage   = rs.getString("Feedback_Message");
			  String infringingUser_ProfilePic = rs.getString("Profile_pic");//total_Unlikes
			  int total_Unlikes =  rs.getInt("total_Unlikes");
			  	
			  PostDetails.add(new Post(post_id,post_detail,post_image,Parent_Comment_id,Name,infringingUser_ProfilePic,Comment_ID,Comment,total_Unlikes,FeedBackMessage,IsTwitterPostDetail));
			  //Post_List.add(new Post(post_id,post_detail,post_image,Parent_Comment_id,Name,infringingUser_ProfilePic,Comment_ID,Comment,total_Unlikes,false));
			 }
		return PostDetails;
		
	}
	
	public List<Post> getResultantTwitterPostDetailData( ResultSet rs, List<Post> PostDetails, boolean IsTwitterPostDetail) throws SQLException
	{
		while(rs.next()) { 
			  
			  String post_id = rs.getString("id"); //User_ID
			  String post_detail = rs.getString("post_Detail"); 
			  String post_image   = rs.getString("Post_Image");
			  String message   = rs.getString("message");
			  int total_Unlikes =  rs.getInt("total_Unlikes");
			  	
			  PostDetails.add(new Post(post_id,post_detail,post_image,"","","","","",total_Unlikes,message,IsTwitterPostDetail));
			  //Post_List.add(new Post(post_id,post_detail,post_image,Parent_Comment_id,Name,infringingUser_ProfilePic,Comment_ID,Comment,total_Unlikes,false));
			 }
		return PostDetails;
		
	}
}
