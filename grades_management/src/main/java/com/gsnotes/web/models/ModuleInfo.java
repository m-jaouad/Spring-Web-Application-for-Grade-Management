package com.gsnotes.web.models;

public class ModuleInfo {

	private String nom;

	private String prof;

	private String semestre;

	private int annnee;

	private String classe;

	private String session;

	public ModuleInfo() {

	}

	public ModuleInfo(String nom, String prof, String semestre, int annnee, String classe, String session) {
		this.nom = nom;
		this.prof = prof;
		this.semestre = semestre;
		this.annnee = annnee;
		this.classe = classe;
		this.session = session;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getProf() {
		return prof;
	}

	public void setProf(String prof) {
		this.prof = prof;
	}

	public String getSemestre() {
		return semestre;
	}

	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}

	public int getAnnnee() {
		return annnee;
	}

	public void setAnnnee(int annnee) {
		this.annnee = annnee;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

}
