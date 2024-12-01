import { expect, test } from '@playwright/test';

/**
 * @author Peter Szrnka
 */
test.describe('Help component', () => {

  test.beforeEach(async ({ page }) => await page.goto(`/help`));

  test('When component loaded fully, Then main texts are visible', async ({ page }) => {
    await expect(page.locator('mat-card-title').getByText('Error codes')).toBeVisible();
    await expect(page.locator('mat-card-title').getByText('Website')).toBeVisible();
    await expect(page.getByText('Tutorials, command line tools, examples')).toBeVisible();
  });
});
