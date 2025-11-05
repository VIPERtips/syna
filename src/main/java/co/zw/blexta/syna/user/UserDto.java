package co.zw.blexta.syna.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String clerkUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String location;
    private Long phoneNumber;
    private User.Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
