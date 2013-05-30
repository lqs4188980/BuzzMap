package com.gae.buzzmap;

public class SearchKeywords {
	String category;
	String stateName;
	public SearchKeywords(String category, String statename) {
		// TODO Auto-generated constructor stub
		setCategory(category);
		setStateName(statename);
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public void setStateName(String statename) {
		this.stateName = statename;
	}
	
	public String getStateName() {
		return this.stateName;
	}
}
