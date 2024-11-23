import { expect, test } from '@playwright/test';

test.describe('Error component', () => {

  test.beforeEach(async ({ page }) => await page.goto(`/error`));

  test('should load main container', async ({ page }) => {
    await expect(page.locator('.error-icon')).toBeVisible();
  });
});
