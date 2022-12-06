package at.ac.uibk.swa.Models;

import at.ac.uibk.swa.Models.Permissions.Permission;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Persons")
public class Person implements Serializable {

    public Person(String username, String email, String passwdHash, UUID token, List<Permission> permissions) {
        this(UUID.randomUUID(), username, email, passwdHash, token,
                new ArrayList<>(), new ArrayList<>(), permissions);
    }

    public Person(String username, String email, String passwdHash, List<Permission> permissions) {
        this(UUID.randomUUID(), username, email, passwdHash, null,
                new ArrayList<>(), new ArrayList<>(), permissions);
    }

    @Id
    // @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "CustomerId", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID customerId;

    @Column(name = "Username", nullable = false, unique = true)
    @Setter
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String username;

    @Column(name = "Email", nullable = false)
    @Setter
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String email;

    @Column(name = "PasswordHash", nullable = false)
    @Setter
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String passwdHash;

    @Column(name = "Token", nullable = true, unique = true)
    @Setter
    @JdbcTypeCode(SqlTypes.UUID)
    @JsonIgnore
    private UUID token;

    @Setter
    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    private List<Deck> decks = new ArrayList<>();

    @Setter
    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "person", orphanRemoval = true)
    private List<DeckReference> deckReferences = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "PersonPermissions",
            joinColumns=@JoinColumn(name = "customerId")
    )
    @Column(name="Permission")
    private List<Permission> permissions;

    @Override
    public String toString() {
        return this.username;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person u)
            return u.customerId == this.customerId;

        return false;
    }

    @Override
    public int hashCode() {
        return this.customerId.hashCode();
    }
}
