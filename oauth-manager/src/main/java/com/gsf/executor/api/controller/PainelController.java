package com.gsf.executor.api.controller;

import com.gsf.executor.api.enums.AttackTypes;
import com.gsf.executor.api.entity.CaptureTemplate;
import com.gsf.executor.api.entity.UserTemplate;
import com.gsf.executor.api.repository.CaptureTemplateMemoryRepository;
import com.gsf.executor.api.repository.UserTemplateMemoryRepository;
import com.gsf.executor.api.service.CaptureService;
import com.gsf.executor.api.service.ManagerService;
import com.gsf.executor.api.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PainelController {

    @Autowired
    private  CaptureService captureService;

    @Autowired
    private ManagerService managerService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/analysis")
    public String analysis() {
        return "analysis";
    }

    @GetMapping("/view")
    public String view(Model model, String templateId) {

        CaptureTemplate captureTemplate = CaptureTemplateMemoryRepository.getCaptureTemplate(Integer.parseInt(templateId));

        model.addAttribute("captured", captureTemplate.getCaptureList());
        model.addAttribute("user", captureTemplate.getUser());

        return "view";
    }

    @GetMapping("/all")
    public String all(Model model) {

        List<CaptureTemplate> capturedList = CaptureTemplateMemoryRepository.getAllCaptureTemplate();

        model.addAttribute("capturedList", capturedList);

        return "all";
    }

    @GetMapping("/delete")
    public String delete(Model model) {

        List<CaptureTemplate> capturedList = CaptureTemplateMemoryRepository.getCleanList();

        model.addAttribute("capturedList", capturedList);

        return "all";
    }

    @GetMapping("/execution")
    public String execution(Model model, String userName, String asId, int flowId, String minutes) {

        AttackTypes flowType = Utilities.getAttackTypesById(flowId);

        UserTemplate userSelected = UserTemplateMemoryRepository.findByNameAndAS(userName, asId);

        //captureService.prepareFile();


        managerService.createTaskSync(userSelected, flowType);

        try {
            //espera 15 segundo para o gravar o arquivo
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CaptureTemplate captureTemplate = captureService.execute(userSelected, flowType.name());

        CaptureTemplateMemoryRepository.addCaptureTemplate(captureTemplate);

        model.addAttribute("captured", captureTemplate.getCaptureList());
        model.addAttribute("user", captureTemplate.getUser());

        return "view";
    }

}
