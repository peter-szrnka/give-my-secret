package io.github.gms.abstraction;

import io.github.gms.common.abstraction.AbstractController;
import io.github.gms.common.abstraction.GmsService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Peter Szrnka
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractClientControllerTest<S extends GmsService, T extends AbstractController<S>> {

    protected T controller;
    protected S service;
}
