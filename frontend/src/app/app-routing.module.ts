import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  {
    path: 'data',
    loadChildren: './data/data.module#DataModule'
  },
  {
    path: 'logging',
    loadChildren: './logging/logging.module#LoggingModule'
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'logging'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
