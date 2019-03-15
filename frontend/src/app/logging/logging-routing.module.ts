import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LiveViewComponent } from './live-view/live-view.component';

const routes: Routes = [
  {
    path: '',
    component: LiveViewComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LoggingRoutingModule { }
