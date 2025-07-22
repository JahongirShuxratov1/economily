package org.example.economily.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.economily.entity.BaseEntity;
import org.example.economily.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
    @Column(unique = true)
    private String email;
    private String fullname;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private String password;
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRole> role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Code> codes;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.stream()
                .map(r -> (GrantedAuthority) () -> "ROLE_" + r.getName())
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the user's email address as the username for authentication purposes.
     *
     * @return the user's email
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indicates whether the user's account has not expired.
     *
     * @return always {@code true}, as user accounts are never considered expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isSuperAdmin() {
        return role != null && role.stream()
                .anyMatch(r -> "SUPER_ADMIN".equalsIgnoreCase(r.getName()));
    }
}
