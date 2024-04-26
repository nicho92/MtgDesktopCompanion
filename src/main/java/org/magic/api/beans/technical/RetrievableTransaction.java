package org.magic.api.beans.technical;

import java.util.Date;

public class RetrievableTransaction {

	private String comments;
	private MoneyValue totalValue;
	private Date dateTransaction;
	private String source;
	private String sourceId;
	private String url;
	
	@Override
	public String toString() {
		return getSourceId();
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public void setTotalValue(MoneyValue totalValue) {
		this.totalValue = totalValue;
	}
	
	public MoneyValue getTotalValue() {
		return totalValue;
	}
	
	
	public Date getDateTransaction() {
		return dateTransaction;
	}
	public void setDateTransaction(Date dateTransaction) {
		this.dateTransaction = dateTransaction;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
