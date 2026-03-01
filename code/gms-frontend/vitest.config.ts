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
    coverage: {
      provider: "v8",
      reporter: ["text", "text-summary", "lcov"],
      include: ["src/**/*.ts"],
      exclude: ["**/*.spec.ts", "src/test-setup.ts", "node_modules/**"]
    }
  }
});