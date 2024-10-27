import { Pipe, PipeTransform } from '@angular/core';
import { TranslatorService } from '../../../service/translator-service';

/**
 * @author Peter Szrnka
 */
@Pipe({
  name: 'translate',
  pure : false,
  standalone: false
})
export class TranslatorPipe implements PipeTransform {

  constructor(private readonly service: TranslatorService) { }

  transform(value: string, arg?: any): any {
    return this.service.translate(value, arg);
  }
}