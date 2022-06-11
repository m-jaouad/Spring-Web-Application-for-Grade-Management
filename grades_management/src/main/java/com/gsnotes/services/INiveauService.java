package com.gsnotes.services;

import java.util.List;

import com.gsnotes.bo.Niveau;

import com.gsnotes.bo.Module ;

public interface INiveauService {

	public List<Niveau> getAllNiveaux();

	public Niveau getNiveauById(Long idNiveau);

	public List<Module> getModuleOfNiveau(int idNiveau);

}
