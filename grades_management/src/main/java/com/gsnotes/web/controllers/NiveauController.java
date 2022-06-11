package com.gsnotes.web.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gsnotes.bo.Element;
import com.gsnotes.bo.Etudiant;
import com.gsnotes.bo.MatiereEnseignement;
import com.gsnotes.bo.Module;
import com.gsnotes.bo.Niveau;
import com.gsnotes.dao.MatiereEnseignementDao;
import com.gsnotes.services.IEtudiantService;
import com.gsnotes.services.IModuleService;
import com.gsnotes.services.INiveauService;
import com.gsnotes.services.IinscriptionModuleService;
import com.gsnotes.services.MatiereEnseignementService;
import com.gsnotes.services.impl.StudentsNotes;
import com.gsnotes.utils.export.ExcelExporter;
import com.gsnotes.web.models.ModuleInfo;

@Controller
@RequestMapping("/niveaux")
public class NiveauController {

	///
	@Autowired
	MatiereEnseignementService matiereService;
	////

	@Autowired
	private INiveauService niveauService;

	@Autowired
	private IModuleService moduleService;

	@Autowired
	private IEtudiantService studentService;

	@Autowired
	private IinscriptionModuleService insModuleService;

	@GetMapping("/")
	public String getAllNiveaux(Model model) {

		List<Niveau> niveaux = niveauService.getAllNiveaux();

		model.addAttribute("niveaux", niveaux);

		return "listOfNiveaux";
	}

	@RequestMapping(value = "/modules/{idNiveau}", method = RequestMethod.GET)
	public String getModules(@PathVariable int idNiveau, Model model, HttpServletRequest request) {

		List<Module> modules = niveauService.getNiveauById(Long.valueOf(idNiveau)).getModules();

		model.addAttribute("modules", modules);
		model.addAttribute("idNiveau", idNiveau);

		return "modulesPerNiveau";
	}

	/**
	 * la methode qui permet de générer le fichier excel de collecte de notes pour
	 * la session normale
	 * 
	 * @param idNiveau
	 * @param idModule
	 * @param response
	 * @throws IOException
	 */

	@RequestMapping(value = "/modules/normal/{idNiveau}/{idModule}", method = RequestMethod.GET)
	public void generateFilePerModuleNormale(@PathVariable int idNiveau, @PathVariable int idModule,
			HttpServletResponse response) throws IOException {

		// trover tous les etudiants qui sont inscrits dans le module
		// les etudiants du niveau N, mais aussi des etudiants du niveau n-1 (ajournées)
		// qui ont des inscriptions dans le module

		List<Etudiant> students = insModuleService.findSessionNormaleStudents(idModule, anneeUniv());

		// get the elements of the module
		List<Element> elementsOfModule = moduleService.getElementsOfModule(idModule);

		// get the infomations related to that module (teacher , module name ,)
		Module module = moduleService.getModuleById(idModule);
		Niveau niveau = niveauService.getNiveauById(Long.valueOf(idNiveau));

		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setNom(module.getTitre());
		moduleInfo.setAnnnee(anneeUniv());
		moduleInfo.setClasse(niveau.getAlias());
		moduleInfo.setSemestre(module.getSession());

		// trouver tous les profs qui enseignent ce module
		String profNames = getProfsForMatiere(anneeUniv(), module);
		moduleInfo.setProf(profNames);
		moduleInfo.setSession("Normale");
		// prepare the students to be exported
		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=students_" + currentDateTime + ".xlsx";
		response.setHeader(headerKey, headerValue);

		ExcelExporter excelExporter = studentService.prepareStundentsExport(students, elementsOfModule, moduleInfo, niveau.getNoteValidation());

		excelExporter.export(response);

	}

