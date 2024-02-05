package io.github.gms.common.service.impl;

import io.github.gms.common.exception.GmsException;
import io.github.gms.common.dto.ResetPasswordRequestDto;
import io.github.gms.common.service.ResetPasswordService;
import io.github.gms.secure.dto.MessageDto;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.MessageService;
import org.springframework.stereotype.Service;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private static final String MESSAGE_TEMPLATE = "Password reset requested by user '%s'";

    private final MessageService messageService;
    private final UserRepository userRepository;

    public ResetPasswordServiceImpl(MessageService messageService, UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    @Override
    public void resetPassword(ResetPasswordRequestDto dto) {
        UserEntity user = userRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new GmsException("User not found!"));

        userRepository.getAllAdmins().parallelStream().forEach(adminUser -> messageService.save(MessageDto.builder()
                .message(MESSAGE_TEMPLATE.formatted(dto.getUsername()))
                .userId(adminUser.getId())
                .actionPath("/user/" + user.getId())
                .build()));
    }
}
