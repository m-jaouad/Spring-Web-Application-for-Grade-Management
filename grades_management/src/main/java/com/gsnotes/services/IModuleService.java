package com.gsnotes.services;


import java.util.List;

import com.gsnotes.bo.Element;
import com.gsnotes.bo.Module;

public interface IModuleService  {
	
	public Module getModuleById(int idModule) ;
	
	public List<Element> getElementsOfModule(int idModule) ;
	
}
