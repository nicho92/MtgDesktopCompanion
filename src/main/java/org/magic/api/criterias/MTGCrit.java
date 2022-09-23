package org.magic.api.criterias;

import java.util.Arrays;
import java.util.List;

public class MTGCrit<T> {

	private String att;
	private OPERATOR operator;
	private T[] val;

	public enum OPERATOR { EQ,START_WITH,END_WITH, LIKE,GREATER,LOWER,GREATER_EQ,LOWER_EQ, IN }

	@SafeVarargs
	public MTGCrit(String att, OPERATOR operator, T... val) {
		this.att = att;
		this.operator = operator;
		this.val = val;
	}

	@SafeVarargs
	public MTGCrit(QueryAttribute att, OPERATOR operator, T... val) {
		this.att = att.getName();
		this.operator = operator;
		this.val = val;
	}

	public Class getType()
	{
		return val[0].getClass();
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