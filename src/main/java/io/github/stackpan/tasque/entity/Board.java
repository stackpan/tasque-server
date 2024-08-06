package io.github.stackpan.tasque.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "boards")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Board implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(length = 64, nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    @Column(name = "banner_picture_url", length = 1024)
    private String bannerPictureUrl;

    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "owner_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;

    @Any
    @AnyKeyJavaClass(UUID.class)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false, insertable = false)
    @Column(name = "owner_type", nullable = false, updatable = false, insertable = false)
    @AnyDiscriminatorValue(discriminator = "USER", entity = User.class)
    @AnyDiscriminatorValue(discriminator = "TEAM", entity = Team.class)
    private BoardOwner owner;

    @Column(name = "created_at", columnDefinition = "timestamptz", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamptz", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(name = "deleted_at", columnDefinition = "timestamptz")
    private Instant deletedAt;

    public enum OwnerType {
        USER,
        TEAM
    }

}
