import { defineConfig } from "vitest/config";

const isCI = !!process.env.CI;


export default defineConfig({
  test: {
    globals: true,
    environment: "jsdom",
    testTimeout: 60000,
    setupFiles: ["src/test-setup.ts"],
    reporters: [
        "default"
    ],
    isolate: isCI,
    pool: 'forks',
    cache: false,
    css: false,
    maxConcurrency: isCI ? 1 : 2,
    fileParallelism: false
  }
});