package com.gsnotes.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.gsnotes.bo.InscriptionModule;

public interface IInscriptionModuleDao extends JpaRepository<InscriptionModule, Long> {
	
	public List<InscriptionModule> findByModuleAndInscriptionAnnuelleAnnee(com.gsnotes.bo.Module module, int year);
	
	public List<InscriptionModule> findByValidationAndModuleAndInscriptionAnnuelleAnnee(String validation,
			com.gsnotes.bo.Module module, int year);
	
	public List<InscriptionModule> findByModuleIdModuleAndInscriptionAnnuelleAnnee( Long idModule, int year);
}
