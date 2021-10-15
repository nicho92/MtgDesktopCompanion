package org.magic.api.beans.shop;

import java.io.Serializable;

public class Contact implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private int id=-1;
	private String name;
	private String lastName;
	private String email;
	private String telephone;
	private String country;
	private String address;
	private String zipCode;
	private String city;
	private String website;
	private boolean emailAccept=true;
	private String password="changeit";
	private boolean active=false;
	private String temporaryToken;
	

	public boolean equals(Object obj) {
	    if(!(obj instanceof Contact))
	    	return false;
	    
	    return ((Contact)obj).getId()==getId();
	}
	

	
	public String getTemporaryToken() {
		return temporaryToken;
	}

	public void setTemporaryToken(String temporaryToken) {
		this.temporaryToken = temporaryToken;
	}
	
	@Override
	public int hashCode() {
		return getId();
	}
	
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getWebsite() {
		return website;
	}

	public boolean isEmailAccept() {
		return emailAccept;
	}
	
	public void setEmailAccept(boolean emailAccept) {
		this.emailAccept = emailAccept;
	}
	

	public void setWebsite(String website) {
		this.website = website;
	}


	@Override
	public String toString() {
		return getName() +" " + getLastName();
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	
}
