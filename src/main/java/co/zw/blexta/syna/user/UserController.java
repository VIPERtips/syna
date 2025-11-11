package co.zw.blexta.syna.user;

import co.zw.blexta.syna.common.exception.BadRequestException;
import co.zw.blexta.syna.common.response.ApiResponse;
import co.zw.blexta.syna.filter.ClerkService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ClerkService clerkService;

    // -------------------------------
    // Public Signup
    // -------------------------------
    @Operation(
            summary = "Signup a new user",
            description = "Creates a new user record in the database after Clerk verification. Public endpoint, no authentication required."
    )
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserDto>> signup(@RequestBody UserDto dto) {
        UserDto savedUser = userService.createUser(dto);
        return ResponseEntity.ok(
                ApiResponse.<UserDto>builder()
                        .success(true)
                        .message("User created successfully")
                        .data(savedUser)
                        .build()
        );
    }

    // -------------------------------
    // Public Login
    // -------------------------------
    @Operation(
            summary = "Login user",
            description = "Verifies Clerk token, returns the corresponding user record. Public endpoint, no authentication required."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@RequestHeader("Authorization") String token) throws Exception {

        String clerkUserId = clerkService.verifyTokenAndGetUserId(token);

        UserDto user = userService.getUserByClerkId(clerkUserId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return ResponseEntity.ok(
                ApiResponse.<UserDto>builder()
                        .success(true)
                        .message("Login successful")
                        .data(user)
                        .build()
        );
    }
    // -------------------------------
    // Authenticated endpoints
    // -------------------------------
    @Operation(
            summary = "Get current user",
            description = "Retrieves the currently authenticated user based on the Clerk token provided in Authorization header."
    )
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@RequestParam String clerkUserId) {
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
            description = "Updates fields of the currently authenticated user. Only provided fields will be updated."
    )
    @PatchMapping("/current")
    public ResponseEntity<ApiResponse<UserDto>> updateCurrentUser(
            @RequestHeader("Authorization") String token,
            @RequestBody UserDto dto) {
       // String clerkUserId = clerkService.verifyTokenAndGetUserId(token);
        UserDto updated = userService.updateUser(null, dto);
        return ResponseEntity.ok(
                ApiResponse.<UserDto>builder()
                        .success(true)
                        .message("User updated successfully")
                        .data(updated)
                        .build()
        );
    }

    @Operation(
            summary = "Delete current user",
            description = "Deletes the currently authenticated user account."
    )
    @DeleteMapping("/current")
    public ResponseEntity<ApiResponse<Void>> deleteCurrentUser(@RequestHeader("Authorization") String token) throws Exception {
        String clerkUserId = clerkService.verifyTokenAndGetUserId(token);
        userService.deleteUser(clerkUserId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("User deleted successfully")
                        .build()
        );
    }
}
