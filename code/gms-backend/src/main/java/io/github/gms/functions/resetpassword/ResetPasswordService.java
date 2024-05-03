package io.github.gms.functions.resetpassword;

import io.github.gms.common.types.GmsException;
import io.github.gms.functions.message.MessageDto;
import io.github.gms.functions.message.MessageService;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.github.gms.common.types.ErrorCode.GMS_002;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private static final String MESSAGE_TEMPLATE = "Password reset requested by user '%s'";

    private final MessageService messageService;
    private final UserRepository userRepository;

    public void resetPassword(ResetPasswordRequestDto dto) {
        UserEntity user = userRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new GmsException("User not found!", GMS_002));

        userRepository.getAllAdmins().parallelStream().forEach(adminUser -> messageService.save(MessageDto.builder()
                .message(MESSAGE_TEMPLATE.formatted(dto.getUsername()))
                .userId(adminUser.getId())
                .actionPath("/user/" + user.getId())
                .build()));
    }
}
