package com.gsnotes.utils.export;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import java.util.List ;
public class ZipExporter {
	
	
	
	public static void exportToZip(HttpServletResponse response, List<ExcelExporter> excelFiles) throws IOException {
		
		response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=download.zip");
        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream()) ;
        try {
            for(ExcelExporter excelFile : excelFiles) {
            
                ZipEntry zipEntry = new ZipEntry("hello");
//                zipEntry.setSize(fileSystemResource.contentLength());
                zipEntry.setTime(System.currentTimeMillis());
                excelFile.getWorkbook().write(zipOutputStream) ;
                zipOutputStream.putNextEntry(zipEntry);

//                StreamUtils.copy(fileSystemResource.getInputStream(), zipOutputStream);
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
        } catch (IOException e) {
           e.getStackTrace() ;
        }


	}
	
	
	
	
	

}
