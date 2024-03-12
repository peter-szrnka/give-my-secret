import { Pipe, PipeTransform } from "@angular/core";
import moment from "moment";
import { ButtonConfig } from "../nav-back/button-config";

/**
 * @author Peter Szrnka
 */
@Pipe({
    name: 'navButtonVisibility',
    pure : false
  })
  export class NavButtonVisibilityPipe implements PipeTransform {
  
    transform(value: ButtonConfig[]): any {
          return value.filter(item => item.visibilityCondition === true);
      }
  }