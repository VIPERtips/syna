package co.zw.blexta.syna.doctor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);

    List<Doctor> findByAccountStatus(Doctor.AccountStatus status);

    List<Doctor> findByAddressContainingIgnoreCaseAndAccountStatus(
            String address,
            Doctor.AccountStatus status
    );
}
