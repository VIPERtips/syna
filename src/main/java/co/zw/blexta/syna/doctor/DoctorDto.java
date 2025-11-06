package co.zw.blexta.syna.doctor;

import co.zw.blexta.syna.doctor.Doctor.AccountStatus;
import co.zw.blexta.syna.user.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorDto {
	private Long doctorId;
	private String fullName;
	private String imageUrl;
	private String specialty;
	private String bio;
	private String education;
	private String address;
	private String longitude;
	private String latitude;
	private Long avgRating;
	private AccountStatus accountStatus;
}
