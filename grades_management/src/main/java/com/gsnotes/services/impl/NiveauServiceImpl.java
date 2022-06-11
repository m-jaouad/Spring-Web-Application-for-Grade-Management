package com.gsnotes.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gsnotes.bo.Niveau;
import com.gsnotes.dao.INiveauDao;
import com.gsnotes.services.INiveauService;
import com.gsnotes.bo.Module ;

@Service
@Transactional
public class NiveauServiceImpl implements INiveauService {
	@Autowired
	private INiveauDao niveauDao ;

	@Override
	public List<Niveau> getAllNiveaux() {
		
		return niveauDao.findAll() ;
	}

	@Override
	public Niveau getNiveauById(Long idNiveau) {
		
		return niveauDao.findById(idNiveau).get();
	}

	@Override
	public List<Module> getModuleOfNiveau(int idNiveau) {
		 
		return niveauDao.getById(Long.valueOf(idNiveau)).getModules();
	}

}
