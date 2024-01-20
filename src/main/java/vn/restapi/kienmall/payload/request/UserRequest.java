package vn.restapi.kienmall.payload.request;

import lombok.Data;
import vn.restapi.kienmall.common.PageRequest;

import java.util.Set;
@Data
public class UserRequest {
    private String userName;
    private String password;
    private String email;
    private Set<String> role;
    private PageRequest pageRequest;
}
