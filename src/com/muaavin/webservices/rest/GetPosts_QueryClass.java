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
	String[] idArray;

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
			//GroupAll_PostDetailWithFeedBack
			
			String sql = "select  * from GroupAllPostDetailWithFeedBack ;";
			rs = (ResultSet) st.executeQuery(sql);
			Post_List = getResultantFacebookPostDetailData( rs, new ArrayList<Post>(), false,false);
			rs = (ResultSet) st.executeQuery("select* from Group_AllCommentDetailWithFeedBack");
			Post_List = getResultantCommentData( rs, Post_List, false,true);
			rs = (ResultSet) st.executeQuery("select* from GroupAll_TweetDetailWithFeedBack");
			Post_List = getResultantTwitterPostDetailData(rs,Post_List,true,false);
			
		}
			 
		else if(Group_name.equals("A"))
		{
			// GroupA_postDetail 
			String sql = "select  * from GroupAPostDetailWithFeedBack;";
			rs = (ResultSet) st.executeQuery(sql);	
			Post_List = getResultantFacebookPostDetailData( rs, new ArrayList<Post>(), false, false);
			rs = (ResultSet) st.executeQuery("select* from Group_ACommentDetailWithFeedBack");
			Post_List = getResultantCommentData( rs, Post_List, false,true);
			rs = (ResultSet) st.executeQuery("select* from GroupA_TweetDetailWithFeedBack");
			Post_List = getResultantTwitterPostDetailData(rs,Post_List,true,false);
		}
			 
		else if(Group_name.equals("B"))
		{	
			//GroupB_postDetail
			String sql = "select  * from GroupBPostDetailWithFeedBack ;";
			rs = (ResultSet) st.executeQuery(sql);
			Post_List = getResultantFacebookPostDetailData( rs, new ArrayList<Post>(), false,false);
			rs = (ResultSet) st.executeQuery("select* from Group_BCommentDetailWithFeedBack");
			Post_List = getResultantCommentData( rs, Post_List, false,true);
			rs = (ResultSet) st.executeQuery("select* from GroupB_TweetDetailWithFeedBack");
			Post_List = getResultantTwitterPostDetailData(rs,Post_List,true,false);
		}
			 
		else if(Group_name.equals("C"))
		{
			//GroupC_postDetail 
			String sql = "select  * from GroupCPostDetailWithFeedBack ;";
			rs = (ResultSet) st.executeQuery(sql);
			Post_List = getResultantFacebookPostDetailData( rs, new ArrayList<Post>(), false,false);
			rs = (ResultSet) st.executeQuery("select* from Group_CCommentDetailWithFeedBack");
			Post_List = getResultantCommentData( rs, Post_List, false, true);
			rs = (ResultSet) st.executeQuery("select* from GroupC_TweetDetailWithFeedBack");
			Post_List = getResultantTwitterPostDetailData(rs,Post_List,true,false);
		}
		System.out.println("QUERY SUCCESSFULL EXECUTED");  conn.close();
		

		return AesEncryption.encrypt(Post_List.toString());
		
	}
	
	@POST
	@Path("/DeletePosts")
	@Produces(MediaType.APPLICATION_JSON)
	public void deletePosts(@QueryParam("Post_id") String Post_id, @QueryParam("Group_name") String group_name, @QueryParam("User_id") String user_id, @QueryParam("isPostOfSpecificUser") boolean isPostOfSpecificUser,@QueryParam("InfringingUserID") String InfringingUserID,@QueryParam("IsTwitterPost") boolean IsTwitterPost ,@QueryParam("IsComment") boolean IsComment) throws Exception
	{
		if(Post_id!=null) Post_id = AesEncryption.decrypt(Post_id);
		if(group_name!=null) group_name = AesEncryption.decrypt(group_name);
		if(user_id!=null) user_id = AesEncryption.decrypt(user_id);
		
		MySqlDb Db = new MySqlDb();
		
		
		try{
			 Connection conn = Db.connect();
			 Statement st = conn.createStatement();
			 if(isPostOfSpecificUser == true) { deleteUsersPosts( st, Post_id , user_id ,IsTwitterPost,IsComment );  }
			 else { deleteAllPosts(st , Post_id ,InfringingUserID, IsTwitterPost ,IsComment ); }	 	
		}
			 
		catch(Exception e){ System.out.println("Inside Catch Block!"); e.printStackTrace(); }

	}
	
	
	
	
	public void deleteUsersPosts( Statement st, String Post_id , String user_id, boolean  IsTwitterPost, boolean  IsComment  ) throws SQLException
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
		else if(IsComment)
		{
			
			boolean recordPresent = false ;
			idArray =  Post_id.split("-");
			st.executeUpdate("delete from infringingUsers where Post_ID =  '"+idArray[0]+"' and  Profile_Name ='"+user_id+"' and Comment_ID ='"+idArray[2]+"';");
			st.executeUpdate("delete from comments where PostId = '"+idArray[0]+"' and  User_ID ='"+user_id+"' and Comment_ID ='"+idArray[2]+"' and Parent_Comment_id = '"+idArray[1]+"';");
			ResultSet rs = (ResultSet)st.executeQuery("select* from comments where PostId = '"+idArray[0]+"' and Comment_ID ='"+idArray[2]+"' and Parent_Comment_id = '"+idArray[1]+"';");
			while(rs.next()) { recordPresent = true; break; }
			if(!recordPresent)
			{  
				st.executeUpdate("delete from commentsThumbdown where post_id = '"+idArray[0]+"' and commentId ='"+idArray[2]+"' and PcommentId = '"+idArray[1]+"';");
				st.executeUpdate("delete from commentFeedBack where post_id = '"+idArray[0]+"' and Comment_id ='"+idArray[2]+"' and Pcommentid = '"+idArray[1]+"';");
			}
			System.out.print("Post ID :"+Post_id); System.out.print("Comment successfuly Deleted");
		}
		else
		{
			boolean recordPresent = false; 
			st.executeUpdate("delete from postTable where id =  '"+Post_id+"' and User_ID ='"+user_id+"';");
			ResultSet rs = (ResultSet)st.executeQuery("select* from postTable where id = '"+Post_id+"';");
			while(rs.next()) { recordPresent = true; break; }
			if(!recordPresent)
			{
				st.executeUpdate("delete from ThumbsDown where post_id =  '"+Post_id+"';");
				st.executeUpdate("delete from postFeedBack where post_id =  '"+Post_id+"' ;");
			}
			System.out.println("Post Successfully Deleted ");
		}
	}
	
	public void deleteAllPosts(Statement st, String Post_id , String InfringingUserID, boolean  IsTwitterPost, boolean  IsComment ) throws SQLException
	{
		
		String sql = "";  boolean RecordAlreadyPresent = false;
		if(IsTwitterPost)
		{
			 st.executeUpdate("delete from TweetTable where TweetID = '"+Post_id+"';");
			 st.executeUpdate("delete from TwitterFeedBack where TweetID = '"+Post_id+"';" );
			 st.executeUpdate("delete from TwitterThumbsDown where TweetID = '"+Post_id+"';");	 
			 ResultSet rs = (ResultSet) st.executeQuery("select * from TweetTable where Infringing_User_ID = '"+InfringingUserID+"';");			 
			 while(rs.next()) {RecordAlreadyPresent = true; break; } 			 
			 if(!RecordAlreadyPresent) { st.executeUpdate( "delete from  TwitterInfringingUsers  where id = '"+InfringingUserID+"';"); } 
			 System.out.println("Tweet successfully Deleted...");
		}
		else if(IsComment)
		{
			idArray =  Post_id.split("-");
			st.executeUpdate("delete from infringingUsers where Post_ID =  '"+idArray[0]+"' and Comment_ID ='"+idArray[2]+"';");
			st.executeUpdate("delete from comments where PostId = '"+idArray[0]+"'  and Comment_ID ='"+idArray[2]+"' and Parent_Comment_id = '"+idArray[1]+"';");
			st.executeUpdate("delete from commentsThumbdown where post_id = '"+idArray[0]+"' and commentId ='"+idArray[2]+"' and PcommentId = '"+idArray[1]+"';");
			st.executeUpdate("delete from commentFeedBack where post_id = '"+idArray[0]+"' and Comment_id ='"+idArray[2]+"' and Pcommentid = '"+idArray[1]+"';");
			System.out.println("Comment successfully Deleted...");
		}
		else 
		{	 
			 st.executeUpdate("delete from postTable where id =  '"+Post_id+"';");
			 st.executeUpdate("delete from ThumbsDown where post_id =  '"+Post_id+"';");
			 st.executeUpdate("delete from postFeedBack where post_id =  '"+Post_id+"' ;");
			 System.out.println("Post successfully Deleted...");
		}
	}
	
	public List<Post> getResultantFacebookPostDetailData( ResultSet rs, List<Post> PostDetails, boolean IsTwitterPostDetail, boolean IsComment) throws SQLException
	{
		while(rs.next()) { 
			  
			  String post_id = rs.getString("id"); //User_ID
			  String post_detail = rs.getString("post_Detail");
			  //String Parent_Comment_id  = rs.getString("Parent_Comment_id");
			  //String Comment_ID   = rs.getString("Comment_ID");
			  //String Comment   = rs.getString("Comment");
			  //String Name   = rs.getString("Name");//Name 
			  String post_image   = rs.getString("Post_Image");
			  //String FeedBackMessage   = rs.getString("Feedback_Message");
			  //String infringingUser_ProfilePic = rs.getString("Profile_pic");//total_Unlikes
			  String FeedBackMessage   = rs.getString("comment");
			  int total_Unlikes =  rs.getInt("total_Unlikes");
			  	
			  PostDetails.add(new Post(post_id,post_detail,post_image,"","","","","","",total_Unlikes,FeedBackMessage,IsTwitterPostDetail,IsComment));
			  
			 }
		return PostDetails;
		
	}
	
	public List<Post> getResultantCommentData( ResultSet rs, List<Post> PostDetails, boolean IsTwitterPostDetail, boolean IsComment) throws SQLException
	{
		while(rs.next()) { 
			  
			  
			  String Parent_Comment_id  = rs.getString("Parent_Comment_id");
			  String Comment_ID   = rs.getString("Comment_ID");
			  String Comment   = rs.getString("Comment");
			  String post_detail = rs.getString("Comment");
			  String FeedBackMessage   = rs.getString("FeedBackMessage");//InfringingUserId
			  String InfringingUserId   = rs.getString("InfringingUserId");
			  int total_Unlikes =  rs.getInt("total_Unlikes");
			  String post_id = rs.getString("id")+ "-"+ rs.getString("Parent_Comment_id") +"-"+ rs.getString("Comment_ID"); //Post_ID
			  	
			  PostDetails.add(new Post(post_id,post_detail,"",Parent_Comment_id,InfringingUserId,"","",Comment_ID,Comment,total_Unlikes,FeedBackMessage,IsTwitterPostDetail, IsComment));
			  
			 }
		return PostDetails;
		
	}
	
	public List<Post> getResultantTwitterPostDetailData( ResultSet rs, List<Post> PostDetails, boolean IsTwitterPostDetail, boolean IsComment) throws SQLException
	{
		while(rs.next()) { 
			  
			  String post_id = rs.getString("id"); //User_ID
			  String post_detail = rs.getString("post_Detail"); 
			  String post_image   = rs.getString("Post_Image");
			  String message   = rs.getString("message");
			  int total_Unlikes =  rs.getInt("total_Unlikes");
			  	
			  PostDetails.add(new Post(post_id,post_detail,post_image,"","","","","","",total_Unlikes,message,IsTwitterPostDetail, IsComment));
			  //Post_List.add(new Post(post_id,post_detail,post_image,Parent_Comment_id,Name,infringingUser_ProfilePic,Comment_ID,Comment,total_Unlikes,false));
			 }
		return PostDetails;
		
	}
}
