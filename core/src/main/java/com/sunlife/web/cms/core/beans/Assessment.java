package com.sunlife.web.cms.core.beans;

import java.util.List;

public class Assessment {	
	
	private String technology;
	
	private List<Question> questions;
	
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	

	public String getTechnology() {
		return technology;
	}
	public void setTechnology(String technology) {
		this.technology = technology;
	}
	public List<Question> getQuestions() {
		return questions;
	}
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}	
	
	@Override
	public String toString() {
		return "AssessmentResponse [technology=" + technology + ", questions=" + questions + "]";
	}
	
}
