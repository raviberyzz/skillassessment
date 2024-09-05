package com.sunlife.web.cms.core.beans;

import java.util.List;

public class Question {

	private String id;

	private List<String> selectedOptions;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getSelectedOptions() {
		return selectedOptions;
	}

	public void setSelectedOptions(List<String> selectedOptions) {
		this.selectedOptions = selectedOptions;
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", selectedOptions=" + selectedOptions + "]";
	}

}
