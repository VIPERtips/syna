package co.zw.blexta.syna.availability;

import java.util.List;

public interface AvailabilityService {
    AvailabilityDTO createAvailability(AvailabilityDTO dto,Long doctorId);
    AvailabilityDTO updateAvailability(Long id, AvailabilityDTO dto);
    void deleteAvailability(Long id);
    List<AvailabilityDTO> getDoctorAvailability(Long doctorId);
    AvailabilityDTO getAvailabilityById(Long id);
}
