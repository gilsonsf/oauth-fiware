package com.gsf.executor.api.websocket;

import com.gsf.executor.api.entity.CaptureTemplate;
import com.gsf.executor.api.repository.CaptureTemplateMemoryRepository;

public class OutputMessageCaptured {

    private  CaptureTemplate captureTemplate;

    public OutputMessageCaptured() {
    }

    public CaptureTemplate getCaptureTemplate() {

        if (CaptureTemplateMemoryRepository.getAllCaptureTemplate().size() > 0 ) {
            this.captureTemplate = CaptureTemplateMemoryRepository.getCaptureTemplate(1);
        } else {
            this.captureTemplate = new CaptureTemplate();
        }

        return captureTemplate;
    }
}
