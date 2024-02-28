package io.github.gms.functions.resetpassword;

import io.github.gms.common.types.GmsException;
import io.github.gms.functions.message.MessageDto;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.functions.message.MessageService;
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
