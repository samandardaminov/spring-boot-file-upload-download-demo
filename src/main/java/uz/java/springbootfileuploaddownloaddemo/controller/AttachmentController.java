package uz.java.springbootfileuploaddownloaddemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.java.springbootfileuploaddownloaddemo.payload.ApiResponse;
import uz.java.springbootfileuploaddownloaddemo.service.AttachmentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/attachment")
public class AttachmentController {

    final
    AttachmentService attachmentService;

    @Autowired
    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/dbSave")
    public HttpEntity<?> dbSave(MultipartHttpServletRequest request){
        ApiResponse apiResponse=attachmentService.dbSave(request);
        return ResponseEntity.status(apiResponse.isSuccess()?201:409).body(apiResponse);
    }

    @PostMapping("/serverSave")
    public HttpEntity<?> serverSave(MultipartHttpServletRequest request){
        return null;
    }

    @GetMapping("/fromDB/{id}")
    public HttpEntity<?> fromDB(@PathVariable UUID id){
        return attachmentService.getFromDB(id);
    }

    @GetMapping("/fromServe/{id}")
    public HttpEntity<?> fromServer(@PathVariable UUID id){
        return null;
    }
}
