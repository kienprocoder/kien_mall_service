package vn.restapi.kienmall.payload.request;

import lombok.Data;
import vn.restapi.kienmall.common.PageRequest;
@Data
public class MemberRequest {
    private String firstName;
    private String lastName;
    private String fullName;
    private String telePhone;
    private String address;
    private String mail;
    private String gender;
    private String country;
    private String birthDay;
    private PageRequest pageRequest;
}
