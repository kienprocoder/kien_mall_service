package vn.restapi.kienmall.service.member;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.restapi.kienmall.model.member.MemberModel;
import vn.restapi.kienmall.payload.request.MemberRequest;
import vn.restapi.kienmall.repository.MembersRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class MemberService {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    @Autowired
    private static MembersRepository membersRepository;
    private static String firstName;
    private static String lastName;

    /**
     * get Data
     * @return
     */
    public List<MemberModel> getDataMember() {
        return membersRepository.orderByID();
    }

    /**
     * search member order like fullname
     *
     * @param memberRequest
     * @return
     */
    public List<MemberModel> findByFullNameLike(MemberRequest memberRequest) {
        return membersRepository.findByFullNameLike(memberRequest.getFullName());
    }

    /**
     *
     * @param memberRequest
     * @return
     */
    public MemberModel saveMemberToDatabase(MemberRequest memberRequest) {
        try {
            MemberModel memberModel = new MemberModel();
            if (memberRequest.getFirstName().isEmpty()) {
                throw new RuntimeException("First Name is not Empty");
            }
            if (memberRequest.getLastName().isEmpty()) {
                throw new RuntimeException("Last Name is not empty");
            }
            if (memberRequest.getMail().isEmpty()) {
                throw new RuntimeException("Mail is not empty");
            }
            memberModel.setFirstName(memberRequest.getFirstName());
            memberModel.setLastName(memberRequest.getLastName());
            memberModel.setFullName(memberRequest.getLastName() + " " + memberRequest.getFirstName());
            memberModel.setAddress(memberRequest.getAddress());
            memberModel.setCountry(memberModel.getCountry());
            memberModel.setGender(memberModel.getGender());
            memberModel.setBirthDay(memberRequest.getBirthDay());
            memberModel.setTelephone(memberRequest.getTelePhone());
            memberModel.setMail(memberRequest.getMail());
            return membersRepository.save(memberModel);
        }catch (Exception e) {
            throw new RuntimeException("Save Member To Database Fail" + e.getMessage());
        }
    }

    /**
     *
     * @param fullName
     * @param telephone
     * @param address
     * @param birthDay
     * @param page
     * @param size
     * @return
     */
    public Map<String, Object> memberModelListPagination(String fullName, String telephone, String address, String birthDay, int page, int size) {
        List<MemberModel> lstMember = new ArrayList<MemberModel>();
        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> response = new HashMap<>();
        Page<MemberModel> pageMember;
        if (fullName == null || telephone == null || address == null || birthDay == null) {
            pageMember = membersRepository.findAll(pageable);
            lstMember = pageMember.getContent();
            response.put("data", lstMember);
            response.put("currentPage", pageMember.getNumber());
            response.put("totalItems", pageMember.getTotalElements());
            response.put("totalPages", pageMember.getTotalPages());
        }else {
            lstMember = membersRepository.findByFullNameAndTelephoneOrAddressOrBirthDay(fullName, telephone, address, birthDay);
            response.put("data", lstMember);
        }
        return response;
    }

    /**
     *
     * @param page
     * @param size
     * @return
     */
    public Map<String, Object> paginationData(int page, int size) {
        List<MemberModel> lstMember = new ArrayList<MemberModel>();
        Pageable pageable = PageRequest.of(page, size);

        Page<MemberModel> pageMember = membersRepository.findAllWithPagination(pageable);
        lstMember = pageMember.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("data", lstMember);
        response.put("currentPage", pageMember.getNumber() + 1);
        response.put("totalItems", pageMember.getTotalElements());
        response.put("totalPages", pageMember.getTotalPages());

        return response;
    }

    /**
     * Check File
     *
     * @param file
     * @return
     */
    public static boolean hasExcelFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())){
            return false;
        }
        return true;
    }

    public static List<MemberModel> excelMember(MultipartFile inputStream) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream.getInputStream());

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<MemberModel> lstMember = new ArrayList<MemberModel>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                MemberModel memberModel = new MemberModel();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            memberModel.setFirstName(currentCell.getStringCellValue());
                            firstName = currentCell.getStringCellValue();
                            break;
                        case 1:
                            memberModel.setLastName(currentCell.getStringCellValue());
                            lastName = currentCell.getStringCellValue();
                            break;
                        case 2:
                            memberModel.setTelephone(currentCell.getStringCellValue());
                            break;
                        case 3:
                            memberModel.setGender(currentCell.getStringCellValue());
                            break;
                        case 4:
                            memberModel.setAddress(currentCell.getStringCellValue());
                            break;
                        case 5:
                            memberModel.setCountry(currentCell.getStringCellValue());
                            break;
                        case 6:
                            memberModel.setBirthDay(currentCell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                memberModel.setFullName(lastName + " " + firstName);
                memberModel.setCreatedDate(LocalDateTime.now());
                lstMember.add(memberModel);
            }

            workbook.close();
            membersRepository.saveAll(lstMember);
            return lstMember;
        }catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
