
/**
 * Complex object to mock a {@link FormGroup}
 */
export const FORM_GROUP_MOCK = {
    group : jest.fn().mockReturnValue({
        _updateTreeValidity : jest.fn(),
        _registerOnCollectionChange : jest.fn(),
        get : jest.fn().mockReturnValue({
            status : "VALID",
            value : "",
            parent : jest.fn(),
            registerOnChange : jest.fn(),
            registerOnDisabledChange : jest.fn(),
            updateValueAndValidity : jest.fn(),
            setValue : jest.fn()
        })
    })
};

describe('FORM_GROUP_MOCK', () => {
    it('should be loaded', () => {
        expect(FORM_GROUP_MOCK).toBeDefined();
    });
});