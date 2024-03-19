import { NavButtonVisibilityPipe } from "./nav-button-visibility.pipe";


describe('NavButtonVisibilityPipe', () => {

    it.each([
        [ "Button1", false, "http://localhost:8080/", undefined ],
        [ "Button2", false, "http://localhost:8080/", true ],
        [ "Button3", false, "http://localhost:8080/", false ]
    ])('Should fail at form validation', (label: string, primary: boolean, url: string, visibilityCondition?: boolean ) => {
        const pipe: NavButtonVisibilityPipe = new NavButtonVisibilityPipe();

        pipe.transform([
            { label : label, primary : primary, url : url, visibilityCondition: visibilityCondition },
        ]);
    });
});