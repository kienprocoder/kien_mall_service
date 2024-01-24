package vn.restapi.kienmall.service.department;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.restapi.kienmall.model.department.DepartmentModel;
import vn.restapi.kienmall.payload.request.department.DepartmentRequest;
import vn.restapi.kienmall.repository.department.DepartmentRepository;

import java.util.List;

/**
 * Department Service
 */
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    /**
     * Insert department
     *
     * @param departmentRequest
     * @return
     */
    public DepartmentModel saveDepartment(DepartmentRequest departmentRequest) {
        try {
            if (departmentRequest.getDepartmentName().isEmpty()) {
                throw new RuntimeException("Department Name is not exist");
            }
            DepartmentModel departmentModel = new DepartmentModel();
            departmentModel.setDepartmentName(departmentRequest.getDepartmentName());
            return departmentRepository.save(departmentModel);
        }catch (Exception e) {
            throw new RuntimeException("Insert Department is not exist" + e.getMessage());
        }
    }

    /**
     * Get data value
     *
     * @param departmentName
     * @return
     */
    public List<DepartmentModel> getData(String departmentName) {
        if (departmentName.isEmpty()) {
            return departmentRepository.orderById();
        }
        return departmentRepository.getDepartmentModelByName(departmentName);
    }
}
