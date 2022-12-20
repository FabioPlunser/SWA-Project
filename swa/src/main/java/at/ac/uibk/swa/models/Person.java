package at.ac.uibk.swa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "person")
@AttributeOverride(name = "id", column = @Column(name = "person_id"))
public class Person extends Authenticable implements Serializable {

    public Person(String username, String email, String passwdHash, UUID token, Set<Permission> permissions) {
        super(username, passwdHash, token, permissions);
        this.email = email;
    }

    public Person(String username, String email, String passwdHash, Set<Permission> permissions) {
        this(username, email, passwdHash, null, permissions);
    }

    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "email", nullable = false)
    private String email;

    @JsonIgnore
    @Builder.Default
    @OneToMany(
            mappedBy = "creator",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private List<Deck> createdDecks = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "person_saved_deck",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "deck_id")
    )
    private List<Deck> savedDecks = new ArrayList<>();

    @JsonInclude
    public UUID getPersonId() {
        return this.getId();
    }

    @Override
    public String toString() {
        return this.getUsername();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person u)
            return u.getPersonId() == this.getPersonId();

        return false;
    }

    @Override
    public int hashCode() {
        return this.getPersonId().hashCode();
    }
}
