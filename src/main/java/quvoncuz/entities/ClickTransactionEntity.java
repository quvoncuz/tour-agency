package quvoncuz.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import quvoncuz.enums.ClickTransactionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "click_transaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "click_trans_id")
    private Long clickTransId;

    @Column(name = "click_paydoc_id")
    private Long clickPaydocId;

    @Column(name = "merchant_trans_id", nullable = false)
    private String merchantTransId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClickTransactionStatus status;

    @Column(name = "sign_string", columnDefinition = "TEXT")
    private String signString;

    @Column(name = "sign_time")
    private LocalDateTime signTime;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private ProfileEntity user;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}