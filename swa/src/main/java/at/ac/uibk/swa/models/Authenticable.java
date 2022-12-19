package at.ac.uibk.swa.models;

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

    protected Authenticable(String username, String password, UUID token, List<Permission> permissions) {
        this(null, username, password, token, permissions);
    }

    protected Authenticable(String username, String password, List<Permission> permissions) {
        this(null, username, password, null, permissions);
    }

    protected Authenticable(String username, String passwdHash) {
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
    private String password;

    @Setter
    @JsonIgnore
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "Token", unique = true)
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
