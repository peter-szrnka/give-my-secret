import { Component, Input, OnInit } from "@angular/core";
import { MatIconModule } from "@angular/material/icon";

const SETTINGS_MAP: any = {
  'success': { icon: 'check_circle', iconColor: 'green', styleClass: 'success' },
  'information': { icon: 'information', iconColor: 'blue', styleClass: 'information' },
  'warning': { icon: 'warning', iconColor: 'orange', styleClass: 'warning' }
};

/**
 * @author Peter Szrnka
 */
@Component({
  standalone: true,
  imports: [MatIconModule],
  selector: 'information-message',
  templateUrl: './information-message.component.html',
  styleUrls: ['./information-message.component.scss']
})
export class InformationMessageComponent implements OnInit {

  @Input() severity: 'success' | 'information' | 'warning' = 'warning';
  icon: string = '';
  iconColor: string = '';
  styleClass: string = '';

  ngOnInit(): void {
    this.icon = SETTINGS_MAP[this.severity].icon;
    this.iconColor = SETTINGS_MAP[this.severity].iconColor;
    this.styleClass = SETTINGS_MAP[this.severity].styleClass;
  }
}