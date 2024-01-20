package vn.restapi.kienmall.controller.member;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.restapi.kienmall.constant.ResponseMessage;
import vn.restapi.kienmall.constant.ResponseStatus;
import vn.restapi.kienmall.constant.message.Message;
import vn.restapi.kienmall.model.member.MemberModel;
import vn.restapi.kienmall.payload.request.MemberRequest;
import vn.restapi.kienmall.repository.MembersRepository;
import vn.restapi.kienmall.service.member.MemberService;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/member")
public class MemberController {
    @Autowired
    MemberService memberService;
    private final MembersRepository membersRepository;

    private String lastName;
    private String firstName;
    private String telephone;
    private static final String REGEX_PHONE = "^\\+?[0-9]+(?:[\\s\\-\\(\\)]?[0-9]+)*$";

    @Autowired
    public MemberController(MembersRepository membersRepository) {
        this.membersRepository = membersRepository;
    }

    @GetMapping("/getAllDataMember")
    public ResponseEntity<List<MemberModel>> getDataMember() {
        try {
            List<MemberModel> getDataAllMember = memberService.getDataMember();
            //Get Data anchor isEmpty
            if (getDataAllMember.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(getDataAllMember, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * search Data Like Full Name
     *
     * @param memberRequest
     * @return
     */
    @GetMapping("/getMemberLikeName")
    public ResponseEntity<List<MemberModel>> getMemberLikeFullName(@RequestBody MemberRequest memberRequest) {
        try {
            List<MemberModel> getMemberLikeFullName = memberService.findByFullNameLike(memberRequest);

            if (getMemberLikeFullName.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(getMemberLikeFullName, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Add record member
     *
     * @param memberRequest
     * @return
     */
    @PostMapping("/addRecordMember")
    public ResponseEntity<ResponseMessage> saveMemberToDatabase(@RequestBody MemberRequest memberRequest) {
        try {
            memberService.saveMemberToDatabase(memberRequest);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(ResponseStatus.SUCCESS, Message.ADD_MEMBER_SUCCESS));
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     *
     * @param memberRequest
     * @return
     */
    @PostMapping("/memberPagination")
    public ResponseEntity<Map<String, Object>> getUserPagination(@RequestBody MemberRequest memberRequest) {
        try {
            Map<String, Object> lstUserPagination = memberService.memberModelListPagination(memberRequest.getFullName(),
                    memberRequest.getTelePhone(),
                    memberRequest.getTelePhone(),
                    memberRequest.getBirthDay(),
                    memberRequest.getPageRequest().getPage(),
                    memberRequest.getPageRequest().getSize());
            return ResponseEntity.status(HttpStatus.OK).body(lstUserPagination);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @param memberRequest
     * @return
     */
    @PostMapping("/searhData")
    public ResponseEntity<Map<String, Object>> searchData(@RequestBody MemberRequest memberRequest) {
        try {
            Map<String, Object> lstSearchData = memberService.paginationData(memberRequest.getPageRequest().getPage() - 1, memberRequest.getPageRequest().getSize());
            return ResponseEntity.status(HttpStatus.OK).body(lstSearchData);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Upload File Data
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        if (MemberService.hasExcelFormat(file)) {
            try {
                InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet datamember = workbook.getSheetAt(0);
                List<MemberModel> allMember = membersRepository.orderByID();
                for (int row = 0; row < datamember.getLastRowNum(); row++) {
                    for (int cell = 0; cell < datamember.getRow(row).getLastCellNum(); cell++) {
                        firstName = datamember.getRow(row + 1).getCell(0).getStringCellValue();
                        lastName = datamember.getRow(row + 1).getCell(1).getStringCellValue();
                        telephone = datamember.getRow(row + 1).getCell(2).getStringCellValue();
                        // validate firstname and lastname already exist
                        if (allMember.stream().anyMatch(member -> lastName.equals(member.getLastName()) && firstName.equals(member.getFirstName()))) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ResponseStatus.BAD_REQUEST, firstName + " " + lastName +" already exists"));
                        }
                        // validate phone
                        // +123456789
                        // 123-456-789
                        // (123) 456-789
                        // 123 456 789
                        Pattern pattern = Pattern.compile(REGEX_PHONE);
                        if (!pattern.matcher(telephone).matches()) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ResponseStatus.BAD_REQUEST, "Phone number is incorrect"));
                        }
                    }
                }
                memberService.excelMember(file);

                String message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            }catch (Exception e) {
                String message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }
        String message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }
}
