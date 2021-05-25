package com.gsf.executor.api.repository;

import com.gsf.executor.api.entity.CaptureTemplate;

import java.util.ArrayList;
import java.util.List;

public class CaptureTemplateMemoryRepository {

    private static List<CaptureTemplate> captureTemplateList = new ArrayList<>();
    private static List<CaptureTemplate> captureTemplateListASYNC = new ArrayList<>();
    public static List<String> allPcapFiles = new ArrayList<>();


    public static void addCaptureTemplate(CaptureTemplate capture) {
        captureTemplateList.add(capture);
    }

    public static void addCaptureTemplateASYNC(CaptureTemplate capture) {
        captureTemplateListASYNC.add(capture);
    }

    public static List<CaptureTemplate> getAllCaptureTemplate() {
        return captureTemplateList;
    }

    public static List<CaptureTemplate> getAllCaptureTemplateASYNC() {
        return captureTemplateListASYNC;
    }



    public static CaptureTemplate getCaptureTemplate(Integer idCapture) {
        return captureTemplateList.stream()
                .filter(c -> c.getId() == idCapture)
                .findFirst().get();
    }

    public static CaptureTemplate getCaptureTemplateASYNC(Integer idCapture) {
        return captureTemplateListASYNC.stream()
                .filter(c -> c.getId() == idCapture)
                .findFirst().orElse(null);
    }

    public static List<CaptureTemplate> getCleanList() {
        captureTemplateList = new ArrayList<>();
        return captureTemplateList;
    }

}
