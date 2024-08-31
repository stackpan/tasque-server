package io.github.stackpan.tasque.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cards")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id")
    private BoardColumn column;

    @Column(length = 1024)
    private String body;

    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @Column(columnDefinition = "timestamptz", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(columnDefinition = "timestamptz", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

}
