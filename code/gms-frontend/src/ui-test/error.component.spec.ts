import { expect, test } from '@playwright/test';

/**
 * @author Peter Szrnka
 */
test.describe('Error component', () => {

  test.beforeEach(async ({ page }) => await page.goto(`/error`));

  test('When component loaded fully, Then error message is visible', async ({ page }) => {
    await expect(page.locator('.error-icon')).toBeVisible();
  });
});
