package com.gsnotes.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsnotes.bo.MatiereEnseignement;
import com.gsnotes.dao.MatiereEnseignementDao;
import com.gsnotes.services.MatiereEnseignementService;


@Service
public class MatiereEnseignementServiceImpl implements MatiereEnseignementService {
	
	@Autowired
	MatiereEnseignementDao matiereDao;
	
	@Override
	public List<MatiereEnseignement> findElementsProfs(int year, Long idElement) {
		
		return matiereDao.findByAnneeAndElementIdMatiere(year, idElement);
	}

}
