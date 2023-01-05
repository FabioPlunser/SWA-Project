package at.ac.uibk.swa.models;

import at.ac.uibk.swa.models.annotations.OnlyDeserialize;
import at.ac.uibk.swa.models.annotations.OnlySerialize;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class Authenticable implements UserDetails, CredentialsContainer {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    //region Constructors
    protected Authenticable(String username, String password, UUID token, Set<GrantedAuthority> permissions) {
        this(null, username, password, false, token, permissions);
    }

    protected Authenticable(String username, String password, Set<GrantedAuthority> permissions) {
        this(username, password, null, permissions);
    }

    protected Authenticable(String username, String password) {
        this(username, password, new HashSet<>());
    }
    //endregion

    //region Fields
    @Id
    // NOTE: Classes that extend this should create a Getter with @JsonInclude to rename the ID.
    @JsonIgnore
    @Setter(AccessLevel.PRIVATE)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "auth_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID id;

    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @OnlyDeserialize
    @Setter(AccessLevel.NONE)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "password", nullable = false)
    private String password;

    // NOTE: Implicitly do not store this field in the Database
    //       The Password always has to be hashed before storing it in the database.
    //       That means if a new Instance is created using a Constructor, the Password is not hashed (see CTOR).
    //       But if it is pulled from the database, the Password is assumed to be hashed (see default Value).
    @Transient
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private boolean password_hashed = true;

    @OnlySerialize
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "token", nullable = true, unique = true)
    private UUID token;

    @Builder.Default
    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Permission.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "permission", joinColumns = @JoinColumn(name = "auth_id"))
    private Set<GrantedAuthority> permissions = Permission.defaultAuthorities();

    public void setPermissions(Set<Permission> permissions) {
        // SAFETY: Permission implements GrantedAuthority
        this.permissions = (Set<GrantedAuthority>) (Set) permissions;
    }
    //endregion

    //region equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        return (this == o) || ((o instanceof Authenticable a) && (this.id != null) && (this.id.equals(a.id)));
    }

    @Override
    public int hashCode() {
        // NOTE: This will intentionally throw an Exception if cardId is null.
        return this.id.hashCode();
    }

    public String toString() {
        return this.username;
    }
    //endregion

    //region Setting/Hashing Password
    public void setPassword(String password) {
        this.password = password;
        this.password_hashed = false;
    }

    public void hashPassword(PasswordEncoder encoder) {
        if (!this.password_hashed) {
            this.password = encoder.encode(this.password);
            this.password_hashed = true;
        }
    }
    //endregion

    //region UserDetails Implementation
    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.permissions;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
        this.password_hashed = false;
        this.token = null;
    }
    //endregion
}
