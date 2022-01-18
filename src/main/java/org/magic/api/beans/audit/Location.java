package org.magic.api.beans.audit;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Location implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String city;
	private String region;
	private String country;
	@SerializedName(value = "country_name")private String countryName;
	@SerializedName(value = "country_code")private String countryCode;
	@SerializedName(value = "continent_code") private String continentCode;
	private Double latitude;
	private Double longitude;
	private String timezone;
	@SerializedName(value = "country_area")private Double countryArea;
	private String operator;
		
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getRegion() {
			return region;
		}
		public void setRegion(String region) {
			this.region = region;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		public String getCountryName() {
			return countryName;
		}
		public void setCountryName(String countryName) {
			this.countryName = countryName;
		}
		public String getCountryCode() {
			return countryCode;
		}
		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}
		public String getContinentCode() {
			return continentCode;
		}
		public void setContinentCode(String continentCode) {
			this.continentCode = continentCode;
		}
		public Double getLatitude() {
			return latitude;
		}
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		public Double getLongitude() {
			return longitude;
		}
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}
		public String getTimezone() {
			return timezone;
		}
		public void setTimezone(String timezone) {
			this.timezone = timezone;
		}
		public Double getCountryArea() {
			return countryArea;
		}
		public void setCountryArea(Double countryArea) {
			this.countryArea = countryArea;
		}
		public String getOperator() {
			return operator;
		}
		public void setOperator(String operator) {
			this.operator = operator;
		}
}
