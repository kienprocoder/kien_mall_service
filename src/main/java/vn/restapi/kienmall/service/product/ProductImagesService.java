package vn.restapi.kienmall.service.product;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductImagesService {
    String uploadFileToFileDirectory(MultipartFile file) throws IOException;

    byte[] downloadFileFromFileDirectory(String fileName) throws IOException;
}
