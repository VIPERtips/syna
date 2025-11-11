package co.zw.blexta.syna.availability;

import co.zw.blexta.syna.common.exception.BadRequestException;
import co.zw.blexta.syna.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    @Operation(summary = "Create doctor availability", description = "Adds a new availability slot for a doctor.")
    public ResponseEntity<ApiResponse<AvailabilityDTO>> createAvailability(@RequestBody AvailabilityDTO dto) {
        if (dto.getDoctorId() == null || dto.getStartTime() == null || dto.getEndTime() == null) {
            throw new BadRequestException("Missing required fields: doctorId, startTime, endTime");
        }

        AvailabilityDTO created = availabilityService.createAvailability(dto);
        return ResponseEntity.ok(new ApiResponse<>("Availability created successfully", true, created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update availability", description = "Updates an existing availability slot.")
    public ResponseEntity<ApiResponse<AvailabilityDTO>> updateAvailability(
            @PathVariable Long id,
            @RequestBody AvailabilityDTO dto
    ) {
        AvailabilityDTO updated = availabilityService.updateAvailability(id, dto);
        return ResponseEntity.ok(new ApiResponse<>("Availability updated successfully", true, updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete availability", description = "Deletes a specific availability slot.")
    public ResponseEntity<ApiResponse<String>> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.ok(new ApiResponse<>("Availability deleted successfully", true, null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get availability by ID", description = "Fetches a single availability slot by its ID.")
    public ResponseEntity<ApiResponse<AvailabilityDTO>> getAvailabilityById(@PathVariable Long id) {
        AvailabilityDTO dto = availabilityService.getAvailabilityById(id);
        return ResponseEntity.ok(new ApiResponse<>("Availability found", true, dto));
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get all doctor availabilities", description = "Fetches all availability slots for a given doctor.")
    public ResponseEntity<ApiResponse<List<AvailabilityDTO>>> getDoctorAvailability(@PathVariable Long doctorId) {
        List<AvailabilityDTO> list = availabilityService.getDoctorAvailability(doctorId);
        return ResponseEntity.ok(new ApiResponse<>("Doctor availabilities fetched successfully", true, list));
    }
}
