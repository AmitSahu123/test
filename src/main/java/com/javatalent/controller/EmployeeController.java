package com.javatalent.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.javatalent.model.Employee;
import com.javatalent.service.EmployeeService;

@Controller
public class EmployeeController {
	@Autowired
	private EmployeeService service;
	
	@GetMapping("/")
	public String home(Model model) {
		List<Employee> list = service.getAllEmployee();
		model.addAttribute("list", list);
		return "index";
	}
	
	@PostMapping("/upload")
	public String fileUpload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
		Employee employee = new Employee();
		String fileName = file.getOriginalFilename();
		employee.setProfilePicture(fileName);
		employee.setContent(file.getBytes());
		employee.setSize(file.getSize());
		service.createEmployee(employee);
		model.addAttribute("success", "File Uploaded Successfully!!!");
		return "index";
	}
	
	@GetMapping("/downloadfile")
	public void downloadFile(@Param("id") Long id, Model model, HttpServletResponse response) throws IOException {
		Optional<Employee> temp = service.findEmployeeById(id);
		if(temp!=null) {
			Employee employee = temp.get();
			response.setContentType("application/octet-stream");
			String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename = " +employee.getProfilePicture();
			response.setHeader(headerKey, headerValue);
			ServletOutputStream outputStream = response.getOutputStream();
			outputStream.write(employee.getContent());
			outputStream.close();
		}
	}
	
	@GetMapping("/image")
	public void showImage(@Param("id") Long id, HttpServletResponse response, Optional<Employee> employee) throws IOException {
		employee = service.findEmployeeById(id);
		response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf");
		response.getOutputStream().write(employee.get().getContent());
		response.getOutputStream().close();
	}
}
