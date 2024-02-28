package io.github.gms.common.controller;

import io.github.gms.functions.resetpassword.ResetPasswordRequestDto;
import io.github.gms.functions.resetpassword.ResetPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/")
public class ResetPasswordController {

    private final ResetPasswordService service;

    public ResetPasswordController(ResetPasswordService service) {
        this.service = service;
    }

    @PostMapping("/reset_password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequestDto dto) {
        service.resetPassword(dto);
        return ResponseEntity.status(200).build();
    }
}
