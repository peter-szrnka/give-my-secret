// Mocks, globals, and other setup for tests can be placed here.
import 'zone.js';
import 'zone.js/testing';
import '@angular/compiler';
import "cross-fetch/polyfill";
import { vi } from "vitest";

global.fetch = global.fetch ?? vi.fn(() =>
  Promise.resolve({ json: () => Promise.resolve({}) })
);
