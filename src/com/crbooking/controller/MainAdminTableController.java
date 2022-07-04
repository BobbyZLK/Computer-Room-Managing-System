package com.crbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainAdminTableController {
   @RequestMapping("/admin/tomaintable")
   public String toMainAdminTable() {
	   return "SystemAdministor/AdminTable";
   }
}
