package at.ac.uibk.swa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Authenticable {

    protected Authenticable(String username, String password, UUID token, Set<Permission> permissions) {
        this(null, username, password, token, permissions);
    }

    protected Authenticable(String username, String password, Set<Permission> permissions) {
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
    private Set<Permission> permissions = new HashSet<>();

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
}
