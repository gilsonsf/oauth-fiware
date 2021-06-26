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

import static java.util.Objects.isNull;

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
    public String execution(Model model, String userName, String asId, int flowId) {

        AttackTypes flowType = Utilities.getAttackTypesById(flowId);

        UserTemplate userSelected = UserTemplateMemoryRepository.findByNameAndAS(userName, asId);

        if (isNull(userSelected)) {
            return "error";
        }

        managerService.createTaskSync(userSelected, flowType);

        try {
            //espera 20 segundo para o gravar o arquivo
            Thread.sleep(20000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CaptureTemplate captureTemplate = captureService.execute(userSelected, "sync");
        captureTemplate.setFlowType(flowType.name());

        CaptureTemplateMemoryRepository.addCaptureTemplate(captureTemplate);

        model.addAttribute("captured", captureTemplate.getCaptureList());
        model.addAttribute("user", captureTemplate.getUser());

        return "view";
    }

    @GetMapping(value = "/executionAsync")
    public String executionAsync(String minutes) {

        if(managerService.hasExecution()) {
            return "anymessage";
        } else {
            managerService.startProcess(Integer.parseInt(minutes));
            return "analysis";
        }

    }

}
