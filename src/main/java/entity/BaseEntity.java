package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity<ID extends Number> implements Serializable {

    public static final String CREATED_AT_COLUMN = "created_at";
    public static final String UPDATED_AT_COLUMN = "updated_at";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;


    @Column(name = CREATED_AT_COLUMN, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = UPDATED_AT_COLUMN)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
