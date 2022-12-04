package at.ac.uibk.swa.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Customers")
public class Customer implements Serializable {

    public Customer(String username, String email, String passwdHash) {
        this(username, email, passwdHash, false);
    }

    public Customer(String username, String email, String passwdHash, boolean isAdmin) {
        this(UUID.randomUUID(), username, email, passwdHash, isAdmin, UUID.randomUUID());
    }

    @Id
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

    @Column(name = "IsAdmin", nullable = false)
    @Setter
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean isAdmin;

    @Column(name = "Token", nullable = true, unique = true)
    @Setter
    @JdbcTypeCode(SqlTypes.UUID)
    @JsonIgnore
    private UUID token;

    // TODO: Create Decks and "Deck Reference" Reference

    @Override
    public String toString() {
        return this.username;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Customer u)
            return u.customerId == this.customerId;

        return false;
    }

    @Override
    public int hashCode() {
        return this.customerId.hashCode();
    }
}
