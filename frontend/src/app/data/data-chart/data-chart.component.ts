import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { DataPoint } from 'src/app/core/data.model';
import { DatePipe } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import { MatCheckboxChange } from '@angular/material/checkbox';


export interface Plottable {
  name: string,
  get: (point: DataPoint) => {},
}

const TABLE_DATA: Plottable[] = [
  {
    name: 'Pressure Baking Pa',
    get: point => {
      return { x: new Date(point.dateTime), y: point.pressureBakingPa };
    }
  },
  {
    name: 'Pressure Full Range 1(Pa)',
    get: point => {
      return { x: new Date(point.dateTime), y: point.pressureFullRangeuPa_1 };
    }
  },
  {
    name: 'Pressure Full Range 2(Pa)',
    get: point => {
      return { x: new Date(point.dateTime), y: point.pressureFullRangeuPa_2 };
    }
  },
  {
    name: 'Magnetron 1 A',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_1_I };
    }
  },
  {
    name: 'Magnetron 2 A',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_2_I };
    }
  },
  {
    name: 'Magnetron 3 A',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_3_I };
    }
  },
  {
    name: 'Magnetron 4 A',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_4_I };
    }
  },
  {
    name: 'Magnetron 5 A',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_5_I };
    }
  },
  {
    name: 'Magnetron 6 A',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_6_I };
    }
  },
  {
    name: 'Magnetron 1 V',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_1_V };
    }
  },
  {
    name: 'Magnetron 2 V',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_2_V };
    }
  },
  {
    name: 'Magnetron 3 V',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_3_V };
    }
  },
  {
    name: 'Magnetron 4 V',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_4_V };
    }
  },
  {
    name: 'Magnetron 5 V',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_5_V };
    }
  },
  {
    name: 'Magnetron 6 V',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_6_V };
    }
  },
  {
    name: 'Magnetron 1 V',
    get: point => {
      return { x: new Date(point.dateTime), y: point.magnetron_1_V };
    }
  },
  {
    name: 'I Transfer Device 1 (A)',
    get: point => {
      return { x: new Date(point.dateTime), y: point.transferDevice_1_I };
    }
  },
  {
    name: 'I Transfer Device 2 (A)',
    get: point => {
      return { x: new Date(point.dateTime), y: point.transferDevice_2_I };
    }
  },
  {
    name: 'I Transfer Device 3 (A)',
    get: point => {
      return { x: new Date(point.dateTime), y: point.transferDevice_3_I };
    }
  },
  {
    name: 'U Transfer Device 1 (V)',
    get: point => {
      return { x: new Date(point.dateTime), y: point.transferDevice_1_V };
    }
  },
  {
    name: 'U Transfer Device 2 (V)',
    get: point => {
      return { x: new Date(point.dateTime), y: point.transferDevice_2_V };
    }
  },
  {
    name: 'U Transfer Device 3 (V)',
    get: point => {
      return { x: new Date(point.dateTime), y: point.transferDevice_3_V };
    }
  },
  {
    name: 'Substrate Temperature 1',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_1 };
    }
  },
  {
    name: 'Substrate Temperature 2',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_2 };
    }
  },
  {
    name: 'Substrate Temperature 3',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_3 };
    }
  },
  {
    name: 'Substrate Temperature 4',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_4 };
    }
  },
  {
    name: 'Substrate Temperature 5',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_5 };
    }
  },
  {
    name: 'Substrate Temperature 6',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_6 };
    }
  },
  {
    name: 'Substrate Temperature 7',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_7 };
    }
  },
  {
    name: 'Substrate Temperature 8',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_8 };
    }
  },
  {
    name: 'Substrate Temperature 9',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_9 };
    }
  },
  {
    name: 'Substrate Temperature 10',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_10 };
    }
  },
  {
    name: 'Substrate Temperature 11',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_11 };
    }
  },
  {
    name: 'Substrate Temperature 12',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_12 };
    }
  },
  {
    name: 'Substrate Temperature 13',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_13 };
    }
  },
  {
    name: 'Substrate Temperature 14',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateTemperature_14 };
    }
  },
  {
    name: 'Cell Temperature 1',
    get: point => {
      return { x: new Date(point.dateTime), y: point.temperatureCell_1 };
    }
  },
  {
    name: 'Cell Temperature 2',
    get: point => {
      return { x: new Date(point.dateTime), y: point.temperatureCell_2 };
    }
  },
  {
    name: 'Cell Temperature 3',
    get: point => {
      return { x: new Date(point.dateTime), y: point.temperatureCell_3 };
    }
  },
  {
    name: 'Cell Temperature 4',
    get: point => {
      return { x: new Date(point.dateTime), y: point.temperatureCell_4 };
    }
  },
  {
    name: 'Manifold Temperature 1',
    get: point => {
      return { x: new Date(point.dateTime), y: point.temperatureManifold_1 };
    }
  },
  {
    name: 'Manifold Temperature 2',
    get: point => {
      return { x: new Date(point.dateTime), y: point.temperatureManifold_2 };
    }
  },
  {
    name: 'Manifold Temperature 3',
    get: point => {
      return { x: new Date(point.dateTime), y: point.temperatureManifold_3 };
    }
  },
  {
    name: 'Manifold Temperature 4',
    get: point => {
      return { x: new Date(point.dateTime), y: point.temperatureManifold_4 };
    }
  },
  {
    name: 'Cell Temperature NaF',
    get: point => {
      return { x: new Date(point.dateTime), y: point.temperatureCell_NaF };
    }
  },
  {
    name: 'Turbo Pump Rpm',
    get: point => {
      return { x: new Date(point.dateTime), y: point.turboRpm };
    }
  },
  {
    name: 'Substrate Rotation Speed (Rpm)',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateRotationSpeedRpm };
    }
  },
  {
    name: 'Substrate Length (mm)',
    get: point => {
      return { x: new Date(point.dateTime), y: point.substrateLengthMM };
    }
  },






];

