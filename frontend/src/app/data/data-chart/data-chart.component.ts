import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { DataPoint } from 'src/app/core/data.model';
import { DatePipe } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';


export interface Plottable {
  name: string,
  get: (point: DataPoint) => {},
}

const TABLE_DATA: Plottable[] = [
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
    name: 'Pressure Baking Pa',
    get: point => {
      return { x: new Date(point.dateTime), y: point.pressureBakingPa };
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
  dataSource = new MatTableDataSource<Plottable>(TABLE_DATA);
  selection = new SelectionModel<Plottable>(true, []);

  @ViewChild(BaseChartDirective) chart: BaseChartDirective;
  @ViewChild('chart') canvas: ElementRef;

  visible = false;

  public chartOptions = {
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
          min: new Date(this.localData[0].dateTime).getTime(),
          max: new Date(this.localData[this.localData.length - 1].dateTime).getTime()
        }
      }]
    };
  }

  private createData() {
    this.chartData = [];
    this.selection.selected.forEach(plottable => {
      this.chartData.push(
        {
          data: this.localData.map(point => {
            return plottable.get(point);
          }), label: plottable.name
        }
      );
    });
  }

  private updateData() {
    // this.chartData = []
    this.chartData = this.chartData.filter(data => this.selection.selected.find(plottable => plottable.name === data.label) !== undefined);
    this.selection.selected.filter(plottable => this.chartData.find(data => data.label !== plottable.name) === undefined).forEach(plottable => {
      console.log("Plotting");
      console.log(plottable.name);
      this.chartData.push(
        {
          data: this.localData.map(point => {
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
