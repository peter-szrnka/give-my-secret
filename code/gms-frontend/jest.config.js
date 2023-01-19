/* eslint-disable @typescript-eslint/no-var-requires */
/* eslint-disable no-undef */
module.exports = {
    preset : "jest-preset-angular",
    globalSetup: "jest-preset-angular/global-setup",
    setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],

    moduleDirectories: ["node_modules", '<rootDir>' ],

    //moduleFileExtensions: ["js", "json", "ts", "mjs"],
    //testPathIgnorePatterns: ['<rootDir>/node_modules/'],
    transformIgnorePatterns: ['node_modules/(?!@angular|rxjs)'],
    
    
    
    //testEnvironment: "jsdom",
    /*transform: {
        '^.+\\.(ts|js|html)$': 'jest-preset-angular'
    }*/
};