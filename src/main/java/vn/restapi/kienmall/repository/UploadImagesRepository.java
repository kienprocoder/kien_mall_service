package vn.restapi.kienmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.restapi.kienmall.model.product.ProductImages;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UploadImagesRepository extends JpaRepository<ProductImages, Long> {
    Optional<ProductImages> findByName(String fileName);
}
