package com.nwb.d2hchannel.api;

import com.nwb.d2hchannel.PathConstants;
import com.nwb.d2hchannel.request.LoginRequest;
import com.nwb.d2hchannel.request.SignupRequest;
import com.nwb.d2hchannel.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PathConstants.AUTH_PATH)
public class AuthApplicationServiceController {

    private final AuthService authService;

    public AuthApplicationServiceController(AuthService loginService) {
        this.authService = loginService;
    }

    @PostMapping(value = PathConstants.LOGIN_PATH)
    public ResponseEntity<String> login(@Validated @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.makeLogin(loginRequest));
    }


    @PostMapping(value = PathConstants.SIGNUP_PATH)
    public ResponseEntity<Object> signup(@Validated @RequestBody SignupRequest signupRequest) {
        authService.makeSignup(signupRequest);
        return ResponseEntity.ok("User has been created, please login now");
    }

    @PostMapping(value = PathConstants.LOGOUT_PATH)
    public ResponseEntity<Object> logout(@RequestHeader("token") String token) {
        authService.removeToken(token);
        return ResponseEntity.ok("logout - OK");
    }
}
