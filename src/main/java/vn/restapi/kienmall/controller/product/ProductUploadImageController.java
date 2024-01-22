package vn.restapi.kienmall.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.restapi.kienmall.constant.ResponseMessage;
import vn.restapi.kienmall.constant.ResponseStatus;
import vn.restapi.kienmall.service.product.ProductImagesService;

import java.io.IOException;

@RestController
@RequestMapping("/api/product")
public class ProductUploadImageController {

    @Autowired
    private ProductImagesService fileDataService;

    @PostMapping("/file-upload-to-directory")
    public ResponseEntity<?> uploadImageToFileDirectory(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadFile = fileDataService.uploadFileToFileDirectory(file);
        try {
            if (uploadFile != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(ResponseStatus.SUCCESS, "Upload " + uploadFile + " success"));
            }
        }catch (Exception e) {
            throw new RuntimeException("Upload image is not success");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(ResponseStatus.INTERNAL_SERVER_ERROR, "Upload " + uploadFile + " error"));
    }

    @GetMapping("/file-download-from-directory/{fileName}")
    public ResponseEntity<?> downloadImageFromFileDirectory(@PathVariable String fileName) throws IOException{
        byte[] downloadFile = fileDataService.downloadFileFromFileDirectory(fileName);



        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(downloadFile);
    }
}
