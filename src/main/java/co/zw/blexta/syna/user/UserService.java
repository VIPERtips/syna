package co.zw.blexta.syna.user;

import java.util.Optional;

public interface UserService {

    UserDto createUser(UserDto userDto);

    Optional<UserDto> getUserByClerkId(String clerkUserId);

    Optional<UserDto> getUserById(Long id);

    UserDto updateUser(String clerkUserId, UserDto userDto);
    void deleteUser(String clerkUserId);

}
