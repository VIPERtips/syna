package co.zw.blexta.syna.availability;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String recurring;
}
