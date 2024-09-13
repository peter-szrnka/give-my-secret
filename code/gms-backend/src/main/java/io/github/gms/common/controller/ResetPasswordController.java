package io.github.gms.common.controller;

import io.github.gms.common.abstraction.GmsController;
import io.github.gms.common.types.SkipSecurityTestCheck;
import io.github.gms.functions.resetpassword.ResetPasswordRequestDto;
import io.github.gms.functions.resetpassword.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@RestController
@SkipSecurityTestCheck
@RequiredArgsConstructor
public class ResetPasswordController implements GmsController {

    private final ResetPasswordService service;

    @PostMapping("/reset_password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequestDto dto) {
        service.resetPassword(dto);
        return ResponseEntity.status(200).build();
    }
}
