/* eslint-disable no-undef */
/* eslint-disable @typescript-eslint/no-var-requires */
module.exports = {
  preset: 'jest-preset-angular',
  globalSetup: 'jest-preset-angular/global-setup',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  globals : {
    tsconfig: 'tsconfig.spec.json'
  },
  testEnvironment: "jsdom",

  collectCoverage : true,
  collectCoverageFrom : [ "**/*.ts" ],
  coveragePathIgnorePatterns : [
    ".module.ts",
    "main.ts",
    "polyfills.ts",
    "<rootDir>/src/environments/",
    "<rootDir>/src/mocks",
    "window.provider.ts"
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