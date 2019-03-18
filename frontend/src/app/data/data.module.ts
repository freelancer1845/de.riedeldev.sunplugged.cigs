import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';

import { DataRoutingModule } from './data-routing.module';
import { DataViewComponent } from './data-view/data-view.component';
import { DataChartComponent } from './data-chart/data-chart.component';
import { DataTableComponent } from './data-table/data-table.component';

import { ChartsModule } from 'ng2-charts';

// Material
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { AreyousureComponent } from '../dialogs/areyousure/areyousure.component';

@NgModule({
  declarations: [DataViewComponent, DataChartComponent, DataTableComponent],
  imports: [
    CommonModule,
    DataRoutingModule,
    ChartsModule,
    FlexLayoutModule,

    // Material
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatCheckboxModule,
    MatDialogModule,
  ],
  providers: [DatePipe],
 
})
export class DataModule { }
