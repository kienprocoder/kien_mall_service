package vn.restapi.kienmall.service.product.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.restapi.kienmall.model.product.ProductImages;
import vn.restapi.kienmall.repository.UploadImagesRepository;
import vn.restapi.kienmall.service.product.ProductImagesService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class ProductImagesServiceImpl implements ProductImagesService {

    @Autowired
    private UploadImagesRepository uploadImagesRepository;

    private final String FILE_PATH = "g_image.redirect";

    /**
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public String uploadFileToFileDirectory(MultipartFile file) throws IOException {
        String filePath = FILE_PATH+file.getOriginalFilename();//absolute path
        // TODO Auto-generated method stub
        ProductImages fileData = uploadImagesRepository.save(ProductImages.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(filePath).build());

        //copy your file into that particular path
        file.transferTo(new java.io.File(filePath));

        if(fileData!= null) {
            return file.getOriginalFilename();
        }
        return null;
    }

    /**
     * @param fileName
     * @return
     * @throws IOException
     */
    @Override
    public byte[] downloadFileFromFileDirectory(String fileName) throws IOException {
        Optional<ProductImages> fileDataObj = uploadImagesRepository.findByName(fileName);

        //first need to get the file path
        String filePath = fileDataObj.get().getFilePath();

        //got the file, now decompress it.
        byte[] imageFile = Files.readAllBytes(new java.io.File(filePath).toPath());

        return imageFile;
    }
}
