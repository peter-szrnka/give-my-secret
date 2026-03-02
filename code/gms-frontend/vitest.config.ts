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
    fileParallelism: false,

    coverage: {
      provider: 'istanbul',
      reporter: ['text', 'text-summary', 'html', 'lcov'],
      exclude: [
        'src/**/*.spec.ts',
        'src/test-setup.ts',
        'src/vitest-env.d.ts'
      ]
    }
  }
});