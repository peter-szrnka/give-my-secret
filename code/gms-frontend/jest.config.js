/* eslint-disable no-undef */
/* eslint-disable @typescript-eslint/no-var-requires */
module.exports = {
  preset: 'jest-preset-angular',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  globals : {
    tsconfig: 'tsconfig.spec.json'
  },
  testEnvironment: "jsdom",
  testMatch: ["src/app/**/*.spec.ts"],

  collectCoverage : true,
  collectCoverageFrom : [ "src/app/**/*.ts" ],
  coveragePathIgnorePatterns : [
    ".module.ts",
    "<rootDir>/src/main.ts",
    "<rootDir>/src/polyfills.ts",
    "<rootDir>/src/environments/",
    "<rootDir>/src/mocks",
    "<rootDir>/src/app/window.provider.ts"
  ],
  coverageThreshold : {
      global : {
          branches : 95,
          functions : 95,
          lines : 95,
          statements : 95
      }
  },
  reporters : [
      "default"
  ]
};
