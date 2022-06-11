package com.gsnotes.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gsnotes.bo.Etudiant;
import com.gsnotes.bo.InscriptionModule;
import com.gsnotes.dao.IInscriptionModuleDao;
import com.gsnotes.services.IinscriptionModuleService;

@Service
@Transactional
public class inscriptionModuleServiceImpl implements IinscriptionModuleService {
	
	@Autowired
	IInscriptionModuleDao insModDao ;

	@Override
	public List<Etudiant> findSessionNormaleStudents(int idModule, int year) {
		List<Etudiant> students = new ArrayList<>() ;
		
		List<InscriptionModule>  insModule = insModDao.findByModuleIdModuleAndInscriptionAnnuelleAnnee(Long.valueOf(idModule) , year) ;
		
		for (InscriptionModule insMod : insModule) {
			students.add(insMod.getInscriptionAnnuelle().getEtudiant());
		}
		System.out.println("### session normale  students ");
		for (Etudiant etu : students) {
			System.out.println(etu.getNom());
		}

		System.out.println("### session Normale students ");

		return students;
	}


}
