package io.github.stackpan.tasque.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "Column")
@Table(name = "columns")
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class BoardColumn implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private Long position;

    @Column(length = 64, nullable = false)
    private String name;

    @Column(length = 128)
    private String description;

    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @Column(columnDefinition = "timestamptz", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(columnDefinition = "timestamptz", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToMany(mappedBy = "column")
    private List<Card> cards = new ArrayList<>();

}
