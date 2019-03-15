import { Component, OnInit } from '@angular/core';
import { SessionService } from 'src/app/core/session.service';
import { switchMap } from 'rxjs/operators';
import { LogSession } from 'src/app/core/logging.service';
import { DataPoint } from 'src/app/core/data.model';

@Component({
  selector: 'app-data-view',
  templateUrl: './data-view.component.html',
  styleUrls: ['./data-view.component.css']
})
export class DataViewComponent implements OnInit {

  constructor(private service: SessionService) { }

  plotData: DataPoint[];

  ngOnInit() {
  }

  handlePlot(data: DataPoint[]) {
    this.plotData = data;
  }

 
}
