package quvoncuz.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import quvoncuz.enums.TransactionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "user_id")
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private ProfileEntity user;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column
    private Long amount;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
