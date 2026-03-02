import { vi } from "vitest";
import { LoggerService } from "./logger-service";

/**
 * @author Peter Szrnka
 */
describe('LoggerService', () => {
    let service: LoggerService = new LoggerService();

    it('should print error message', () => {
        console.error = vi.fn();

        // act
        service.error('message', 'param');

        // assert
        expect(console.error).toHaveBeenCalled();
    });

    it('should print info message', () => {
        console.info = vi.fn();

        // act
        service.info('message', 'param');

        // assert
        expect(console.info).toHaveBeenCalled();
    });

    it('should print log message', () => {
        console.log = vi.fn();

        // act
        service.log('message', 'param');

        // assert
        expect(console.log).toHaveBeenCalled();
    });

    it('should print warn message', () => {
        console.warn = vi.fn();

        // act
        service.warn('message', 'param');

        // assert
        expect(console.warn).toHaveBeenCalled();
    });
});