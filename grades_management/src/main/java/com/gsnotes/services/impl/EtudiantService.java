package com.gsnotes.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gsnotes.bo.Element;
import com.gsnotes.bo.Etudiant;
import com.gsnotes.bo.InscriptionAnnuelle;
import com.gsnotes.bo.InscriptionMatiere;
import com.gsnotes.bo.InscriptionModule;
import com.gsnotes.bo.Module;
import com.gsnotes.bo.Utilisateur;
import com.gsnotes.dao.IInscriptionModuleDao;
import com.gsnotes.services.IEtudiantService;
import com.gsnotes.services.IinscriptionModuleService;
import com.gsnotes.utils.export.ExcelExporter;
import com.gsnotes.web.models.ModuleInfo;

@Service
@Transactional
public class EtudiantService implements IEtudiantService {

	@Autowired
	private IinscriptionModuleService insModuleService;

	@Autowired
	private IInscriptionModuleDao insModDao;

	@Override
	public ExcelExporter prepareStundentsExport(List<Etudiant> etudiants, List<Element> elements,
			ModuleInfo moduleInfo,  double noteValidation) {
		String[] columnNames = new String[10];
		columnNames[0] = "ID";
		columnNames[1] = "CNE";
		columnNames[2] = "Nom";
		columnNames[3] = "Prenom";

		// an array of element coefficients
		List<Integer> coeffs = new ArrayList<>();

		// ajouter les noms des elements du module
		int j = 4;

		for (Element element : elements) {
			columnNames[j] = element.getNom();
			coeffs.add((int) element.getCurrentCoefficient());
			j++;
		}

		// puis ajouter les columns Note Module , Validation
		columnNames[j++] = "Moyenne";
		columnNames[j++] = "Validation";
		int numberOfColumns = 4 + elements.size()+ 2 ;
		Object[][] data = new Object[etudiants.size()][numberOfColumns];

		int i = 0;
		for (Etudiant e : etudiants) {
			data[i][0] = e.getIdUtilisateur();
			data[i][1] = e.getCne();
			data[i][2] = e.getNom();
			data[i][3] = e.getPrenom();

			i++;
		}

		ExcelExporter excel = new ExcelExporter(columnNames, data, "etudiants");

		excel.fillSheet();
		excel.SetFormulas(coeffs,noteValidation,  true);

		excel.addDataTosheet(0, moduleInfo);

		return excel;
	}

	// une premiere version du methode pour retourner les etudiants qui ont un rattrappage , mais 
	// sans les notes du module 
	@Override
	public List<Etudiant> getRattrapedStudents(int idModule, int year) {

		com.gsnotes.bo.Module mod = new Module();
		mod.setIdModule(Long.valueOf(idModule));

		// list of Rattraped students
		List<Etudiant> students = new ArrayList<>();

		List<InscriptionModule> insModules = insModDao.findByValidationAndModuleAndInscriptionAnnuelleAnnee("R", mod,
				year);

		for (InscriptionModule insMod : insModules) {
			students.add(insMod.getInscriptionAnnuelle().getEtudiant());
		}
		System.out.println("### ratt students ");
		for (Etudiant etu : students) {
			System.out.println(etu.getNomArabe());
		}

		System.out.println("### ratt students ");

		return students;
	}

	
	// une 2eme  version du methode pour retourner les etudiants qui ont un rattrappage , mais 
	// avec le notes de elements du session normale
	
