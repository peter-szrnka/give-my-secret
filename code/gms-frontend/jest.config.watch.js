// eslint-disable-next-line @typescript-eslint/no-var-requires, no-undef
const jestConfig = require("./jest.config");

jestConfig.collectCoverage = false;
jestConfig.reporters = ['default']