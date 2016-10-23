package com.muaavin.webservices.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONException;
import org.json.JSONObject;

@XmlRootElement
public class InfringingUser {

	String Post_Id;
	int Group_Id;
	String Profile_Name;
	
	@XmlElement(name="User_ID")
	double User_ID;
	
	public InfringingUser(){        
    }
	
	public InfringingUser(double user_id) {
		User_ID = user_id;
    } 
	
	 @Override
	    public String toString(){
	        try {
	            // takes advantage of toString() implementation to format {"a":"b"}
	            return new JSONObject().put("User_ID", User_ID).toString();
	        } catch (JSONException e) {
	            return null;
	        }
	    }
	
}
