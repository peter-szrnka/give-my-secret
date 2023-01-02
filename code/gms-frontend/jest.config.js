// eslint-disable-next-line no-undef
module.exports = {
    name : "give-my-secret",
    preset : "jest-preset-angular",
    globals : {
        'ts-jest' : {
            tsconfig : "tsconfig.spec.json"
        }
    },
    roots : ["src/"],
    testMatch :["**/+(*.)+(spec).+(ts|js)"],
    collectCoverage : false,
    collectCoverageFrom : [ "**/*.ts" ],
    coveragePathIgnorePatterns : [
        ".module.ts",
        "main.ts",
        "polyfills.ts",
        "<rootDir>/src/environments/",
        "<rootDir>/src/mocks"
    ],
    coverageThreshold : {
        global : {
            branches : 25,
            functions : 25,
            lines : 25,
            statements : 25
        }
    },
    reporters : [
        "default"
    ]
};