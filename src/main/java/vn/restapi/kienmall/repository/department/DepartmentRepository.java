package vn.restapi.kienmall.repository.department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.restapi.kienmall.model.department.DepartmentModel;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentModel, Long> {
    @Query(value = "select * from department order by id asc", nativeQuery = true)
    List<DepartmentModel> orderById();
    List<DepartmentModel> getDepartmentModelByName(String departmentName);
}
