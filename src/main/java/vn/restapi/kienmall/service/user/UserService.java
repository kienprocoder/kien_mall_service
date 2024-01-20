package vn.restapi.kienmall.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.restapi.kienmall.constant.message.Message;
import vn.restapi.kienmall.model.ERole;
import vn.restapi.kienmall.model.Role;
import vn.restapi.kienmall.model.User;
import vn.restapi.kienmall.payload.request.UserRequest;
import vn.restapi.kienmall.repository.RoleRepository;
import vn.restapi.kienmall.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    public User saveUser(UserRequest userRequest) {
        try {
            User user = new User();
            user.setUsername(userRequest.getUserName());
            user.setEmail(userRequest.getEmail());
            user.setPassword(encoder.encode(user.getPassword()));
            Set<String> strRoles = userRequest.getRole();
            Set<Role> roles = new HashSet<>();
            if (strRoles == null) {
                Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException(Message.ROLE_ADMIN_NOT_FOUND));
                roles.add(userRole);
            }
            user.setRoles(roles);
            return userRepository.save(user);
        }catch (Exception e) {
            throw new RuntimeException("Add user admin error!" + e.getMessage());
        }
    }

    /**
     * getUsernameById
     * @return
     */
    public boolean existByUserName(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * getPassword by username
     *
     * @param username
     * @return
     */
    public String getPasswordByUsername(String username) {
        return userRepository.paswordEncoderByUsername(username);
    }

    /**
     * getUser pagination
     * @param username
     * @param page
     * @param size
     * @return
     */
    public Map<String, Object> userPagination(String username, int page, int size) {
        List<User> lstUser = new ArrayList<User>();
        Pageable paging = PageRequest.of(page, size);

        Page<User> pageTuts;
        if (username == null)
            pageTuts = userRepository.findAll(paging);
        else
            pageTuts = userRepository.findByUsernameContaining(username, paging);

        lstUser = pageTuts.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("data", lstUser);
        response.put("currentPage", pageTuts.getNumber());
        response.put("totalItems", pageTuts.getTotalElements());
        response.put("totalPages", pageTuts.getTotalPages());

        return response;
    }
}
