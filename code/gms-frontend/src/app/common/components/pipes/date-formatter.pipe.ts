import { Pipe, PipeTransform } from '@angular/core';
import moment from 'moment';

@Pipe({
  name: 'momentPipe',
  pure : false
})
export class MomentPipe implements PipeTransform {

  transform(value: any, dateFormat: string): any {
        return !value ? '' : moment(value).format(dateFormat);
    }
}