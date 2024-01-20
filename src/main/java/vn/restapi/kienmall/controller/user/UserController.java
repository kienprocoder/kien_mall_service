package vn.restapi.kienmall.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.restapi.kienmall.constant.ResponseMessage;
import vn.restapi.kienmall.constant.ResponseStatus;
import vn.restapi.kienmall.constant.message.Message;
import vn.restapi.kienmall.model.User;
import vn.restapi.kienmall.payload.request.UserRequest;
import vn.restapi.kienmall.service.user.UserService;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping("/saveUser")
    public ResponseEntity<ResponseMessage> saveUserToDatabase(@RequestBody UserRequest userRequest) {
        try {
            User user = userService.saveUser(userRequest);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(ResponseStatus.SUCCESS, Message.ADD_USER_SUCCESS));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(Message.ADD_USER_ERROR));
        }
    }

    @PostMapping("/userPagination")
    public ResponseEntity<Map<String, Object>> getUserPagination(@RequestBody UserRequest userRequest) {
        try {
            Map<String, Object> lstUserPagination = userService.userPagination(userRequest.getUserName(), userRequest.getPageRequest().getPage(), userRequest.getPageRequest().getSize());
            return ResponseEntity.status(HttpStatus.OK).body(lstUserPagination);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
