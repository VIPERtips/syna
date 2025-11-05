package co.zw.blexta.syna.user;

import co.zw.blexta.syna.common.response.ApiResponse;
import co.zw.blexta.syna.common.util.CurrentUserUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Create or fetch current user",
            description = "Creates a new user if it does not exist, otherwise returns the existing user"
    )
    @PostMapping("/current")
    public ResponseEntity<ApiResponse<UserDto>> createOrGetCurrentUser(@RequestBody UserDto dto) {
        Long clerkUserId = CurrentUserUtils.getCurrentClerkUserId();
        dto.setClerkUserId(clerkUserId);

        UserDto user = userService.createUser(dto);

        return ResponseEntity.ok(
                ApiResponse.<UserDto>builder()
                        .success(true)
                        .message("User created or retrieved successfully")
                        .data(user)
                        .build()
        );
    }

    @Operation(
            summary = "Get current user",
            description = "Fetches the currently authenticated Clerk user"
    )
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        Long clerkUserId = CurrentUserUtils.getCurrentClerkUserId();

        return userService.getUserByClerkId(clerkUserId)
                .map(user -> ResponseEntity.ok(
                        ApiResponse.<UserDto>builder()
                                .success(true)
                                .message("Current user retrieved successfully")
                                .data(user)
                                .build()
                ))
                .orElse(ResponseEntity.status(404).body(
                        ApiResponse.<UserDto>builder()
                                .success(false)
                                .message("User not found")
                                .build()
                ));
    }

    @Operation(
            summary = "Update current user",
            description = "Updates the currently authenticated user's profile. Only provided fields will be updated."
    )
    @PatchMapping("/current")
    public ResponseEntity<ApiResponse<UserDto>> updateCurrentUser(@RequestBody UserDto dto) {
        Long clerkUserId = CurrentUserUtils.getCurrentClerkUserId();
        UserDto updated = userService.updateUser(clerkUserId, dto);

        return ResponseEntity.ok(
                ApiResponse.<UserDto>builder()
                        .success(true)
                        .message("User updated successfully")
                        .data(updated)
                        .build()
        );
    }

    @Operation(
            summary = "Get user by ID",
            description = "Fetches a user by their database ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(
                        ApiResponse.<UserDto>builder()
                                .success(true)
                                .message("User retrieved successfully")
                                .data(user)
                                .build()
                ))
                .orElse(ResponseEntity.status(404).body(
                        ApiResponse.<UserDto>builder()
                                .success(false)
                                .message("User not found")
                                .build()
                ));
    }

    @Operation(
            summary = "Delete current user",
            description = "Deletes the currently authenticated user's account"
    )
    @DeleteMapping("/current")
    public ResponseEntity<ApiResponse<Void>> deleteCurrentUser() {
        Long clerkUserId = CurrentUserUtils.getCurrentClerkUserId();

        userService.getUserByClerkId(clerkUserId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        userService.deleteUser(clerkUserId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("User deleted successfully")
                        .build()
        );
    }
}
