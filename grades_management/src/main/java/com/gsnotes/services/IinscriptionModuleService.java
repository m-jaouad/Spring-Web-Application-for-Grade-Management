package com.gsnotes.services;

import java.util.List;

import com.gsnotes.bo.Etudiant;

public interface IinscriptionModuleService {
	
	public List<Etudiant> findSessionNormaleStudents(int idModule, int year) ;

}
