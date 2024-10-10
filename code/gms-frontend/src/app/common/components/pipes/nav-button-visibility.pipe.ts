import { Pipe, PipeTransform } from "@angular/core";
import { ButtonConfig } from "../nav-back/button-config";

/**
 * @author Peter Szrnka
 */
@Pipe({
    name: 'navButtonVisibility',
    pure : false,
    standalone: true
  })
  export class NavButtonVisibilityPipe implements PipeTransform {
  
    transform(value: ButtonConfig[]): any {
          return value.filter(item => item.visibilityCondition ?? true);
      }
  }