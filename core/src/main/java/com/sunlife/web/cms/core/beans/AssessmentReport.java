package com.sunlife.web.cms.core.beans;

import java.util.List;

public class AssessmentReport {

	private String technology;
	private List<Question> correctQuestions;
	private List<Question> incorrectQuestions;
	private List<Question> unAttemptedQuestions;
	private boolean passed;
	private int correct;
	private double percentageObtained;
	private int totalQuestions;

	private String userId;
	private String userFirstName;
	private String userLastName;

	private String userEmail;

	public int getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(int totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public List<Question> getCorrectQuestions() {
		return correctQuestions;
	}

	public void setCorrectQuestions(List<Question> correctQuestions) {
		this.correctQuestions = correctQuestions;
	}

	public List<Question> getIncorrectQuestions() {
		return incorrectQuestions;
	}

	public void setIncorrectQuestions(List<Question> incorrectQuestions) {
		this.incorrectQuestions = incorrectQuestions;
	}

	public List<Question> getUnAttemptedQuestions() {
		return unAttemptedQuestions;
	}

	public void setUnAttemptedQuestions(List<Question> unAttemptedQuestions) {
		this.unAttemptedQuestions = unAttemptedQuestions;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public int getCorrect() {
		return correct;
	}

	public void setCorrect(int correct) {
		this.correct = correct;
	}

	public double getPercentageObtained() {
		return percentageObtained;
	}

	public void setPercentageObtained(double percentageObtained) {
		this.percentageObtained = percentageObtained;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}
