import { expect, test } from '@playwright/test';

/**
 * @author Peter Szrnka
 */
test.describe('Reset password component', () => {

  test.beforeEach(async ({ page }) => await page.goto(`/password_reset`));

  test('When component loaded fully, Then reset password', async ({ page }) => {
    await expect(page.getByText('Username')).toBeVisible();
    await expect(page.locator('input[name=username]')).toBeVisible();

    await expect(page.getByText('Submit')).toBeDisabled();

    page.locator('input[name=username]').fill('test');

    await page.locator('button').getByText('Submit').click();

    await page.waitForTimeout(300);

    await expect(page.getByText('Password request sent to admins!')).toBeVisible();
    await page.locator('button').getByText('Close').click();
  });
});
