package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DevisMapperTest {

    private DevisMapper devisMapper;

    @BeforeEach
    public void setUp() {
        devisMapper = new DevisMapperImpl();
    }
}
