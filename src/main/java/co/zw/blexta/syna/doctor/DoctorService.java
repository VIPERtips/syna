package co.zw.blexta.syna.doctor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface DoctorService {
	DoctorDto registerDoctor(DoctorDto dto,Long docId,MultipartFile image) throws IOException;
	Optional<DoctorDto> getDoctorById(Long id);
	DoctorDto updateDoctor(DoctorDto doctorDto, Long docId,MultipartFile image) throws IOException;
	List<DoctorDto> getAllDoctorsByVerifiedAccounts();
	List<DoctorDto> getAllDoctorsByLocation(String address);
	void deleteDoctor(Long userId);
	
}
