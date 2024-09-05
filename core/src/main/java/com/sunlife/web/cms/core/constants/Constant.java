package com.sunlife.web.cms.core.constants;

public interface Constant {

	public final class Paths {
		private Paths() {
			throw new IllegalStateException("This is a constant class and cannot be instantiated");
		}

		public static final String BASE_CONTENT_FRAGMENT_FOLDER_PATH = "/content/dam/skillassessment/content-fragments/";
		public static final String ASSESSMENT_MODEL_PATH = "/conf/skillassessment/settings/dam/cfm/models/assessment";
		public static final String ASSESSMENT_REPORT_PATH = "/var/skillassessment/assessmentreport/";
		public static final String TECHNOLOGY_GENERIC_LIST_PATH = "/etc/acs-commons/lists/technolgoies/jcr:content/list";
		public static final String TECHNOLOGY_LEADS_LIST_PATH = "/etc/acs-commons/lists/technology-leads/jcr:content/list";
		public static final String UPCOMING_ASSESSMENT_PATH = "/var/skillassessment/upcomingassessment/";
		public static final String PARTICIPANT_USRES_PATH = "/home/users/participants";
		public static final String ASSESSMENT_REPORT_EMAIL_TEMPLATE_PATH="/content/dam/skillassessment/emailtemplate/assessmentreport.html";
		public static final String ASSESSMENT_SCHEDULE_EMAIL_TEMPLATE_PATH="/content/dam/skillassessment/emailtemplate/assessmentschedule.html";
		
		public static final String LOGIN_PAGE_URL = "/content/skillassessment/us/en/skill-assessment/login";
		public static final String DEFAULT_PAGE = "/content/skillassessment/us/en/redirect";
		public static final String BASE_PAGE_PATH = "/content/skillassessment/us/en/skill-assessment/";
		public static final String RECRUITER_PAGE_URL="/content/skillassessment/us/en/skill-assessment/recruiter.html";
	}

	public final class ServiceUser {
		private ServiceUser() {
			throw new IllegalStateException("This class cannot be instantiated");
		}

		public static final String SKILLASSESSMENT_USER = "skill-assessment-user";
	}

	public final class SlingJob {

		private SlingJob() {
			throw new IllegalStateException("This class cannot be instantiated");
		}

		public static final String ASSESSMENT_EXPIRATION_TOPIC = "topic/assessmentexpiration";
		public static final String ASSESSMENT_REPORT_EMAIL_TOPIC="topic/assessmentreportemail";
		public static final String ASSESSMENT_SCHEDULE_EMAIL_TOPIC="topic/assessmentscheduleemail";
	}

	public final class GenericConstant {
		private GenericConstant() {
			throw new IllegalStateException("This class cannot be instantiated");
		}

		public static final String USER_ID = "userId";
		public static final String MESSAGE = "message";
		public static final String ASSESSMENT_PATH = "assessmentPath";
		public static final String ID = "id";
		public static final String FIRST_NAME = "firstName";
		public static final String LAST_NAME = "lastName";
		public static final String TECHNOLOGY = "technology";
		public static final String EMAIL = "email";
		public static final String FORWARD_SLASH="/";
	}

	public final class ResponseConstants {
		private ResponseConstants() {
			throw new IllegalStateException("This class cannot be instantiated");
		}

		public static final String MESSAGE = "message";
		public static final String STATUS = "status";
		public static final String JSON_RESPONSE_TYPE = "application/json";
	}

	public final class SQL2Query {
		public static final String CONTENT_FRAGMENT_BY_PATH_MODEL = "SELECT * FROM [dam:Asset] AS asset WHERE ischildnode(asset ,\"%s\") and [jcr:content/contentFragment] IS NOT NULL and [jcr:content/data/cq:model] ='%s'";
	}

	public final class Extensions {
		public static final String HTML = ".html";
	}

	public final class EmailConstants {

		public static final String FROM_EMAIL_ADDRESS = "slf-from-email-address";
		public static final String TO_EMAIL_ADDRES = "slf-to-email-address";
		public static final String CC_EMAIL_ADDRES = "slf-cc-email-address";
		public static final String BCC_EMAIL_ADDRES = "slf-bcc-email-address";
		public static final String EMAIL_SUBJECT = "slf-email-subject";
		public static final String EMAIL_BODY = "slf-email-body";
		public static final String API_KEY = "slf-api-key";
	}

}
