package com.gsnotes.services.impl;

import java.util.List;

import com.gsnotes.bo.Etudiant;

public class StudentsNotes {
	
	
	private List<Etudiant> students ;
	
	private double[][] elementsNotes ;

	public List<Etudiant> getStudents() {
		return students;
	}

	public void setStudents(List<Etudiant> students) {
		this.students = students;
	}

	public double[][] getElementsNotes() {
		return elementsNotes;
	}

	public void setElementsNotes(double[][] elementsNotes) {
		this.elementsNotes = elementsNotes;
	} 
	
	
}
