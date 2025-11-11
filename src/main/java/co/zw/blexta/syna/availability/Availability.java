package co.zw.blexta.syna.availability;

import co.zw.blexta.syna.doctor.Doctor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurrenceType recurring;
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public  void onCreate(){
        if(recurring == null){
            recurring = RecurrenceType.NONE;
        }
        this.createdAt = LocalDateTime.now();
    }

    @PostUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    public enum RecurrenceType {
        NONE,
        DAILY,
        WEEKLY,
    }
}
