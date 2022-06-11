package com.gsnotes.services;

import java.util.List;

import com.gsnotes.bo.InscriptionAnnuelle;

public interface IInscriptionAnnuelleService {
	
	public List<InscriptionAnnuelle> getInscriptionsNiveauByYear(int idNiveau, int year) ;

}
