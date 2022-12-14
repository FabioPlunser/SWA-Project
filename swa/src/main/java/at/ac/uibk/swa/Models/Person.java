package at.ac.uibk.swa.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "Persons")
@AttributeOverride(name = "id", column = @Column(name = "PersonId"))
public class Person extends Authenticable implements Serializable {

    public Person(String username, String email, String passwdHash, UUID token, List<Permission> permissions) {
        super(username, passwdHash, token, permissions);
        this.email = email;
    }

    public Person(String username, String email, String passwdHash, List<Permission> permissions) {
        this(username, email, passwdHash, null, permissions);
    }

    @Setter
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "Email", nullable = false)
    private String email;

    @Setter
    @JsonIgnore
    @Builder.Default
    @OneToMany(
            mappedBy = "creator",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch=FetchType.EAGER
    )
    private List<Deck> createdDecks = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "person_deck",
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
        return this.getUsername() + "\n" + this.getPermissions().toString() + "\n" + this.getPersonId();
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
