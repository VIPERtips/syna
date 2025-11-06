package co.zw.blexta.syna.doctor;

import co.zw.blexta.syna.user.User;

public class DoctorMapper {

    public static DoctorDto toDto(Doctor doc) {
        if (doc == null) return null;

        String fullName = "Dr. " + doc.getUser().getFirstName() + " " + doc.getUser().getLastName();

        return DoctorDto.builder()
                .doctorId(doc.getDoctorId())
                .fullName(fullName)
                .imageUrl(doc.getImageUrl())
                .specialty(doc.getSpecialty())
                .bio(doc.getBio())
                .education(doc.getEducation())
                .address(doc.getAddress())
                .longitude(doc.getLongitude())
                .latitude(doc.getLatitude())
                .avgRating(doc.getAvgRating())
                .accountStatus(doc.getAccountStatus())
                .build();
    }

    public static Doctor toEntity(DoctorDto dto, User user, String imageUrl) {
        if (dto == null || user == null) return null;

        return Doctor.builder()
                .user(user)
                .imageUrl(imageUrl != null ? imageUrl : dto.getImageUrl())
                .specialty(dto.getSpecialty())
                .bio(dto.getBio())
                .education(dto.getEducation())
                .address(dto.getAddress())
                .longitude(dto.getLongitude())
                .latitude(dto.getLatitude())
                .avgRating(dto.getAvgRating() != null ? dto.getAvgRating() : 0L)
                .accountStatus(dto.getAccountStatus() != null ? dto.getAccountStatus() : Doctor.AccountStatus.PENDING)
                .build();
    }

    public static void updateEntity(Doctor doctor, DoctorDto dto, String imageUrl) {
        if (doctor == null || dto == null) return;

        doctor.setSpecialty(dto.getSpecialty());
        doctor.setBio(dto.getBio());
        doctor.setEducation(dto.getEducation());
        doctor.setAddress(dto.getAddress());
        doctor.setLongitude(dto.getLongitude());
        doctor.setLatitude(dto.getLatitude());
        if (imageUrl != null) doctor.setImageUrl(imageUrl);
        if (dto.getAccountStatus() != null) doctor.setAccountStatus(dto.getAccountStatus());
    }
}
