package com.performance.helper;

import java.time.LocalDate;

public class RegisterParticipationRequest {

	 private String activityType;
	    private String title;
	    private LocalDate date;
	    private String level;
	    private String role;
	    private String description;
		
		public RegisterParticipationRequest(String activityType, String title, LocalDate date, String level,
				String role, String description) {
			super();
			this.activityType = activityType;
			this.title = title;
			this.date = date;
			this.level = level;
			this.role = role;
			this.description = description;
		}
		public String getActivityType() {
			return activityType;
		}
		public void setActivityType(String activityType) {
			this.activityType = activityType;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public LocalDate getDate() {
			return date;
		}
		public void setDate(LocalDate date) {
			this.date = date;
		}
		public String getLevel() {
			return level;
		}
		public void setLevel(String level) {
			this.level = level;
		}
		public String getRole() {
			return role;
		}
		public void setRole(String role) {
			this.role = role;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	    
	    
}
