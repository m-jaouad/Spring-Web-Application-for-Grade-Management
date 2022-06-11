package com.gsnotes.services;

import java.util.List;

import com.gsnotes.bo.MatiereEnseignement;

public interface MatiereEnseignementService {
	
	public List<MatiereEnseignement> findElementsProfs(int year, Long idElement) ;

}
