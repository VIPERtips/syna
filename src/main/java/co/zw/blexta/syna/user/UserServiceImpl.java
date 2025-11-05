package co.zw.blexta.syna.user;

import co.zw.blexta.syna.common.exception.BadRequestException;
import co.zw.blexta.syna.common.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        Optional<User> existing = userRepository.findByClerkUserId(userDto.getClerkUserId());
        if (existing.isPresent()) throw new ConflictException("User already exists");

        User user = UserMapper.toEntity(userDto);
        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    @Override
    public Optional<UserDto> getUserByClerkId(String clerkUserId) {
        return userRepository.findByClerkUserId(clerkUserId)
                .map(UserMapper::toDto);
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto);
    }

    @Override
    public UserDto updateUser(String clerkUserId, UserDto userDto) {
        User user = userRepository.findByClerkUserId(clerkUserId)
                .orElseThrow(() -> new ConflictException("User not found for update"));

        // Only update mutable fields
        if (userDto.getFirstName() != null) user.setFirstName(userDto.getFirstName());
        if (userDto.getLastName() != null) user.setLastName(userDto.getLastName());
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
        if (userDto.getLocation() != null) user.setLocation(userDto.getLocation());
        if (userDto.getPhoneNumber() != null) user.setPhoneNumber(userDto.getPhoneNumber());
        if (userDto.getRole() != null) user.setRole(userDto.getRole());

        User updated = userRepository.save(user);
        return UserMapper.toDto(updated);
    }

    @Override
    public void deleteUser(String clerkUserId) {
        User user = userRepository.findByClerkUserId(clerkUserId)
                .orElseThrow(() -> new ConflictException("User not found for deletion"));
        userRepository.delete(user);
    }

}
