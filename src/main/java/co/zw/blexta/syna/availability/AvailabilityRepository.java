package co.zw.blexta.syna.availability;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability,Long> {
    List<Availability> findByDoctor_DoctorId(Long doctorId);
    Optional<Availability> findByDoctor_DoctorIdAndStartTimeAndEndTime(Long doctorId, LocalDateTime startTime, LocalDateTime endTime);

}
