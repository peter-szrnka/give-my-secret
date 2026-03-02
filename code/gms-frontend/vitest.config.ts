import { defineConfig } from "vitest/config";


export default defineConfig({
  test: {
    globals: true,
    environment: "jsdom",
    testTimeout: 15000,
    setupFiles: ["src/test-setup.ts"],
    reporters: [
        "default"
    ],
    isolate: false,
    cache: false,
    css: false,
    maxConcurrency: 1,
    maxWorkers: 1
  }
});