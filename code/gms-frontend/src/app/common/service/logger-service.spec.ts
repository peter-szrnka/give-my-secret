import { LoggerService } from "./logger-service";

/**
 * @author Peter Szrnka
 */
describe('LoggerService', () => {
    let service: LoggerService = new LoggerService();

    it('should print info log message', () => {
        console.info = jest.fn();

        // act
        service.info('message', 'param');

        // assert
        expect(console.info).toHaveBeenCalled();
    });
});