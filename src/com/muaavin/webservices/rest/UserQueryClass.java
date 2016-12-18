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

@Path("/Users") 
public class UserQueryClass {
	
	List<InfringingUser> users = new ArrayList<>();
	List<InfringingUser> infringing_users = new ArrayList<>();
	
	@POST
	@Path("/GetUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUsers() throws Exception
	{
		users = new ArrayList<InfringingUser>();
		MySqlDb db = new MySqlDb();
		Connection conn = db.connect();
		Statement st = conn.createStatement();
		ResultSet rs = null;
		String sql = "select* from users;";
		rs = (ResultSet) st.executeQuery(sql);
		    
		while(rs.next()) 
		{ 
		   InfringingUser user = InfringingUser.initializeUser(rs.getString("name"), rs.getString("id"), rs.getString("profilePic"),rs.getString("state")); 		    	  
		   users.add(user); 
		}    	
		return AesEncryption.encrypt(users.toString());	
	}
	
	@POST
	@Path("/BlockUser")
	@Produces(MediaType.APPLICATION_JSON)
	public String BlockUser(@QueryParam("user_id") String user_id) throws Exception
	{
		user_id = AesEncryption.decrypt(user_id);
		MySqlDb Db = new MySqlDb();
		
		Connection conn = Db.connect();
		Statement st = conn.createStatement();
		String sql = "select* from BlockedUsers;";
		ResultSet rs = (ResultSet) st.executeQuery(sql);
		if(isUserAlreadyBlocked(rs, user_id)== true)
		return "User Already Blocked";
			
	    st.executeUpdate("UPDATE infringingUsers SET state ='Blocked' where User_ID = '"+user_id+"';");
	    st.executeUpdate("UPDATE Users SET state ='Blocked' where id = '"+user_id+"';");
		String response = "User Successfully Blocked"; 
		return AesEncryption.encrypt(response);

	}
	
	// Check if user is already blocked
	public boolean isUserAlreadyBlocked(ResultSet rs, String user_id) throws SQLException
	{
		while(rs.next()) 
		{  
		   String User_id = rs.getString("User_ID");
		   if((User_id.equals(user_id ))){ return true ; }	
		}
		return false;
	}
	
	public String getBlockedUsersList(ResultSet rs) throws SQLException // Get List of Blocked Users
	{
		users = new ArrayList<>();
		while(rs.next()) 
		{  		   
		   InfringingUser user = InfringingUser.initializeUser("", rs.getString("User_ID"), "","");	   
		   users.add(user);
		}	
		return users.toString();
	}
	
	@POST
	@Path("/checkIfUserBlocked")
	@Produces(MediaType.APPLICATION_JSON)
	public String checkIfUserIsAlreadyBlocked(@QueryParam("user_id") String user_id) throws Exception
	{
		user_id = AesEncryption.decrypt(user_id);
		MySqlDb Db = new MySqlDb();
		
		Connection conn = Db.connect();
		Statement st = conn.createStatement();
		String sql = "select* from BlockedUsers;";
		ResultSet rs = (ResultSet) st.executeQuery(sql);
		if(isUserAlreadyBlocked(rs, user_id)== true)
		return AesEncryption.encrypt("User Already Blocked");
		return AesEncryption.encrypt("User not Blocked");
	}
	
	@POST
	@Path("/getBlockedUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public String getBlockedUsers() throws Exception
	{
		
		MySqlDb Db = new MySqlDb();
		
		Connection conn = Db.connect();
		Statement st = conn.createStatement();
		ResultSet rs = (ResultSet) st.executeQuery("select* from BlockedUsers;");
		String JsonResponseUsers = getBlockedUsersList(rs);
		return AesEncryption.encrypt(JsonResponseUsers);
	}
	@POST
	@Path("/UnBlockUser")
	@Produces(MediaType.APPLICATION_JSON)
	public String UnBlockUser(@QueryParam("user_id") String user_id) throws Exception
	{
		user_id = AesEncryption.decrypt(user_id);
		MySqlDb Db = new MySqlDb();
		Connection conn = Db.connect();
		Statement st = conn.createStatement();
	    st.executeUpdate("UPDATE infringingUsers SET state ='UnBlocked' where User_ID = '"+user_id+"';");
	    st.executeUpdate("UPDATE Users SET state ='UnBlocked' where id = '"+user_id+"';");
	    System.out.println("User unBlocked");
		return AesEncryption.encrypt("User Unblocked");
	}
	////////////////////////////////////////////////////////////////////////////
	@POST
	@Path("/Highlights")
	@Produces(MediaType.APPLICATION_JSON)
	public String get_InfringingUsers(@QueryParam("name") String Group_name ,@QueryParam("user_id") String user_id ,@QueryParam("specificUserFriends") boolean specificUserFriends) throws Exception
	{
		if(Group_name!=null) {Group_name = AesEncryption.decrypt(Group_name); }
		if(user_id!=null) {user_id = AesEncryption.decrypt(user_id); }
		
		MySqlDb db = new MySqlDb();
		String response = "";
		Connection conn = db.connect();
		Statement st = conn.createStatement();
		ResultSet rs = null;
		
		if(Group_name == null) { response = "No Parameters Passed.."; }
		if(specificUserFriends == false) { rs = getAllInfringingUsersFromDB(Group_name,st,rs ) ;}
		else { rs = getInfringingUsersFromDB(Group_name,user_id,st,rs ); }
		
		while(rs.next())
		{	
			InfringingUser user = InfringingUser.initializeUser(rs.getString("Name") ,rs.getString("User_ID") , rs.getString("Profile_pic"),rs.getString("state"));
			infringing_users.add(user);					 
		}
		System.out.println("QUERY SUCCESSFULL EXECUTED");
		conn.close();
		return AesEncryption.encrypt(infringing_users.toString());
	}
	
	@POST
	@Path("/UnReportUser")
	@Produces(MediaType.APPLICATION_JSON)
	public void UnReportUsers(@QueryParam("user_id") String User_ID) throws Exception
	{
		User_ID = AesEncryption.decrypt(User_ID);
		MySqlDb db = new MySqlDb();
		Connection conn = db.connect();
		Statement st = conn.createStatement();
		deleteInfringingUserFromDB(User_ID, st );
		conn.close();	
	}
	/////////// Get All InfringingUsers From DB
	public ResultSet getAllInfringingUsersFromDB(String Group_name, Statement st, ResultSet rs ) throws SQLException
	{
		
		if(Group_name.equals("All")){
			rs = (ResultSet) st.executeQuery("select distinct(infringingUsers.User_ID) ,infringingUsers.Name, infringingUsers.Profile_pic ,infringingUsers.state from groupTable,infringingUsers where groupTable.id=infringingUsers.Group_ID ;");
		}
		else{
			rs = (ResultSet) st.executeQuery("select distinct(infringingUsers.User_ID), infringingUsers.Name,  infringingUsers.Profile_pic, infringingUsers.state  from groupTable,infringingUsers where groupTable.id=infringingUsers.Group_ID and groupTable.name='"+Group_name+"';");
		}	
		return rs;	
	}
	
	// Get Selective InfringingUsers From DB
	public ResultSet getInfringingUsersFromDB(String Group_name, String user_id ,Statement st, ResultSet rs ) throws SQLException
	{
		if(Group_name.equals("All")){
			rs = (ResultSet) st.executeQuery("select distinct(infringingUsers.User_ID) ,infringingUsers.Name, infringingUsers.Profile_pic ,infringingUsers.state from groupTable,infringingUsers where groupTable.id=infringingUsers.Group_ID and infringingUsers.state = 'UnBlocked' and infringingUsers.Profile_Name = '"+user_id+"';");
		}
		else{
			rs = (ResultSet) st.executeQuery("select distinct(infringingUsers.User_ID), infringingUsers.Name,  infringingUsers.Profile_pic, infringingUsers.state  from groupTable,infringingUsers where groupTable.id=infringingUsers.Group_ID and infringingUsers.state = 'UnBlocked' and infringingUsers.Profile_Name ='"+user_id+"' and groupTable.name='"+Group_name+"';");
		}	
		return rs;	
	}
	// Delete InfringingUsers From DB
	public void deleteInfringingUserFromDB(String UserID , Statement st) throws SQLException
	{
		
		st.executeUpdate("delete from postTable where id in (select Post_ID from infringingUsers where Post_ID in (select DISTINCT(Post_ID) from infringingUsers where User_ID = '"+UserID+"') group by Post_ID having COUNT(DISTINCT User_ID) = 1);");	
		st.executeUpdate("delete from posts_comments_table where Post_ID in (select Post_ID from infringingUsers where Post_ID in (select DISTINCT(Post_ID) from infringingUsers where User_ID = '"+UserID+"') group by Post_ID having COUNT(DISTINCT User_ID) = 1);");	
		st.executeUpdate("delete from infringingUsers where User_ID = '"+UserID+"';");
	}
	
	
	
	
	
}