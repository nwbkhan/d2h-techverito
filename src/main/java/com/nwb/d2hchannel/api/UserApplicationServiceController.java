package com.nwb.d2hchannel.api;


import com.nwb.d2hchannel.ApiResponse;
import com.nwb.d2hchannel.PathConstants;
import com.nwb.d2hchannel.exception.D2Exception;
import com.nwb.d2hchannel.persistence.User;
import com.nwb.d2hchannel.repository.UserRepository;
import com.nwb.d2hchannel.request.UserDto;
import com.nwb.d2hchannel.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PathConstants.USER_PATH)
public class UserApplicationServiceController {

    // This should be done through service layer but for coding round i have done like this
    private final UserRepository userRepository;
    private final TokenService tokenService;


    public UserApplicationServiceController(UserRepository userRepository,
                                            TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> postUser(@RequestHeader("token") String token,
                                                @Validated @RequestBody UserDto user) {
        final User userByToken = tokenService.getUserByToken(token);
        userByToken.setEmail(user.getEmail());
        userByToken.setPhoneNo(user.getPhoneNo());
        final User savedUser = userRepository.saveAndFlush(userByToken);
        return ResponseEntity.ok(ApiResponse.of(savedUser));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getUser(@RequestHeader("token") String token) {
        final User userByToken = tokenService.getUserByToken(token);
        return ResponseEntity.ok(ApiResponse.of(userByToken));
    }


    @PostMapping("/recharge")
    public ResponseEntity<ApiResponse> recharge(@RequestHeader("token") String token,
                                                @RequestParam Long rechargeAmt) {
        final User userByToken = tokenService.getUserByToken(token);

        if (rechargeAmt <= 0L) {
            throw new D2Exception("recharge Amt should be greater than 0");
        }

        userByToken.setBalance(userByToken.getBalance() + rechargeAmt);
        final User savedUser = userRepository.saveAndFlush(userByToken);
        return ResponseEntity.ok(ApiResponse.of("Recharge completed successfully, Current balance " + savedUser.getBalance()));
    }


}
