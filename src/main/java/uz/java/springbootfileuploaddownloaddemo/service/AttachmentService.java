package uz.java.springbootfileuploaddownloaddemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.java.springbootfileuploaddownloaddemo.entity.Attachment;
import uz.java.springbootfileuploaddownloaddemo.entity.AttachmentContent;
import uz.java.springbootfileuploaddownloaddemo.payload.ApiResponse;
import uz.java.springbootfileuploaddownloaddemo.repository.AttachmentContentRepository;
import uz.java.springbootfileuploaddownloaddemo.repository.AttachmentRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {
    final
    AttachmentRepository attachmentRepository;

    final
    AttachmentContentRepository attachmentContentRepository;

    public AttachmentService(AttachmentRepository attachmentRepository, AttachmentContentRepository attachmentContentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
    }


    public ApiResponse dbSave(MultipartHttpServletRequest request) {
        try {
            Iterator<String> fileNames = request.getFileNames();
            List<UUID> savedAttachmentIds=new ArrayList<>();
            while (fileNames.hasNext()) {
                MultipartFile file = request.getFile(fileNames.next());
                assert file != null;
                Attachment attachment = new Attachment(
                        file.getOriginalFilename(),
                        file.getContentType() != null ? file.getContentType() : "unknown",
                        (int) file.getSize()
                );
                Attachment savedAttachment = attachmentRepository.save(attachment);

                AttachmentContent attachmentContent = new AttachmentContent(
                        file.getBytes(),
                        savedAttachment
                );
                attachmentContentRepository.save(attachmentContent);
                savedAttachmentIds.add(savedAttachment.getId());
            }
            return new ApiResponse("Saved", true,savedAttachmentIds);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }

    public ApiResponse serverSave(MultipartHttpServletRequest request) {
        try {
            Iterator<String> fileNames = request.getFileNames();
            List<UUID> savedAttachmentIds=new ArrayList<>();
            while (fileNames.hasNext()) {
                MultipartFile file = request.getFile(fileNames.next());
                assert file != null;


                FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/static/appFiles/"+file.getOriginalFilename());
                byte[] bytes=file.getBytes();
                fileOutputStream.write(bytes);
                fileOutputStream.close();

                Attachment attachment = new Attachment(
                        file.getOriginalFilename(),
                        file.getContentType() != null ? file.getContentType() : "unknown",
                        (int) file.getSize()
                );
                Attachment savedAttachment = attachmentRepository.save(attachment);

                AttachmentContent attachmentContent = new AttachmentContent(
                        file.getBytes(),
                        savedAttachment
                );
                attachmentContentRepository.save(attachmentContent);
                savedAttachmentIds.add(savedAttachment.getId());
            }
            return new ApiResponse("Saved", true,savedAttachmentIds);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }

    public ResponseEntity getFromDB(UUID id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("getAttachment"));
        AttachmentContent attachmentContent = attachmentContentRepository.findByAttachmentId(id);
        return ResponseEntity.ok().contentType(MediaType.valueOf(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; fileName=\"" + attachment.getName() + "\"")
                .body(attachmentContent.getBytes());
    }

    public ResponseEntity getFromServer(UUID id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("getAttachment"));
        try {
            File file = ResourceUtils.getFile("classpath:static/appFiles/"+attachment.getName());
            InputStream in = new FileInputStream(file);
            byte[] bdata = FileCopyUtils.copyToByteArray(in);
            return ResponseEntity.ok().contentType(MediaType.valueOf(attachment.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; fileName=\"" + attachment.getName() + "\"")
                    .body(bdata);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
