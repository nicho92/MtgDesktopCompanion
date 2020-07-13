package org.magic.api.criterias;

public class Criteria {
	
	private String att;
	private OPERATOR operator;
	private Object val;
	
	
	public enum OPERATOR { EQ,LIKE,GREATER,LOWER }
	
	
	public String getAtt() {
		return att;
	}

	public void setAtt(String att) {
		this.att = att;
	}

	public OPERATOR getOperator() {
		return operator;
	}

	public void setOperator(OPERATOR operator) {
		this.operator = operator;
	}

	public Object getVal() {
		return val;
	}

	public void setVal(Object val) {
		this.val = val;
	}

	public Criteria add(String att, OPERATOR t,Object val)
	{
		Criteria c = new Criteria();
		c.setAtt(att);
		c.setOperator(t);
		c.setVal(val);
		return c;
	}
	
	public static void main(String[] args) {
		Criteria c = new Criteria();
		c.add("Name", OPERATOR.EQ, "Liliana of the veil");
		
		
	}
	
	
	
	
}
