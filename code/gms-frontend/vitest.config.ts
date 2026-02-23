import { defineConfig } from "vitest/config";


export default defineConfig({
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: ["./src/test-setup.ts"],
    pool: 'threads',
    isolate: false,
    poolOptions: {
      threads: {
        singleThread: true,
      },
    },
    reporters: [
        "default"
    ]
  }
});