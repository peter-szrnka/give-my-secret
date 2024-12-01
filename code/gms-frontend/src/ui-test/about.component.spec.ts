import { expect, test } from '@playwright/test';

/**
 * @author Peter Szrnka
 */
test.describe('About component', () => {

  test.beforeEach(async ({ page }) => await page.goto(`/about`));

  test('When component loaded fully, Then main texts are visible', async ({ page }) => {
    await expect(page.getByText('About Give My Secret')).toBeVisible();
    await expect(page.locator('li').getByText('Application version: MOCK')).toBeVisible();
    await expect(page.locator('li').getByText('Built time')).toBeVisible();
    await expect(page.locator('li').getByText('Status: OK')).toBeVisible();
  });
});
