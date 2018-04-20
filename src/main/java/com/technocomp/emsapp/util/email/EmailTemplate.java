/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technocomp.emsapp.util.email;

import com.technocomp.emsapp.util.AppUtil;
import com.technocomp.emsapp.util.Constants;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;


/**
 *
 * @author Ravi Varma Yarakaraj
 */
public class EmailTemplate {

	private String templateId;

	private String template;
        
        private ResourceLoader resourceLoader;

	private Map<String, String> replacementParams;

	public EmailTemplate(String templateId) {
		this.templateId = templateId;
		try {
                    System.out.println("nside template try ");
			this.template = loadTemplate(templateId);
		} catch (Exception e) {
                     e.printStackTrace();
                    System.out.println("nside template catch " + e.toString());
			this.template = Constants.BLANK;
		}
	}

	private String loadTemplate(String templateId) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
                Resource resource = resourceLoader.getResource("classpath:application.properties");
            
                 System.out.println("class loader value is  are :"+ resource.getFilename() );
		File file = new File(resource.getFilename());
                System.out.println("Files are :"+file.getName() );
		String content = Constants.BLANK;
		try {
			content = new String(Files.readAllBytes(file.toPath()));
                        System.out.println("Inside try" );
		} catch (IOException e) {
                    System.out.println("Inside catchCould not read template with ID = " + templateId);
			throw new Exception("Could not read template with ID = " + templateId);
                        
		}
		return content;
	}

	public String getTemplate(Map<String, String> replacements) {
		String cTemplate = this.getTemplate();

		if (!AppUtil.isObjectEmpty(cTemplate)) {
			for (Map.Entry<String, String> entry : replacements.entrySet()) {
				cTemplate = cTemplate.replace("{{" + entry.getKey() + "}}", entry.getValue());
			}
		}
		
		return cTemplate;
	}

	/**
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId
	 *            the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * @return the replacementParams
	 */
	public Map<String, String> getReplacementParams() {
		return replacementParams;
	}

	/**
	 * @param replacementParams
	 *            the replacementParams to set
	 */
	public void setReplacementParams(Map<String, String> replacementParams) {
		this.replacementParams = replacementParams;
	}

}
