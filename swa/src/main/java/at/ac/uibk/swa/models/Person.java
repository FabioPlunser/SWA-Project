package at.ac.uibk.swa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "person")
// NOTE: This changes the name of the "id"-Column inherited from Authenticable to "person_id"
@AttributeOverride(name = "id", column = @Column(name = "person_id"))
public class Person extends Authenticable implements Serializable {

    public Person(String username, String email, String passwdHash, UUID token, Set<GrantedAuthority> permissions) {
        super(username, passwdHash, token, permissions);
        this.email = email;
        this.createdDecks = new ArrayList<>();
        this.savedDecks = new ArrayList<>();
    }

    public Person(String username, String email, String passwdHash, Set<GrantedAuthority> permissions) {
        this(username, email, passwdHash, null, permissions);
    }

    @Setter(AccessLevel.PRIVATE)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "email", nullable = false)
    private String email;

    @Builder.Default
    @Getter(onMethod = @__( @JsonIgnore ))
    @Setter(AccessLevel.PRIVATE)
    @OneToMany(
            mappedBy = "creator",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private List<Deck> createdDecks = new ArrayList<>();

    @Setter(AccessLevel.PRIVATE)
    @Getter(onMethod = @__( @JsonIgnore))
    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "person_saved_deck",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "deck_id")
    )
    private List<Deck> savedDecks = new ArrayList<>();

    /**
     * Gets the Person's ID.
     * This method is a renamed version of {@link Authenticable#getId()} so the ID field will be
     * included in the JSON-Serialization of a {@link Person}.
     *
     * @implNote This may return null if the {@link Person} was not saved to the Database.
     * @return The ID of the Person.
     */
    @JsonInclude
    public UUID getPersonId() {
        return this.getId();
    }

    @Override
    public String toString() {
        return this.getUsername();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
