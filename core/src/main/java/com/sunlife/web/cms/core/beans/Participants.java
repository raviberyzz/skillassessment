package com.sunlife.web.cms.core.beans;

import java.util.List;

public class Participants {

	List<User> participants;
	String recruiter;
	String[] leadsEmail;
	String technology;

	public List<User> getParticipants() {
		return participants;
	}

	public void setParticipants(List<User> participants) {
		this.participants = participants;
	}

	public String getRecruiter() {
		return recruiter;
	}

	public void setRecruiter(String recruiter) {
		this.recruiter = recruiter;
	}

	public String[] getLeadsEmail() {
		return leadsEmail;
	}

	public void setLeadsEmail(String[] leadsEmail) {
		this.leadsEmail = leadsEmail;
	}


	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

}
