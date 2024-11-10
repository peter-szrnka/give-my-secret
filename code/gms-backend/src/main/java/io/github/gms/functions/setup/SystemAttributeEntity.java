package io.github.gms.functions.setup;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static io.github.gms.common.util.Constants.ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_system_attribute")
@EqualsAndHashCode(callSuper = false)
public class SystemAttributeEntity extends AbstractGmsEntity {

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;
}