	// une methode qui genere un fichier de rattrapage , mais sans les notes de la
	// ssession normale
	// donc il ya pas un moyen pour calculer la moyenne pour les etudiants
	@RequestMapping(value = "/modules/rattv1/{idNiveau}/{idModule}", method = RequestMethod.GET)
	public void generateFilePerModuleRatt(@PathVariable int idNiveau, @PathVariable int idModule,
			HttpServletResponse response) throws IOException {

		// get all students who have rattrapage

		List<Etudiant> students = studentService.getRattrapedStudents(idModule, anneeUniv());

		// get the elements of the module and stocke them into a variable
		List<Element> elementsOfModule = moduleService.getElementsOfModule(idModule);

		// get the infomations related to that module (teacher , module name ,)
		Module module = moduleService.getModuleById(idModule);
		Niveau niveau = niveauService.getNiveauById(Long.valueOf(idNiveau));

		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setNom(module.getTitre());
		moduleInfo.setAnnnee(anneeUniv());
		moduleInfo.setClasse(niveau.getAlias());
		moduleInfo.setSemestre(module.getSession());
		moduleInfo.setProf(getProfsForMatiere(anneeUniv(), module));
		moduleInfo.setSession("Rattrapage");

		// prepare the students to be exported
		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=students_" + currentDateTime + ".xlsx";
		response.setHeader(headerKey, headerValue);

		ExcelExporter excelExporter = studentService.prepareStundentsExport(students, elementsOfModule, moduleInfo, niveau.getNoteValidation());

		excelExporter.export(response);
	}

	/**
	 * generer le fichier zip des fichiers de collecte des notes pour la session
	 * normale pour un niveau donné
	 * 
	 * @param response
	 * @param idNiveau
	 * @throws IOException
	 */

	@RequestMapping(value = "/zip/{idNiveau}", produces = "application/zip")
	public void zipFiles(HttpServletResponse response, @PathVariable int idNiveau) throws IOException {

		// setting headers
		response.setStatus(HttpServletResponse.SC_OK);
		response.addHeader("Content-Disposition", "attachment; filename=\"test.zip\"");

		ByteArrayOutputStream workbookByteArrayStream = null;
		ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
		ZipEntry zipEntry = null;
		List<XSSFWorkbook> workbooks = new ArrayList<>();

		// get all modules for that level
		List<Module> modules = niveauService.getModuleOfNiveau(idNiveau);
		Niveau niveau = niveauService.getNiveauById(Long.valueOf(idNiveau));
		
		for (Module module : modules) {
			List<Etudiant> students = insModuleService
					.findSessionNormaleStudents(Long.valueOf(module.getIdModule()).intValue(), anneeUniv());
			////

			// get the elements of the module
			List<Element> elementsOfModule = moduleService
					.getElementsOfModule(Long.valueOf(module.getIdModule()).intValue());

			// get the infomations related to that module (teacher , module name .....)

			

			ModuleInfo moduleInfo = new ModuleInfo();
			moduleInfo.setNom(module.getTitre());
			moduleInfo.setAnnnee(anneeUniv());
			moduleInfo.setClasse(niveau.getAlias());
			moduleInfo.setSemestre(module.getSession());
			moduleInfo.setProf(getProfsForMatiere(anneeUniv(), module));
			moduleInfo.setSession("Normale");

			ExcelExporter excelExporter = studentService.prepareStundentsExport(students, elementsOfModule, moduleInfo, niveau.getNoteValidation());
			workbooks.add(excelExporter.getWorkbook());
		}

		// the name of the file
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:ms");

		int i = 0;
		for (XSSFWorkbook current : workbooks) {
			String currentDateTime = dateFormatter.format(new Date());
			zipEntry = new ZipEntry("students_" + currentDateTime + i + ".xlsx");
			zipOutputStream.putNextEntry(zipEntry);
			workbookByteArrayStream = new ByteArrayOutputStream();
			current.write(workbookByteArrayStream);
			zipEntry.setSize(workbookByteArrayStream.size());
			zipOutputStream.write(workbookByteArrayStream.toByteArray());
			zipOutputStream.closeEntry();
			i++;
		}
		zipOutputStream.close();

	}

	/**
	 * la methode qui permet de générer le fichier excel de collecte des notes de
	 * ratrappage
	 * 
	 * dans ce fichier on trouve aussi les notes de la session normale , ce qui nous
	 * permet de calculer la note finale des etudiants rattrappées
	 * 
	 * @param idNiveau
	 * @param idModule
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/modules/ratt/{idNiveau}/{idModule}", method = RequestMethod.GET)
	public void generateFilePerModuleRattV2(@PathVariable int idNiveau, @PathVariable int idModule,
			HttpServletResponse response) throws IOException {

		// get all students who have rattrapage

		StudentsNotes studentsNotes = studentService.getRattrapedStudentsV2(idModule, anneeUniv());

		// get the elements of the module and stocke them into a variable
		List<Element> elementsOfModule = moduleService.getElementsOfModule(idModule);

		// get the infomations related to that module (teacher , module name ,)
		Module module = moduleService.getModuleById(idModule);
		Niveau niveau = niveauService.getNiveauById(Long.valueOf(idNiveau));

		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setNom(module.getTitre());
		moduleInfo.setAnnnee(anneeUniv());
		moduleInfo.setClasse(niveau.getAlias());
		moduleInfo.setSemestre(module.getSession());
		moduleInfo.setProf(getProfsForMatiere(anneeUniv(), module));
		moduleInfo.setSession("Rattrapage");

		// prepare the students to be exported
		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=students_" + currentDateTime + ".xlsx";
		response.setHeader(headerKey, headerValue);

		ExcelExporter excelExporter = studentService.prepareRattrapedStundents(studentsNotes, elementsOfModule,
				moduleInfo, niveau.getNoteValidation());

		excelExporter.export(response);

	}

	/**
	 * génération du fichier zip de fichiers de rattrapage
	 * 
	 * @param response
	 * @param idNiveau
	 * @throws IOException
	 */

