package com.gsnotes.bo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class MatiereEnseignement {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long idMatiereEnseignement ;
	
	private int annee ;
	
	@ManyToOne
	@JoinColumn(name = "idElement")
	private Element element ;
	
	@ManyToOne
	@JoinColumn(name = "idProf")
	private Enseignant prof ;

	public int getAnnee() {
		return annee;
	}

	public void setAnnee(int annee) {
		this.annee = annee;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public Enseignant getProf() {
		return prof;
	}

	public void setProf(Enseignant prof) {
		this.prof = prof;
	}
	
	
}
