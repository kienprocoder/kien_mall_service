package vn.restapi.kienmall.controller.department;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.restapi.kienmall.constant.ResponseMessage;
import vn.restapi.kienmall.constant.ResponseStatus;
import vn.restapi.kienmall.model.department.DepartmentModel;
import vn.restapi.kienmall.payload.request.department.DepartmentRequest;
import vn.restapi.kienmall.service.department.DepartmentService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/department")
public class DepartmentController {
    private final DepartmentService departmentService;
    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * API add record Database
     *
     * @param departmentRequest
     * @return
     */
    @PostMapping("/addRecordDepartment")
    public ResponseEntity<ResponseMessage> saveRecordDatabase(@Validated @RequestBody DepartmentRequest departmentRequest) {
        try {
            departmentService.saveDepartment(departmentRequest);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(ResponseStatus.SUCCESS, "Insert Department is exist"));
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * API GET Data Department
     *
     * @param departmentRequest
     * @return
     */
    public ResponseEntity<List<DepartmentModel>> getDataDepartment(@RequestBody DepartmentRequest departmentRequest) {
        try {
            List<DepartmentModel> lstDepartment = departmentService.getData(departmentRequest.getDepartmentName().toString());
            return ResponseEntity.status(HttpStatus.OK).body(lstDepartment);
        }catch (Exception e) {
            throw new RuntimeException("Data Department Error");
        }
    }
}
