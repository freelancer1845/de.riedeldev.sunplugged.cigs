import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';
import { LoggingService } from 'src/app/core/logging.service';
import { Subscription } from 'rxjs';
import { DataPoint } from 'src/app/core/data.model';

@Component({
  selector: 'app-live-view',
  templateUrl: './live-view.component.html',
  styleUrls: ['./live-view.component.css']
})
export class LiveViewComponent implements OnInit, OnDestroy {




  logspeedFormGroup: FormGroup;
  data: DataPoint = undefined;
  private subscription: Subscription;

  constructor(private formBuilder: FormBuilder, private logService: LoggingService) { }

  ngOnInit() {
    this.logspeedFormGroup = this.formBuilder.group({
      logspeedCtrl: ['1000', Validators.pattern('[0-9]+')],
    });
    this.subscription = this.logService.subscribeToLivePoints(point => this.data = point);
  }
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  startLogging() {
    this.logService.startLogging(this.logspeedFormGroup.value.logspeedCtrl).subscribe(s => console.log(s), err => console.log(err));
  }

  stopLogging() {
    this.logService.stopLogging().subscribe(s => console.log(s), err => console.log(err));
  }

}
