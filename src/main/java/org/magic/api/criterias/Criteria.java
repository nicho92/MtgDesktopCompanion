package org.magic.api.criterias;

import java.util.Arrays;
import java.util.List;

public class Criteria<T> {

	private String att;
	private OPERATOR operator;
	private T[] val;
		
	public enum OPERATOR { EQ,LIKE,GREATER,LOWER, HAS }
	
	@SafeVarargs
	public Criteria(String att, OPERATOR operator, T... val) {
		this.att = att;
		this.operator = operator;
		this.val = val;
	}
	

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

	public void setVal(T[] val) {
		this.val = val;
	}
	
	public T[] getVal() {
		return val;
	}
	
	public T getFirst()
	{
		return val[0];
	}
	
	public boolean isList()
	{
		return val.length>1;
	}
	
	public List<T> toList()
	{
		return Arrays.asList(val);
	}
	
	
	
	@Override
	public String toString() {
		
		if(!isList())
			return att + " " + operator +" " +getFirst();
		
		return att + " " + operator +" " +Arrays.toString(val);
	}
	
}
