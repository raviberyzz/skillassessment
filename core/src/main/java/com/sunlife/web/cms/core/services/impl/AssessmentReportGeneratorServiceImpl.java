package com.sunlife.web.cms.core.services.impl;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunlife.web.cms.core.beans.Assessment;
import com.sunlife.web.cms.core.beans.AssessmentReport;
import com.sunlife.web.cms.core.beans.Question;
import com.sunlife.web.cms.core.constants.Constant;
import com.sunlife.web.cms.core.constants.Constant.Paths;
import com.sunlife.web.cms.core.services.AssessmentReportGeneratorService;
import com.sunlife.web.cms.core.utils.SlingResourceUtil;

@Component(service = AssessmentReportGeneratorService.class, immediate = true)
public class AssessmentReportGeneratorServiceImpl implements AssessmentReportGeneratorService {

	private static final double PASSING_SCORE = 50.0;
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

	@Override
	public AssessmentReport generateAssessmentReport(ResourceResolver resolver, Assessment assessmentModel) {
		AssessmentReport assessmentReport = new AssessmentReport();
		setUserInfo(assessmentReport, assessmentModel);
		List<Question> questions = assessmentModel.getQuestions();
		generateReport(assessmentReport, resolver, questions);
		return assessmentReport;
	}

	private void setUserInfo(AssessmentReport assessmentReport, Assessment assessmentModel) {
		assessmentReport.setTechnology(assessmentModel.getTechnology());
		assessmentReport.setUserId(assessmentModel.getUser().getId());
		assessmentReport.setUserFirstName(assessmentModel.getUser().getFirstName());
		assessmentReport.setUserLastName(assessmentModel.getUser().getLastName());
		assessmentReport.setUserEmail(assessmentModel.getUser().getEmail());
	}

	private void generateReport(AssessmentReport report, ResourceResolver resolver, List<Question> questions) {

		List<Question> correctQuestions = new ArrayList<>();
		List<Question> incorrectQuestions = new ArrayList<>();
		List<Question> unAttemptedQuestions = new ArrayList<>();

		questions.forEach(question -> {
			String cfPath = Constant.Paths.BASE_CONTENT_FRAGMENT_FOLDER_PATH + report.getTechnology() + "/"
					+ question.getId();
			Resource cfResource = resolver.getResource(cfPath);

			if (cfResource != null) {
				ContentFragment contentFragment = cfResource.adaptTo(ContentFragment.class);
				String[] correctOptions = getCorrectOptions(contentFragment);
				List<String> selectedOptions = question.getSelectedOptions();

				if (selectedOptions == null || selectedOptions.isEmpty()) {
					unAttemptedQuestions.add(question);
				} else if (isCorrectResponse(selectedOptions, correctOptions)) {
					correctQuestions.add(question);
				} else {
					incorrectQuestions.add(question);
				}
			}
		});

		int correctCount = correctQuestions.size();
		double percentage = calculatePercentage(correctCount, questions.size());
		report.setCorrectQuestions(correctQuestions);
		report.setIncorrectQuestions(incorrectQuestions);
		report.setUnAttemptedQuestions(unAttemptedQuestions);
		report.setTotalQuestions(questions.size());
		report.setCorrect(correctCount);
		report.setPercentageObtained(percentage);
		report.setPassed(percentage >= PASSING_SCORE);
	}

	private String[] getCorrectOptions(ContentFragment contentFragment) {
		return (String[]) contentFragment.getElement("correctResponse").getValue().getValue();
	}

	private boolean isCorrectResponse(List<String> selectedOptions, String[] correctOptions) {
		String[] selectedOptionsArray = selectedOptions.toArray(new String[0]);
		Arrays.sort(correctOptions);
		Arrays.sort(selectedOptionsArray);
		return Arrays.equals(selectedOptionsArray, correctOptions);
	}

	private double calculatePercentage(int correctCount, int totalQuestions) {
		double percent = ((double) correctCount / totalQuestions) * 100;
		return Double.parseDouble(DECIMAL_FORMAT.format(percent));
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean storeAssessmentReport(ResourceResolver resolver, AssessmentReport assessmentReport)
			throws PersistenceException, JsonProcessingException {
		LocalDate currentDate = LocalDate.now();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> assessmentMap = mapper.convertValue(assessmentReport, Map.class);
		assessmentMap.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
		String path = Paths.ASSESSMENT_REPORT_PATH + assessmentReport.getTechnology() + "/" + currentDate.getYear()
				+ "/" + currentDate.getMonthValue() + "/" + currentDate.getDayOfMonth() + "/"
				+ assessmentReport.getUserId();

		assessmentMap.put("correctQuestions", convertListToArray(mapper, assessmentReport.getCorrectQuestions()));
		assessmentMap.put("incorrectQuestions", convertListToArray(mapper, assessmentReport.getIncorrectQuestions()));
		assessmentMap.put("unAttemptedQuestions",
				convertListToArray(mapper, assessmentReport.getUnAttemptedQuestions()));
		Resource resource = SlingResourceUtil.getOrCreateResource(resolver, path, assessmentMap);
		return Objects.nonNull(resource);
	}

	private String[] convertListToArray(ObjectMapper mapper, List<Question> itemList) {
		return itemList.stream().map(item -> {
			try {
				return mapper.writeValueAsString(item);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}).toArray(String[]::new);
	}
}