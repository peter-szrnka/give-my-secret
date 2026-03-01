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
    pool: 'vmThreads',
    css: false,
    deps: {
      optimizer:{
        web: {
          enabled: true,
        }
      }
    }
  }
});