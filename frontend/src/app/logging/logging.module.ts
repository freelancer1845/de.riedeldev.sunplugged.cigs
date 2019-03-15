import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { LoggingRoutingModule } from './logging-routing.module';
import { LiveViewComponent } from './live-view/live-view.component';
import { FlexLayoutModule } from '@angular/flex-layout';

import { CoreModule } from './../core/core.module';

// Material
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';


@NgModule({
  declarations: [LiveViewComponent],
  imports: [
    CommonModule,
    LoggingRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    FlexLayoutModule,

    CoreModule,

    // Material
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    MatListModule,
    MatButtonModule,
    MatDividerModule,
  ]
})
export class LoggingModule { }
