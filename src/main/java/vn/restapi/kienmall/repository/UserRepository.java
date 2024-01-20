package vn.restapi.kienmall.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.restapi.kienmall.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);
    @Query(value = "select username from users order by id asc", nativeQuery = true)
    List<String> getUsernameById();
    @Query(value = "select password from users where username = ?1", nativeQuery = true)
    String paswordEncoderByUsername(String username);
    Boolean existsByUsername(String username);
    Page<User> findByUsernameContaining(String username, Pageable pageable);
}
