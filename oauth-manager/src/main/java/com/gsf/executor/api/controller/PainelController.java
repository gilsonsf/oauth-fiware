package com.gsf.executor.api.controller;

import com.gsf.executor.api.service.CaptureService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class PainelController {

    @GetMapping("/")
    public String home(Model model) {

        CaptureService captureService = new CaptureService();
        Map captured = captureService.capture();

        model.addAttribute("captured", captured);

        return "view";
    }

}
