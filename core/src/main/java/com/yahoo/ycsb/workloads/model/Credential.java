package com.yahoo.ycsb.workloads.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Credential implements Serializable {
		private static final long serialVersionUID = 1815300291490290091L;

		private String userName;
		private String temporaryPassword;
		private String password;
		private Boolean resetPasswordFlag;
		private PasswordHash passwordHash;
		private PasswordHash smsPasswordHash;
		private String userId;
		private String idpId = "-1";
		private Date lastLoginSuccess;
		private ArrayList<Date> loginFailureTimes;
		private String loginFailureIPs;
		private Boolean checkUserName;
		private Integer passwordStrength;
		private String passwordQuality;
	    private Date expiryDate;
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getTemporaryPassword() {
			return temporaryPassword;
		}
		public void setTemporaryPassword(String temporaryPassword) {
			this.temporaryPassword = temporaryPassword;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public Boolean getResetPasswordFlag() {
			return resetPasswordFlag;
		}
		public void setResetPasswordFlag(Boolean resetPasswordFlag) {
			this.resetPasswordFlag = resetPasswordFlag;
		}
		public PasswordHash getPasswordHash() {
			return passwordHash;
		}
		public void setPasswordHash(PasswordHash passwordHash) {
			this.passwordHash = passwordHash;
		}
		public PasswordHash getSmsPasswordHash() {
			return smsPasswordHash;
		}
		public void setSmsPasswordHash(PasswordHash smsPasswordHash) {
			this.smsPasswordHash = smsPasswordHash;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getIdpId() {
			return idpId;
		}
		public void setIdpId(String idpId) {
			this.idpId = idpId;
		}
		public Date getLastLoginSuccess() {
			return lastLoginSuccess;
		}
		public void setLastLoginSuccess(Date lastLoginSuccess) {
			this.lastLoginSuccess = lastLoginSuccess;
		}
		public ArrayList<Date> getLoginFailureTimes() {
			return loginFailureTimes;
		}
		public void setLoginFailureTimes(ArrayList<Date> loginFailureTimes) {
			this.loginFailureTimes = loginFailureTimes;
		}
		public String getLoginFailureIPs() {
			return loginFailureIPs;
		}
		public void setLoginFailureIPs(String loginFailureIPs) {
			this.loginFailureIPs = loginFailureIPs;
		}
		public Boolean getCheckUserName() {
			return checkUserName;
		}
		public void setCheckUserName(Boolean checkUserName) {
			this.checkUserName = checkUserName;
		}
		public Integer getPasswordStrength() {
			return passwordStrength;
		}
		public void setPasswordStrength(Integer passwordStrength) {
			this.passwordStrength = passwordStrength;
		}
		public String getPasswordQuality() {
			return passwordQuality;
		}
		public void setPasswordQuality(String passwordQuality) {
			this.passwordQuality = passwordQuality;
		}
		public Date getExpiryDate() {
			return expiryDate;
		}
		public void setExpiryDate(Date expiryDate) {
			this.expiryDate = expiryDate;
		}
		public static long getSerialversionuid() {
			return serialVersionUID;
		}
}
