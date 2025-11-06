package co.zw.blexta.syna.doctor;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.zw.blexta.syna.common.exception.BadRequestException;
import co.zw.blexta.syna.common.response.ApiResponse;
import co.zw.blexta.syna.user.UserDto;
import co.zw.blexta.syna.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Register a doctor", description = "Registers a new doctor account for the given user.")
    public ResponseEntity<ApiResponse<DoctorDto>> registerDoctor(
            @RequestParam String clerkUserId,
            @RequestParam("dto") String dtoJson,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {

        // parse JSON string into DoctorDto
        DoctorDto dto = objectMapper.readValue(dtoJson, DoctorDto.class);

        UserDto user = userService.getUserByClerkId(clerkUserId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        DoctorDto doctor = doctorService.registerDoctor(dto, user.getId(), image);
        return ResponseEntity.ok(new ApiResponse<>("Doctor registered successfully", true, doctor));
    }

    @PutMapping(value = "/{docId}", consumes = "multipart/form-data")
    @Operation(summary = "Update doctor profile", description = "Updates the doctor information for a given user.")
    public ResponseEntity<ApiResponse<DoctorDto>> updateDoctor(
            @PathVariable Long docId,
            @RequestParam("dto") String dtoJson,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {

        DoctorDto dto = objectMapper.readValue(dtoJson, DoctorDto.class);

        DoctorDto updated = doctorService.updateDoctor(dto, docId, image);
        return ResponseEntity.ok(new ApiResponse<>("Doctor updated successfully", true, updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorDto>> getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id)
                .map(dto -> ResponseEntity.ok(new ApiResponse<>("Doctor found", true, dto)))
                .orElseThrow(() -> new BadRequestException("Doctor not found with ID: " + id));
    }

    @GetMapping("/verified")
    public ResponseEntity<ApiResponse<List<DoctorDto>>> getAllVerifiedDoctors() {
        List<DoctorDto> doctors = doctorService.getAllDoctorsByVerifiedAccounts();
        return ResponseEntity.ok(new ApiResponse<>("Verified doctors fetched successfully", true, doctors));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DoctorDto>>> getDoctorsByLocation(@RequestParam String address) {
        List<DoctorDto> doctors = doctorService.getAllDoctorsByLocation(address);
        return ResponseEntity.ok(new ApiResponse<>("Doctors by location fetched successfully", true, doctors));
    }

    @DeleteMapping("/{docId}")
    public ResponseEntity<ApiResponse<String>> deleteDoctor(@PathVariable Long docId) {
        doctorService.deleteDoctor(docId);
        return ResponseEntity.ok(new ApiResponse<>("Doctor account blocked successfully", true, null));
    }
}
