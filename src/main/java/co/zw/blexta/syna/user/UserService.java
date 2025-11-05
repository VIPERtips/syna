package co.zw.blexta.syna.user;

import java.util.Optional;

public interface UserService {

    UserDto createUser(UserDto userDto);

    Optional<UserDto> getUserByClerkId(Long clerkUserId);

    Optional<UserDto> getUserById(Long id);

    UserDto updateUser(Long clerkUserId, UserDto userDto);
    void deleteUser(Long clerkUserId);

}
