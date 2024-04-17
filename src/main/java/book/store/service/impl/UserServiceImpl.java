package book.store.service.impl;

import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserResponseDto;
import book.store.exception.RegistrationException;
import book.store.mapper.UserMapper;
import book.store.model.Role;
import book.store.model.User;
import book.store.repository.role.RoleRepository;
import book.store.repository.user.UserRepository;
import book.store.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can`t register User by email. This email is used");
        }
        User model = userMapper.toModel(requestDto);
        model.setPassword(passwordEncoder.encode(model.getPassword()));
        model.setRoles(Set.of(roleRepository.getByName(Role.RoleName.USER)));
        User savedUser = userRepository.save(model);
        return userMapper.toDto(savedUser);
    }
}
