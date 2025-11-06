package co.zw.blexta.syna.doctor;

import java.time.LocalDateTime;

import co.zw.blexta.syna.user.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Doctor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long doctorId;
	@OneToOne
	@JoinColumn(name = "user_id",unique = true)
	private User user;
	private String imageUrl;
	private String specialty;
	private String bio;
	private String education;
	private String address;
	private String longitude;
	private String latitude;
	private Long avgRating;
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.accountStatus = AccountStatus.PENDING;
	}

	@PreUpdate
	public void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
	
	public enum AccountStatus {
		PENDING,
		APPROVED,
		REJECTED,
		BLOCKED,
		HIDDEN,
	}
}
