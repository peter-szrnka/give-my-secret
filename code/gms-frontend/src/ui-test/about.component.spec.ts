import { expect, test } from '@playwright/test';

test.beforeEach(async ({ page }) => {
  await page.goto(`/about`);
});

test.describe('About component', () => {

    test('should load main texts', async ({ page }) => {
      await expect(page.getByText('About Give My Secret')).toBeVisible();
      await expect(page.locator('li').getByText('Application version: MOCK')).toBeVisible();
      await expect(page.locator('li').getByText('Built time')).toBeVisible();
      await expect(page.locator('li').getByText('Status: OK')).toBeVisible();
    });
});
