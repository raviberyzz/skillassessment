package com.sunlife.web.cms.core.beans;

import java.util.Arrays;

public class AssessmentTemplate {

	private String question;
	private String[] options;
	private String[] correctResponse;
	private boolean multipleChoice;
	private String id;

	public AssessmentTemplate() {

	}

	public AssessmentTemplate(String question, String[] options, String[] correctResponse, boolean multipleChoice) {
		super();
		this.question = question;
		this.options = options;
		this.correctResponse = correctResponse;
		this.multipleChoice = multipleChoice;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public String[] getCorrectResponse() {
		return correctResponse;
	}

	public void setCorrectResponse(String[] correctResponse) {
		this.correctResponse = correctResponse;
	}

	public boolean isMultipleChoice() {
		return multipleChoice;
	}

	public void setMultipleChoice(boolean multipleChoice) {
		this.multipleChoice = multipleChoice;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AssessmentTemplate(String question, String[] options, String[] correctResponse, boolean multipleChoice,
			String id) {
		super();
		this.question = question;
		this.options = options;
		this.correctResponse = correctResponse;
		this.multipleChoice = multipleChoice;
		this.id = id;
	}

	@Override
	public String toString() {
		return "AssessmentTemplate [question=" + question + ", options=" + Arrays.toString(options)
				+ ", correctResponse=" + Arrays.toString(correctResponse) + ", multipleChoice=" + multipleChoice
				+ ", id=" + id + "]";
	}
	

}
