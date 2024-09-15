package org.magic.api.beans.technical.audit;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Location implements Serializable {

	private static final long serialVersionUID = 1L;
	private String city;
	private String region;
	private String country;
	@SerializedName(value = "country_code")private String countryCode;
	@SerializedName(value = "continent_code") private String continentCode;
	private Double latitude;
	private Double longitude;
	private String timezone;
	private String operator;

		public void setAll(String string) {
				setCity(string);
				setRegion(string);
				setCountry(string);
				setCountryCode(string);
				setContinentCode(string);
		}
	
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
		public String getOperator() {
			return operator;
		}
		public void setOperator(String operator) {
			this.operator = operator;
		}
	
}
