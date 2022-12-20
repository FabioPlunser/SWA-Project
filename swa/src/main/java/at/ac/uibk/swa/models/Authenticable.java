package at.ac.uibk.swa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Getter
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
    @JsonIgnore
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "auth_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID id;

    @Setter
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Setter
    @JsonIgnore
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "password_hash", nullable = false)
    private String passwdHash;

    @Setter
    @JsonIgnore
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "token", nullable = true, unique = true)
    private UUID token;

    @Setter
    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Permission.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "permission", joinColumns = @JoinColumn(name = "person_id"))
    private Set<Permission> permissions = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        return (this == o) || ((o instanceof Authenticable a) && (a.id == this.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String toString() {
        return this.username;
    }
}