	@RequestMapping(value = "/zip/ratt/{idNiveau}", produces = "application/zip")
	public void zipFilesRatt(HttpServletResponse response, @PathVariable int idNiveau) throws IOException {

		// setting headers
		response.setStatus(HttpServletResponse.SC_OK);
		response.addHeader("Content-Disposition", "attachment; filename=\"test.zip\"");
		ByteArrayOutputStream workbookByteArrayStream = null;
		ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
		ZipEntry zipEntry = null;
		List<XSSFWorkbook> workbooks = new ArrayList<>();

		// get all modules for that level
		List<Module> modules = niveauService.getModuleOfNiveau(idNiveau);
		double noteValidation = niveauService.getNiveauById(Long.valueOf(idNiveau)).getNoteValidation();

		for (Module module : modules) {
			StudentsNotes studentsNotes = studentService
					.getRattrapedStudentsV2(Long.valueOf(module.getIdModule()).intValue(), anneeUniv());

			////

			// get the elements of the module
			List<Element> elementsOfModule = moduleService
					.getElementsOfModule(Long.valueOf(module.getIdModule()).intValue());

			// get the infomations related to that module (teacher , module name .....)

			Niveau niveau = niveauService.getNiveauById(Long.valueOf(idNiveau));

			ModuleInfo moduleInfo = new ModuleInfo();
			moduleInfo.setNom(module.getTitre());
			moduleInfo.setAnnnee(anneeUniv());
			moduleInfo.setClasse(niveau.getAlias());
			moduleInfo.setSemestre(module.getSession());
			moduleInfo.setProf(getProfsForMatiere(anneeUniv(), module));
			moduleInfo.setSession("Rattrapage");

			ExcelExporter excelExporter = studentService.prepareRattrapedStundents(studentsNotes, elementsOfModule,
					moduleInfo, noteValidation);
			workbooks.add(excelExporter.getWorkbook());
		}

		// the name of the file
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:ms");

		int i = 0;
		for (XSSFWorkbook current : workbooks) {
			String currentDateTime = dateFormatter.format(new Date());
			zipEntry = new ZipEntry("students_" + currentDateTime + i + ".xlsx");
			zipOutputStream.putNextEntry(zipEntry);
			workbookByteArrayStream = new ByteArrayOutputStream();
			current.write(workbookByteArrayStream);
			zipEntry.setSize(workbookByteArrayStream.size());
			zipOutputStream.write(workbookByteArrayStream.toByteArray());
			zipOutputStream.closeEntry();
			i++;
		}
		zipOutputStream.close();

	}

	/**
	 * une methode qui permet de retourner tous les profs qui enseignent un module ,
	 * meme si il ya des elements de ce module enseigné par plusieurs profs
	 */

	public String getProfsForMatiere(int year, Module module) {

		String profNames = "";
		for (Element element : module.getElements()) {
			List<MatiereEnseignement> matieresEns = matiereService.findElementsProfs(year,
					Long.valueOf(element.getIdMatiere()));
			for (MatiereEnseignement ms : matieresEns) {
				profNames += ms.getProf().getNom() + "  ";
			}

		}

		return profNames;
	}

	/**
	 * une methhode qui permet de retourner l'anne universetiare noter que on stocke
	 * dans la base de données que la deuxieme partie de l'annee 2021/2022 ===> 2020
	 * 
	 * @return
	 */
	public static int anneeUniv() {

		Date date = new Date();
		int year = date.getYear() + 1900;

		int month = date.getMonth();

		if (month > 9)
			return year + 1;
		return year;
	}
}
