package vn.restapi.kienmall.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.restapi.kienmall.model.member.MemberModel;

import java.util.List;

public interface MembersRepository extends JpaRepository<MemberModel, Long> {
    @Query(value = "select * from members order by id asc", nativeQuery = true)
    List<MemberModel> orderByID();
    List<MemberModel> findByFullNameLike(String fullName);
    List<MemberModel> findByFullNameAndTelephoneOrAddressOrBirthDay(String fullName, String telephone, String address, String birthDay);
    @Query(value = "select * from members", nativeQuery = true)
    Page<MemberModel> findAllWithPagination(Pageable pageable);
}
