package io.github.gms.common.abstraction;

/**
 * @author Peter Szrnka
 * @since 1.0
 * 
 * @param <T> An extended GmsService
 */
public abstract class AbstractController<T extends GmsService> {

	protected T service;

	protected AbstractController(T service) {
		this.service = service;
	}
}
