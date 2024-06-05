import { LoggerService } from "./logger-service";

/**
 * @author Peter Szrnka
 */
describe('LoggerService', () => {
    let service: LoggerService = new LoggerService();

    it('should print error message', () => {
        console.error = jest.fn();

        // act
        service.error('message', 'param');

        // assert
        expect(console.error).toHaveBeenCalled();
    });

    it('should print info message', () => {
        console.info = jest.fn();

        // act
        service.info('message', 'param');

        // assert
        expect(console.info).toHaveBeenCalled();
    });

    it('should print log message', () => {
        console.log = jest.fn();

        // act
        service.log('message', 'param');

        // assert
        expect(console.log).toHaveBeenCalled();
    });

    it('should print warn message', () => {
        console.warn = jest.fn();

        // act
        service.warn('message', 'param');

        // assert
        expect(console.warn).toHaveBeenCalled();
    });
});