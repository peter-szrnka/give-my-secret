package io.github.gms.secure.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.gms.common.abstraction.AbstractClientController;
import io.github.gms.common.abstraction.GmsClientService;

/**
 * @author Peter Szrnka
 */
@ExtendWith(MockitoExtension.class)
abstract class AbstractClientControllerTest<S extends GmsClientService, T extends AbstractClientController<S>> {

    protected T controller;
    protected S service;
}
