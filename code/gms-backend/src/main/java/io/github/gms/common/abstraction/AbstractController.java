package io.github.gms.common.abstraction;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Peter Szrnka
 * @since 1.0
 * 
 * @param <T> An extended GmsService
 */
public abstract class AbstractController<T extends GmsService> {

	@Autowired
	protected T service;
}
