package co.zw.blexta.syna.availability;


import co.zw.blexta.syna.common.exception.ResourceNotFoundException;
import co.zw.blexta.syna.doctor.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public AvailabilityDTO createAvailability(AvailabilityDTO dto, Long doctorId) {
        var doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        var existing = availabilityRepository
                .findByDoctor_DoctorIdAndStartTimeAndEndTime(doctorId, dto.getStartTime(), dto.getEndTime())
                .orElse(null);

        if (existing != null) {
            existing.setRecurring(Availability.RecurrenceType.valueOf(dto.getRecurring().toUpperCase()));
            existing.setStartTime(dto.getStartTime());
            existing.setEndTime(dto.getEndTime());
            return toDTO(availabilityRepository.save(existing));
        }

        var entity = Availability.builder()
                .doctor(doctor)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .recurring(Availability.RecurrenceType.valueOf(dto.getRecurring().toUpperCase()))
                .build();

        return toDTO(availabilityRepository.save(entity));
    }

    @Override
    public AvailabilityDTO updateAvailability(Long id, AvailabilityDTO dto) {
        var availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));

        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        availability.setRecurring(Availability.RecurrenceType.valueOf(dto.getRecurring().toUpperCase()));

        return toDTO(availabilityRepository.save(availability));
    }

    @Override
    public void deleteAvailability(Long id) {
        availabilityRepository.deleteById(id);
    }

    @Override
    public List<AvailabilityDTO> getDoctorAvailability(Long doctorId) {
        return availabilityRepository.findByDoctor_DoctorId(doctorId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AvailabilityDTO getAvailabilityById(Long id) {
        return availabilityRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
    }

    private AvailabilityDTO toDTO(Availability entity) {
        return AvailabilityDTO.builder()
                .id(entity.getId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .recurring(entity.getRecurring().name())
                .build();
    }
}
