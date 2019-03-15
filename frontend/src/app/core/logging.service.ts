import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment.prod';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { RxStompService } from '@stomp/ng2-stompjs';
import { DataPoint } from './data.model';

@Injectable({
  providedIn: 'root'
})
export class LoggingService {



  constructor(private http: HttpClient, private rxStompService: RxStompService) { }

  startLogging(speed: number): Observable<any> {

    const paramss = new HttpParams().set('speed', speed.toString());
    return this.http.get(environment.api + '/api/log/start', { params: paramss });
  }

  stopLogging(): Observable<any> {
    return this.http.get(environment.api + '/api/log/stop');
  }

  currentState(): Observable<LogState> {
    return this.http.get<LogState>(environment.api + '/api/log/state');
  }
  subscribeToState(onnext: (state: LogState) => any) {
    return this.rxStompService.watch('/topic/state').pipe(map(m => JSON.parse(m.body) as LogState)).subscribe(onnext);
  }

  subscribeToLivePoints(onnext: (point: DataPoint) => any) {
    return this.rxStompService.watch('/topic/data').pipe(map(m => JSON.parse(m.body) as DataPoint)).subscribe(onnext)
  }
}


export interface LogSession {
  id: number;
  startData: Date;
  endDate: Date;
  comment: string;
  logFilePath: string;

}

export interface LogState {
  state: boolean;
  session?: LogSession;
}
