import { defineConfig } from "vitest/config";


export default defineConfig({
  test: {
    globals: true,
    environment: "jsdom",
    testTimeout: 60000,
    setupFiles: ["src/test-setup.ts"],
    reporters: [
        "default"
    ],
    isolate: true,
    pool: 'forks',
    cache: false,
    css: false,
    maxConcurrency: 1,
    fileParallelism: false
  }
});