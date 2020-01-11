package org.magic.api.beans;

public class Grading {

	private Grader grader;
	private String numberID;
	private Double gradeNote=0.0;
	private EnumCondition grade;
	private EnumCondition subGrade;
	private Double weight=0.0;
	private Double thickness=0.0;
	private Double centering=0.0;
	private Double corners=0.0;
	private Double edges=0.0;
	private Double surface=0.0;
	
	
	@Override
	public String toString() {
		return getGrader() +" " + getGradeNote();
	}
	
	public Grader getGrader() {
		return grader;
	}
	public void setGrader(Grader grader) {
		this.grader = grader;
	}
	public String getNumberID() {
		return numberID;
	}
	public void setNumberID(String numberID) {
		this.numberID = numberID;
	}
	public Double getGradeNote() {
		return gradeNote;
	}
	public void setGradeNote(Double gradeNote) {
		this.gradeNote = gradeNote;
	}
	public EnumCondition getGrade() {
		return grade;
	}
	public void setGrade(EnumCondition grade) {
		this.grade = grade;
	}
	public EnumCondition getSubGrade() {
		return subGrade;
	}
	public void setSubGrade(EnumCondition subGrade) {
		this.subGrade = subGrade;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public double getThickness() {
		return thickness;
	}
	public void setThickness(double thickness) {
		this.thickness = thickness;
	}
	public double getCentering() {
		return centering;
	}
	public void setCentering(double centering) {
		this.centering = centering;
	}
	public double getCorners() {
		return corners;
	}
	public void setCorners(double corners) {
		this.corners = corners;
	}
	public double getEdges() {
		return edges;
	}
	public void setEdges(double edges) {
		this.edges = edges;
	}
	public double getSurface() {
		return surface;
	}
	public void setSurface(double surface) {
		this.surface = surface;
	}
	
	
	
	
}