@Component({
  selector: 'app-data-chart',
  templateUrl: './data-chart.component.html',
  styleUrls: ['./data-chart.component.css']
})
export class DataChartComponent implements OnInit {


  columnsToDisplay: string[] = ['select', 'property'];
  dataSource = new MatTableDataSource<Plottable>(TABLE_DATA.slice(0, 16));
  dataSource1 = new MatTableDataSource<Plottable>(TABLE_DATA.slice(16, 32));
  dataSource2 = new MatTableDataSource<Plottable>(TABLE_DATA.slice(32, TABLE_DATA.length));
  selection = new SelectionModel<Plottable>(true, []);

  @ViewChild(BaseChartDirective) chart: BaseChartDirective;
  @ViewChild('chart') canvas: ElementRef;

  visible = false;

  public chartOptions = {
    elements: {
      line: {
        tension: 0 // disables bezier curves
      }
    },
    scaleShowVerticalLines: false,
    responsive: true,
    // Container for pan options
    pan: {
      enabled: true,
      mode: 'x',
      speed: 10,
      threshold: 10,
      // rangeMin: {},
      // rangeMax: {},
    },

    // Container for zoom options
    zoom: {
      enabled: true,
      drag: false,
      mode: 'x',
      rangeMin: {
        // Format of min zoom range depends on scale type
        x: 10000// let say the zoom range min is 1 hour
      },
      rangeMax: {},
    },
    scales: {
      xAxes: [
        {
          id: 'x',
          type: 'time',
          time: {}
        }
        //   {

        //   type: 'time',
        //   time: {
        //     min: new Date(0).getTime(),
        //     max: new Date(20).getTime()
        //   }
        // }
      ]
    }
  };
  public chartType = 'line';
  public barChartLegend = true;
  public chartData = [
    { data: {}, label: 'Series A' },
    { data: {}, label: 'Series B' }
  ];


