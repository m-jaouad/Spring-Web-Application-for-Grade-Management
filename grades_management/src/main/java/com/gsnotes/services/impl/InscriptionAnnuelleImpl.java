package com.gsnotes.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gsnotes.bo.InscriptionAnnuelle;
import com.gsnotes.bo.Niveau;
import com.gsnotes.dao.IInscriptionAnnuelleDao;
import com.gsnotes.services.IInscriptionAnnuelleService;


@Service
@Transactional
public class InscriptionAnnuelleImpl implements IInscriptionAnnuelleService {
	
	@Autowired
	IInscriptionAnnuelleDao inscriptionDao ;

	@Override
	public List<InscriptionAnnuelle> getInscriptionsNiveauByYear(int idNiveau, int year) {
		
		Niveau niveau = new Niveau();
		niveau.setIdNiveau(Long.valueOf(idNiveau));
		
		return inscriptionDao.findByNiveauAndAnnee(niveau, year) ;
	}

}
