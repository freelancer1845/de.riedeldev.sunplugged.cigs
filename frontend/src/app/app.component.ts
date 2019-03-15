import { Component, OnInit, OnDestroy } from '@angular/core';
import { LoggingService, LogState } from './core/logging.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {

  private subscription: Subscription;

  title = 'frontend';

  logState: LogState;

  constructor(private logService: LoggingService) {

  }

  ngOnInit(): void {
    this.logService.currentState().subscribe(s => this.logState = s);
    this.subscription = this.logService.subscribeToState(s => this.logState = s);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