  localData: DataPoint[];
  @Input()
  set data(input: DataPoint[]) {
    // this.selection.clear();
    if (input !== undefined) {
      this.visible = true;
    } else {
      this.visible = false;
      return;
    }

    // if (this.selection.isEmpty()) {
    //   this.selection.select(TABLE_DATA[0]);
    // }
    this.localData = input;

    this.updateLabels();
    this.createData();

    this.chart.ngOnDestroy();
    this.chart.options = this.chartOptions;
    if (this.chartData === undefined || this.chartData.length === 0) {
      this.chartData = [{ data: {}, label: 'Series A' },
      { data: {}, label: 'Series B' }
      ];
    }
    this.chart.chart = this.chart.getChartBuilder(this.chart.ctx);

  }
  constructor() { }

  ngOnInit() {
    this.selection.changed.subscribe(change => {
      if (this.localData !== undefined) {
        this.updateData();
      }
    });
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach(row => this.selection.select(row));
  }

  trySelect(change: MatCheckboxChange, row: any) {
    if (change.checked) {
      if (this.selection.selected.length > 4) {
        this.selection.deselect(row);
        change.source.writeValue(false);
      } else {
        this.selection.select(row);
      }
    } else {
      this.selection.deselect(row);
    }

  }

  refreshData() {
    this.data = this.localData;
  }

  private updateLabels() {
    this.chartOptions.pan = {
      enabled: true,
      mode: 'x',
      speed: 10,
      threshold: 10,
      // rangeMin: {
      //   // Format of min zoom range depends on scale type
      //   x: new Date(this.localData[0].dateTime).getTime() // let say the zoom range min is 1 hour
      // },
      // rangeMax: {
      //   // Format of max zoom range depends on scale type
      //   x: new Date(this.localData[this.localData.length - 1].dateTime).getTime(),
      // },
    };
    this.chartOptions.zoom = {
      enabled: true,
      drag: false,
      mode: 'x',
      rangeMin: {
        // Format of min zoom range depends on scale type
        x: 1000// let say the zoom range min is 1 hour
      },
      rangeMax: {
        // Format of max zoom range depends on scale type
        x: new Date(this.localData[this.localData.length - 1].dateTime).getTime() - new Date(this.localData[0].dateTime).getTime(),
      },
    };
    this.chartOptions.scales = {
      xAxes: [{
        id: 'x',
        type: 'time',
        time: {
          unit: 'second',
          displayFormats: {
            second: 'HH:mm:ss'
          },
          stepSize: '1200',
          // min: new Date(this.localData[0].dateTime).getTime(),
          min: new Date(this.localData[this.localData.length - 1].dateTime).getTime() - 3600000,
          max: new Date(this.localData[this.localData.length - 1].dateTime).getTime()
        }
      }]
    };
  }

  private createData() {
    this.chartData = [];
    this.selection.selected.forEach(plottable => {
      const endTime = new Date(this.localData[this.localData.length - 1].dateTime);
      let data = this.localData.filter(point => endTime.getTime() < new Date(point.dateTime).getTime() + 7200000);
      if (data.length > 1000) {
        data = data.reverse().filter((point, idx) => idx % 20 === 0).reverse();
      }

      this.chartData.push(
        {
          data: data.map(point => {
            return plottable.get(point);
          }), label: plottable.name
        }
      );
    });
  }

  private updateData() {
    this.chartData = this.chartData.filter(data => this.selection.selected.find(plottable => plottable.name === data.label) !== undefined);
    this.selection.selected.filter(plottable => this.chartData.find(data => data.label === plottable.name) === undefined)
      .forEach(plottable => {

        const endTime = new Date(this.localData[this.localData.length - 1].dateTime);
        let data = this.localData.filter(point => endTime.getTime() < new Date(point.dateTime).getTime() + 7200000);
        if (data.length > 1000) {
          data = data.reverse().filter((point, idx) => idx % 20 === 0).reverse();
        }

        this.chartData.push(
          {
            data: data.map(point => {
              return plottable.get(point);
            }), label: plottable.name
          }
        );
      });

    if (this.selection.isEmpty()) {
      this.chartData = [{ data: {}, label: 'Series A' },
      { data: {}, label: 'Series B' }
      ];
    }
  }

}
