package carsharingapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Data
@SQLDelete(sql = "UPDATE roles SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Column(name = "name", nullable = false, unique = true)
    private RoleName name;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public enum RoleName {
        MANAGER,
        CUSTOMER
    }
}
