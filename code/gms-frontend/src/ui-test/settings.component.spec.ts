import { expect, test } from '@playwright/test';

/**
 * @author Peter Szrnka
 */
test.describe('Settings component', () => {

  test.beforeEach(async ({ page }) => await page.goto(`/settings`));

  test('When component loaded fully, Then save new password', async ({ page }) => {
    await page.locator('mat-panel-title').getByText("Password").click();
    await expect(page.getByText('Current password')).toBeVisible();
    await expect(page.getByText('New password')).toBeVisible();
    await expect(page.getByText('Confirm password')).toBeVisible();

    await expect(page.locator('input[name="oldCredential"]')).toBeVisible();
    await expect(page.locator('input[name="newCredential1"]')).toBeVisible();
    await expect(page.locator('input[name="newCredential2"]')).toBeVisible();

    await page.locator('input[name="oldCredential"]').fill('password');
    await page.locator('input[name="newCredential1"]').fill('newpassword');
    await page.locator('input[name="newCredential2"]').fill('newpassword');

    await page.locator('button').getByText('Save').click();

    await page.waitForTimeout(300);

    await page.locator('button').getByText('Close').click();
  });

  test('When component loaded fully, Then display MFA settings', async ({ page }) => {
    await page.locator('mat-panel-title').nth(1).click();

    await expect(page.locator('mat-checkbox[name="mfaEnabled"]')).toBeVisible();

    await page.locator('mat-checkbox[name="mfaEnabled"]').click();
    await page.waitForTimeout(300);

    await page.locator('img').getByAltText('MFA QR code').isVisible();
  });

  test('When component loaded fully, Then change language', async ({ page }) => {
    await page.locator('mat-panel-title').nth(2).click();

    await expect(page.locator('mat-select[name="language"]')).toBeVisible();

    await page.locator('mat-select[name="language"]').click();
    await page.locator('mat-option').getByText('English').click();
  });
});
