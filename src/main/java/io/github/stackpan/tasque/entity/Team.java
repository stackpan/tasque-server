package io.github.stackpan.tasque.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teams")
@Data
public class Team implements BoardOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String name;

    private String description;

    private String profilePicture;

    @OneToMany(mappedBy = "team")
    private List<TeamMember> teamMembers;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "timestamptz", nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "timestamptz", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at", columnDefinition = "timestamptz")
    private Instant deletedAt;

}
