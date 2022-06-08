package com.stock.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ReportGenerate {


	public ResponseEntity<byte[]> generatereportPDF(Map<String, Object> parameters,
			JRBeanCollectionDataSource beanCollectionDataSource, String path ) {
		
		byte data[] = null;

		JasperReport compileReport;
		try {
			compileReport = JasperCompileManager
					.compileReport(new FileInputStream(path));
			JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, parameters, beanCollectionDataSource);
			 data = JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
	}
	
}