	@Override
	public StudentsNotes getRattrapedStudentsV2(int idModule, int year) {

		com.gsnotes.bo.Module mod = new Module();
		mod.setIdModule(Long.valueOf(idModule));

		// list of Rattraped students
		List<Etudiant> students = new ArrayList<>();

		// un tableau des notes pour l'ajouter au fichier des collectes des notes
		double[][] elementsNotes = null;

		List<InscriptionModule> insModules = insModDao.findByValidationAndModuleAndInscriptionAnnuelleAnnee("R", mod,
				year);

		int studentsNumbers = insModules.size();
		int elementsNumbers = insModules.get(0).getInscriptionAnnuelle().getInscriptionMatieres().size();

		int i = 0;
		for (InscriptionModule insMod : insModules) {
			students.add(insMod.getInscriptionAnnuelle().getEtudiant());

			if (elementsNotes == null) {
				elementsNotes = new double[studentsNumbers][elementsNumbers];
			}

			int k = 0;
			for (InscriptionMatiere insMatiere : insMod.getInscriptionAnnuelle().getInscriptionMatieres()) {
				elementsNotes[i][k] = insMatiere.getNoteSN();
				k++;
			}

			i++;

			System.out.println(insMod.getIdInscriptionModule());
		}

		System.out.println("### ratt students ");
		for (Etudiant etu : students) {
			System.out.println(etu.getNomArabe());
		}

		System.out.println("### ratt students ");

		System.out.println("### note  du session normale students ");
		for (double[] student : elementsNotes) {
			for (double note : student) {
				System.out.println(note);
			}
		}

		System.out.println("###  ");

		StudentsNotes studentsNotes = new StudentsNotes();
		studentsNotes.setStudents(students);
		studentsNotes.setElementsNotes(elementsNotes);

		return studentsNotes;
	}
	
	/**
	 * une methode qui a pour mission de preparer un fichier excel de collecte de notes 
	 *  pour les etudiants qui vont passer un rattrapage 
	 */

	@Override
	public ExcelExporter prepareRattrapedStundents(StudentsNotes studentsNotes, List<Element> elements,
			ModuleInfo moduleInfo, double noteValidation) {
		String[] columnNames = new String[10];
		columnNames[0] = "ID";
		columnNames[1] = "CNE";
		columnNames[2] = "Nom";
		columnNames[3] = "Prenom";

		// an array of element coefficients
		List<Integer> coeffs = new ArrayList<>();

		// ajouter les noms des elements du module
		int j = 4;

		for (Element element : elements) {
			columnNames[j] = element.getNom() + " Normale";
			coeffs.add((int) element.getCurrentCoefficient());
			j++;
		}

		for (Element element : elements) {
			columnNames[j] = element.getNom() + " Ratt";
			j++;
		}
		// puis ajouter les columns Note Module , Validation
		columnNames[j++] = "Moyenne";
		columnNames[j++] = "Validation";
		int numberOfColumns = 4 + 2*elements.size() + 2 ;
		Object[][] data = new Object[studentsNotes.getStudents().size()][numberOfColumns];

		int i = 0;
		for (Etudiant e : studentsNotes.getStudents()) {
			data[i][0] = e.getIdUtilisateur();
			data[i][1] = e.getCne();
			data[i][2] = e.getNom();
			data[i][3] = e.getPrenom();
			
			// si il ya pas d notes pour ces etudiants , il arreter la boucle 
			// donc il ya un probleme , ie que n'a pas encore passed la session normale , mais il considiré 
			// comme avoir un rattrapage 
			if(studentsNotes.getElementsNotes()[0].length == 0) {
				break ;
			}

			// les notes de la session normale
			int k = 4;
			int kk = 0;
			for (Element elm : elements) {
				data[i][k] = studentsNotes.getElementsNotes()[i][kk];
				k++;
				kk++;
			}
			// inserstion des notes du rattrapage , si l'etudiant a validé un element mais
			// pas les autres
			kk = 0;
			for (Element elm : elements) {
				if(studentsNotes.getElementsNotes()[i][kk] > noteValidation ) {
					data[i][k] = studentsNotes.getElementsNotes()[i][kk];
				}
				
				k++;
				kk++;
			}

			i++;
		}
		System.out.println("printing the data");
		System.out.println(data.length);
		System.out.println(data[0].length);
		for (Object[] obj : data) {
			for (Object ele : obj) {
				System.out.println(ele);
			}
			System.out.println("####################");
		}
		System.out.println("printing the data ");
		ExcelExporter excel = new ExcelExporter(columnNames, data, "etudiants");

		excel.fillSheet();
		excel.SetFormulas(coeffs,noteValidation, false);

		excel.addDataTosheet(0, moduleInfo);

		return excel;
	}

}
