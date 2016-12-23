create database Muaavin;
use  Muaavin;
create table groupTable(id int unsigned auto_increment primary key not null, name varchar(50) not null);
create table infringingUsers(Post_ID varchar(100) ,Group_ID int,User_ID varchar(100) not null, Profile_pic varchar(1000),Comment_ID varchar(100) not null, Name varchar(100) not null,  Profile_Name varchar(50),state varchar(100) not null,PRIMARY KEY (Post_ID,Group_ID,User_ID,Comment_ID));
create table Users(id varchar(100) not null,name varchar(100) not null, profilePic varchar(1000) not null, state varchar(100) not null , PRIMARY KEY(id) );
create table postTable(id varchar(100) not null, group_name varchar(50) not null, post_Detail varchar(2000),User_ID varchar(100) not null ,  Post_Image varchar(1000), PRIMARY KEY(id,group_name,User_ID));
create table Comments(User_ID varchar(100) not null,Parent_Comment_id varchar(100) not null,Comment_ID varchar(100) not null,Group_Name varchar(50) not null, Comment varchar(2000), PRIMARY KEY(Comment_ID,User_ID,Group_Name,Parent_Comment_id));
create table Posts_Comments_Table(Post_ID varchar(100) not null , Comment_ID varchar(100) not null , PRIMARY KEY(Post_ID,Comment_ID));
create table userFeedBack(id int unsigned auto_increment primary key not null, post_id varchar(100) not null, user_id varchar(100) not null, comment varchar(2000));
create table ThumbsDown(post_id varchar(100) not null, user_id varchar(100) not null, PRIMARY KEY(post_id,user_id));


CREATE VIEW Group_A AS
select distinct postTable.id, Comments.Parent_Comment_id, Comments.Comment_ID, infringingUsers.Name,infringingUsers.Profile_pic,postTable.Post_Image,Comments.Comment, postTable.post_Detail
from postTable, Comments, infringingUsers, Posts_Comments_Table
where postTable.id=Posts_Comments_Table.Post_ID and Posts_Comments_Table.Comment_ID = Comments.Comment_ID and infringingUsers.Comment_ID = Comments.Comment_ID and Comments.Group_Name = 'A';

CREATE VIEW Group_B AS
select distinct postTable.id, Comments.Parent_Comment_id, Comments.Comment_ID, infringingUsers.Name,infringingUsers.Profile_pic,postTable.Post_Image,Comments.Comment, postTable.post_Detail
from postTable, Comments, infringingUsers, Posts_Comments_Table
where postTable.id=Posts_Comments_Table.Post_ID and Posts_Comments_Table.Comment_ID = Comments.Comment_ID and infringingUsers.Comment_ID = Comments.Comment_ID and Comments.Group_Name = 'B';


CREATE VIEW Group_C AS
select distinct postTable.id, Comments.Parent_Comment_id, Comments.Comment_ID, infringingUsers.Name,infringingUsers.Profile_pic,postTable.Post_Image,Comments.Comment, postTable.post_Detail
from postTable, Comments, infringingUsers, Posts_Comments_Table
where postTable.id=Posts_Comments_Table.Post_ID and Posts_Comments_Table.Comment_ID = Comments.Comment_ID and infringingUsers.Comment_ID = Comments.Comment_ID and Comments.Group_Name = 'C';

CREATE VIEW Group_All AS
select distinct postTable.id, Comments.Parent_Comment_id, Comments.Comment_ID, infringingUsers.Name,infringingUsers.Profile_pic,postTable.Post_Image,Comments.Comment, postTable.post_Detail
from postTable, Comments, infringingUsers, Posts_Comments_Table
where postTable.id=Posts_Comments_Table.Post_ID and Posts_Comments_Table.Comment_ID = Comments.Comment_ID and infringingUsers.Comment_ID = Comments.Comment_ID and Comments.Group_Name = postTable.group_name;

CREATE VIEW ThumbsDown_View AS
select ThumbsDown.post_id, count(*) AS  total_Unlikes
from ThumbsDown
group by ThumbsDown.post_id;

CREATE VIEW GroupA_postDetail AS select * from Group_A left join (ThumbsDown_View) on Group_A.id = ThumbsDown_View.post_id;
CREATE VIEW GroupB_postDetail AS select * from Group_B left join (ThumbsDown_View) on Group_B.id = ThumbsDown_View.post_id;
CREATE VIEW GroupC_postDetail AS select * from Group_C left join (ThumbsDown_View) on Group_C.id = ThumbsDown_View.post_id;
CREATE VIEW GroupAll_postDetail AS select * from Group_All left join (ThumbsDown_View) on Group_All.id = ThumbsDown_View.post_id;


create table BlockUser(UserID varchar(100) not null PRIMARY KEY);

CREATE VIEW BlockedUsers AS
select User_ID from infringingUsers
where state = 'Blocked'
union
select id from users
where state = 'Blocked';
