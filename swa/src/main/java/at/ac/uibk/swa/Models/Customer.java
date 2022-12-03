package at.ac.uibk.swa.Models;

import jakarta.persistence.*;
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

    @Id
    @Column(name = "CustomerId", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID customerId;

    @Column(name = "Username", nullable = false)
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

    @Column(name = "Token", nullable = true)
    @Setter
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID token;

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
