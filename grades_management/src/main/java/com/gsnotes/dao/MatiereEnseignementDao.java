package com.gsnotes.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gsnotes.bo.MatiereEnseignement;


@Repository
public interface MatiereEnseignementDao extends JpaRepository<MatiereEnseignement, Long> {
	
	public List<MatiereEnseignement> findByAnneeAndElementIdMatiere(int year, Long idElement) ;

}
