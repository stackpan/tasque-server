package io.github.stackpan.tasque.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements UserDetails, BoardOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(length = 16, nullable = false, unique = true)
    private String username;

    @Column(length = 64, nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", length = 32, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 64)
    private String lastName;

    @Column(name = "profile_picture_url", length = 1024)
    private String profilePictureUrl;

    @Column(length = 4096, nullable = false)
    private String password;

    @Column(name = "email_verified_at", columnDefinition = "timestamptz")
    private Instant emailVerifiedAt;

    @Column(name = "created_at", columnDefinition = "timestamptz", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamptz", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(name = "deleted_at", columnDefinition = "timestamptz")
    private Instant deletedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
