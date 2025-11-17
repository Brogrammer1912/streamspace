package com.brogrammer.streamspace.preferences;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class Preference {

    @Id
    private Integer prefId;
    private String name;
    private boolean enabled;
}
