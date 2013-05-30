package com.gae.buzzmap;

import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Tweet {
	@Id Long Id;
	String text;
	Long from_user;
	String from_user_name;
	Date create_at;
	String state_name;
	String category;
	String profile_image_url;
	List<String> keywords;
	
	public void setId(long id) {
		this.Id = id;
	}
	
	public Long getId() {
		return this.Id;
	}
	
	public void setText(String content) {
		this.text = content;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void set_from_user(long from_user_id) {
		this.from_user = from_user_id;
	}
	
	public Long get_from_user() {
		return this.from_user;
	}
	
	public void set_from_user_name(String userName) {
		this.from_user_name = userName;
	}
	
	public String get_from_user_name() {
		return from_user_name;
	}
	
	public void set_Create_at(Date createDate) {
		this.create_at = createDate;
	}
	
	public Date get_Create_at() {
		return this.create_at;
	}
	
	public void setStatename(String stateName) {
		this.state_name = stateName;
	}
	
	public String getStatename() {
		return this.state_name;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	
	public List<String> getKeywords() {
		return this.keywords;
	}
	
	public void setProfileImageUrl(String url) {
		this.profile_image_url = url;
	}
	
	public String getProfileImageUrl() {
		return this.profile_image_url;
	}
}
