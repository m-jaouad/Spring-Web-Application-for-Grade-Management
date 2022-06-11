package com.gsnotes.bo;



import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;


/**
 * Represente un enseignant.
 * 
 * Un enseignant est un cas spéciale de l'Utilisateur
 * 
 * @author T. BOUDAA
 *
 */


@Entity
@PrimaryKeyJoinColumn(name = "idEnseighant")
public class Enseignant extends Utilisateur {


	
	private String specialite;
	
	@OneToMany(mappedBy = "prof", cascade = CascadeType.ALL, targetEntity = MatiereEnseignement.class)
	private List<MatiereEnseignement> matiereEnseignements ;


	

	public List<MatiereEnseignement> getMatiereEnseignement() {
		return matiereEnseignements;
	}




	public void setMatiereEnseignement(List<MatiereEnseignement> matiereEnseignement) {
		this.matiereEnseignements = matiereEnseignement;
	}




	public String getSpecialite() {
		return specialite;
	}




	public void setSpecialite(String specialite) {
		this.specialite = specialite;
	}





}