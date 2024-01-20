package vn.restapi.kienmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.restapi.kienmall.constant.ResponseMessage;
import vn.restapi.kienmall.constant.ResponseStatus;
import vn.restapi.kienmall.constant.message.Message;
import vn.restapi.kienmall.model.ERole;
import vn.restapi.kienmall.model.Role;
import vn.restapi.kienmall.model.User;
import vn.restapi.kienmall.payload.request.LoginRequest;
import vn.restapi.kienmall.payload.request.SignupRequest;
import vn.restapi.kienmall.payload.response.JwtResponse;
import vn.restapi.kienmall.payload.response.MessageResponse;
import vn.restapi.kienmall.repository.RoleRepository;
import vn.restapi.kienmall.repository.UserRepository;
import vn.restapi.kienmall.security.jwt.util.JwtUtils;
import vn.restapi.kienmall.service.impl.UserDetailsImpl;
import vn.restapi.kienmall.service.user.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest) {
        try {
            if ("".equals(loginRequest.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ResponseStatus.BAD_REQUEST, "Username is not null"));
            }
            boolean existByUserName = userService.existByUserName(loginRequest.getUsername());
            if (!existByUserName) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ResponseStatus.NOT_FOUND, "Username is exists"));
            }
            if ("".equals(loginRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ResponseStatus.BAD_REQUEST, "Password is not null"));
            }
            //validate password
            String rawPassword = userService.getPasswordByUsername(loginRequest.getUsername());
            if (!encoder.matches(loginRequest.getPassword(), rawPassword)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ResponseStatus.BAD_REQUEST, "Password is correct"));
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ResponseStatus.BAD_REQUEST, "Login Fail"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> registerUser(@Validated @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ResponseStatus.BAD_REQUEST, Message.SIGNUP_USERNAME_ERROR));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(ResponseStatus.BAD_REQUEST, Message.SIGNUP_EMAIL_ERROR));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException(Message.ROLE_USER_NOT_FOUND));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException(Message.ROLE_ADMIN_NOT_FOUND));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException(Message.ROLE_MODERATOR_NOT_FOUND));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException(Message.ROLE_USER_NOT_FOUND));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(ResponseStatus.SUCCESS, Message.SIGNUP_SUCCESS));

    }
}
