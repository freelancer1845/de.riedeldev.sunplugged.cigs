import { Injectable } from '@angular/core';
import { LogSession, LoggingService } from './logging.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment.prod';
import { DataPoint } from './data.model';
import { shareReplay, switchMap, retry, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  private sessionsCache = [];

  constructor(private http: HttpClient, private loggingService: LoggingService) { }


  getSessions(): Observable<LogSession[]> {
    return this.http.get<LogSession[]>(environment.api + '/api/sessions');
  }

  getSessionAsCSV(session: LogSession): Observable<any> {
    const par = new HttpParams().set('id', session.id.toString());
    // this.http.get<Response>(environment.api + '/api/sessions/loadcsv', { params: par }).pipe(map(response => new Blob([response.blob()], { type: 'text/csv' })));
    return this.http.get(environment.api + '/api/sessions/loadcsv', { params: par, responseType: 'blob' });
  }

  getSessionRaw(session: LogSession): Observable<DataPoint[]> {
    return this.loggingService.currentState().pipe(switchMap(state => {
      if (state.state) {
        if (state.session.id === session.id) {
          this.sessionsCache[session.id] = this.loadSessionRaw(session).pipe(shareReplay(1));
          return this.sessionsCache[session.id];
        }
      }
      if (this.sessionsCache[session.id] === undefined) {
        this.sessionsCache[session.id] = this.loadSessionRaw(session).pipe(shareReplay(1));
      }
      return this.sessionsCache[session.id];

    }));
  }

  deleteSession(session: LogSession): Observable<any> {
    const par = new HttpParams().set('id', session.id.toString());
    return this.http.get(environment.api + '/api/sessions/delete', { params: par });
  }

  private loadSessionRaw(session: LogSession) {
    const par = new HttpParams().set('id', session.id.toString());
    return this.http.get<DataPoint[]>(environment.api + '/api/sessions/loadraw', { params: par }).pipe(retry(2));
  }

}
