package com.gsnotes.services;

import java.util.List;

import com.gsnotes.bo.Element;
import com.gsnotes.bo.Etudiant;
import com.gsnotes.services.impl.StudentsNotes;
import com.gsnotes.utils.export.ExcelExporter;
import com.gsnotes.web.models.ModuleInfo;

public interface IEtudiantService {
	
	public ExcelExporter prepareStundentsExport(List<Etudiant> persons, List<Element> elements, ModuleInfo moduleInfo,  double noteValidation);
	
	public List<Etudiant> getRattrapedStudents(int idModule, int year) ;
	
	public  StudentsNotes getRattrapedStudentsV2(int idModule, int year) ;
	
	
	public ExcelExporter prepareRattrapedStundents(StudentsNotes studentsNotes, List<Element> elements,
			ModuleInfo moduleInfo , double noteValidation) ;

}
