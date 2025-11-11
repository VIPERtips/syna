package co.zw.blexta.syna.doctor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import co.zw.blexta.syna.common.exception.ConflictException;
import co.zw.blexta.syna.common.exception.ResourceNotFoundException;
import co.zw.blexta.syna.fileUpload.FileUploadService;
import co.zw.blexta.syna.user.User;
import co.zw.blexta.syna.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    public Optional<DoctorDto> getDoctorByUserId(String clerkUserId) {
        User user = userRepository.findByClerkUserId(clerkUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return doctorRepository.findByUser(user)
                .map(DoctorMapper::toDto);
    }


    @Override
    public DoctorDto registerDoctor(DoctorDto dto, Long userId, MultipartFile image) throws IOException {
        doctorRepository.findByUserId(userId)
                .ifPresent(d -> { throw new ConflictException("Doctor already registered for this user"); });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ConflictException("User not found with id " + userId));

        String imageUrl = uploadImageIfPresent(image);

        Doctor doctor = DoctorMapper.toEntity(dto, user, imageUrl);
        doctorRepository.save(doctor);

        return DoctorMapper.toDto(doctor);
    }

    @Override
    public DoctorDto updateDoctor(DoctorDto dto, Long docId, MultipartFile image) throws IOException {
        Doctor doctor = doctorRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found for id " + docId));

        String imageUrl = uploadImageIfPresent(image);
        DoctorMapper.updateEntity(doctor, dto, imageUrl);

        doctorRepository.save(doctor);
        return DoctorMapper.toDto(doctor);
    }

    @Override
    public Optional<DoctorDto> getDoctorById(Long id) {
        return doctorRepository.findById(id).map(DoctorMapper::toDto);
    }

    @Override
    public List<DoctorDto> getAllDoctorsByVerifiedAccounts() {
        return doctorRepository.findByAccountStatus(Doctor.AccountStatus.APPROVED)
                .stream().map(DoctorMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<DoctorDto> getAllDoctorsByLocation(String address) {
        return doctorRepository.findByAddressContainingIgnoreCaseAndAccountStatus(address, Doctor.AccountStatus.APPROVED)
                .stream().map(DoctorMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteDoctor(Long docId) {
        Doctor doctor = doctorRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found for id " + docId));
        doctor.setAccountStatus(Doctor.AccountStatus.BLOCKED);
        doctorRepository.save(doctor);
    }

    @Override
    public DoctorDto approveDoctor(Long docId) {
        Doctor doctor = doctorRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found for id " + docId));

        if (doctor.getAccountStatus() == Doctor.AccountStatus.APPROVED) {
            throw new ConflictException("Doctor is already approved");
        }
        doctor.setAccountStatus(Doctor.AccountStatus.APPROVED);
        doctorRepository.save(doctor);

        User user = doctor.getUser();
        user.setRole(User.Role.DOCTOR);
        userRepository.save(user);

        return DoctorMapper.toDto(doctor);
    }


    private String uploadImageIfPresent(MultipartFile image) throws IOException {
        return (image != null && !image.isEmpty()) ? fileUploadService.storeFile(image) : null;
    }
}
