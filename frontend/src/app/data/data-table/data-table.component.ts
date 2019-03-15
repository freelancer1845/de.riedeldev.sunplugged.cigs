import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { DataPoint } from 'src/app/core/data.model';
import { SessionService } from 'src/app/core/session.service';
import { LogSession } from 'src/app/core/logging.service';
import { saveAs } from 'file-saver';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.css']
})
export class DataTableComponent implements OnInit {

  dataSource = new MatTableDataSource<LogSession>();
  columnsToDisplay = ['startDate', 'endDate', 'actions'];
  sessions: LogSession[];

  constructor(private service: SessionService) { }

  @Output() toChart = new EventEmitter<DataPoint[]>();

  ngOnInit() {
    this.service.getSessions().subscribe(s => this.dataSource.data = s.reverse());
  }

  showSession(session: LogSession) {
    this.service.getSessionRaw(session).subscribe(s => this.toChart.emit(s));
  }

  test() {
    this.downloadSession(this.sessions[0]);
  }

  downloadSession(session: LogSession) {
    this.service.getSessionAsCSV(session).subscribe(res => {
      const blob = new Blob([res], { type: 'text/csv' });
      saveAs(blob, session.logFilePath.split('\\').pop().split('/').pop());
    });
  }

  deleteSession(session: LogSession) {
    this.service.deleteSession(session).subscribe(v => {
      this.dataSource.data = this.dataSource.data.filter(obj => obj.id !== session.id);
    });
  }

  showChart(session: LogSession) {
    this.service.getSessionRaw(session).subscribe(data => this.toChart.emit(data));
  }




}
