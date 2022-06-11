package com.gsnotes.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gsnotes.bo.Element;
import com.gsnotes.bo.Module;
import com.gsnotes.dao.IModuleDao;
import com.gsnotes.services.IModuleService;


@Service
@Transactional
public class ModuleServiceImpl implements IModuleService {

	@Autowired
	IModuleDao moduleDao;

	@Override
	public Module getModuleById(int idModule) {

		return moduleDao.findById(Long.valueOf(idModule)).get();
	}

	@Override
	public List<Element> getElementsOfModule(int idModule) {
		
		return getModuleById(idModule).getElements();
	}

}
