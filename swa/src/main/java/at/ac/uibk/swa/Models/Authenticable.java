package at.ac.uibk.swa.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Authenticable {

    public Authenticable(String username, String passwdHash, UUID token, List<Permission> permissions) {
        this(null, username, passwdHash, token, permissions);
    }

    public Authenticable(String username, String passwdHash, ArrayList<Permission> permissions) {
        this(null, username, passwdHash, null, permissions);
    }

    public Authenticable(String username, String passwdHash) {
        this(username, passwdHash, new ArrayList<>());
    }

    @Id
    @JsonIgnore
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "AuthId", nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID id;

    @Setter
    @NonNull
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "Username", nullable = false, unique = true)
    private String username;

    @Setter
    @NonNull
    @JsonIgnore
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "PasswordHash", nullable = false)
    private String passwdHash;

    @Setter
    @JsonIgnore
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "Token", nullable = true, unique = true)
    private UUID token;

    @Setter
    @ElementCollection(targetClass = Permission.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "PersonPermissions",
            joinColumns=@JoinColumn(name = "personId")
    )
    @Enumerated(EnumType.STRING)
    private List<Permission> permissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authenticable that = (Authenticable) o;
        return username.equals(that.username) && passwdHash.equals(that.passwdHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, passwdHash);
    }

    public String toString() {
        return this.username;
    }
}
