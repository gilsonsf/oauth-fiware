package com.gsf.executor.api.websocket;

import com.gsf.executor.api.entity.CaptureTemplate;
import com.gsf.executor.api.repository.CaptureTemplateMemoryRepository;

import java.util.Objects;

public class OutputMessageCaptured {

    private  CaptureTemplate captureTemplate;

    private static Integer ID = 1;

    public OutputMessageCaptured() {
    }

    public CaptureTemplate getCaptureTemplate() {

        if (CaptureTemplateMemoryRepository.getAllCaptureTemplateASYNC().size() > 0 ) {
            this.captureTemplate = CaptureTemplateMemoryRepository.getCaptureTemplateASYNC(ID);

            if (Objects.nonNull(this.captureTemplate)) {
                ID = ID + 1;
            } else {
                this.captureTemplate = new CaptureTemplate();
            }

        } else {
            this.captureTemplate = new CaptureTemplate();
        }

        return this.captureTemplate;
    }


}
