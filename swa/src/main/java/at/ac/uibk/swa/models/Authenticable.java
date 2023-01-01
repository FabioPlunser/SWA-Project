package at.ac.uibk.swa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Authenticable implements UserDetails, CredentialsContainer {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    
    protected Authenticable(String username, String password, UUID token, Set<GrantedAuthority> permissions) {
        this(username, password, permissions);
    }

    protected Authenticable(String username, String password, Set<GrantedAuthority> permissions) {
        this(null, username, password, null, permissions);
    }

    protected Authenticable(String username, String passwdHash) {
        this(username, passwdHash, new HashSet<>());
    }

    @Id
    // NOTE: This JsonIgnore is fine because this is an abstract Class
    //       Classes that extend this should create a Getter with @JsonInclude to rename the ID.
    @JsonIgnore
    @Setter(AccessLevel.PRIVATE)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "auth_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID id;

    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "password_hash", nullable = false)
    private String passwdHash;

    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "token", nullable = true, unique = true)
    private UUID token;

    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Permission.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "permission", joinColumns = @JoinColumn(name = "auth_id"))
    private Set<GrantedAuthority> permissions = new HashSet<>();

    public void setPermissions(Set<Permission> permissions) {
        // SAFETY: Permission implements GrantedAuthority
        this.permissions = (Set<GrantedAuthority>) (Set) permissions;
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || ((o instanceof Authenticable a) && (this.id.equals(a.id)));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return this.username;
    }

    //region UserDetails Implementation
    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.passwdHash;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.permissions;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

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
    public void eraseCredentials() {
        this.passwdHash = null;
    }
    //endregion
}
