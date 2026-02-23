import { vi } from "vitest";

/**
 * Complex object to mock a {@link FormGroup}
 * 
 * @author Peter Szrnka
 */
export const FORM_GROUP_MOCK = {
    group : vi.fn().mockReturnValue({
        _updateTreeValidity : vi.fn(),
        _registerOnCollectionChange : vi.fn(),
        get : vi.fn().mockReturnValue({
            status : "VALID",
            value : "",
            parent : vi.fn(),
            registerOnChange : vi.fn(),
            registerOnDisabledChange : vi.fn(),
            updateValueAndValidity : vi.fn(),
            setValue : vi.fn()
        })
    })
};

describe('FORM_GROUP_MOCK', () => {
    it('should be loaded', () => {
        expect(FORM_GROUP_MOCK).toBeDefined();
    });
});