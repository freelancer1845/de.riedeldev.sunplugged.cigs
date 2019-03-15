import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { DataPoint } from 'src/app/core/data.model';
import { DatePipe } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';

@Component({
  selector: 'app-data-chart',
  templateUrl: './data-chart.component.html',
  styleUrls: ['./data-chart.component.css']
})
export class DataChartComponent implements OnInit {

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
  set data(data: DataPoint[]) {
    if (data !== undefined) {
      this.visible = true;
    } else {
      this.visible = false;
      return;
    }
    this.localData = data.slice(0, 50);

    this.updateLabels();
    this.updateData();

    this.chart.ngOnDestroy();
    this.chart.options = this.chartOptions;
    this.chart.chart = this.chart.getChartBuilder(this.chart.ctx);

  }
  constructor() { }

  ngOnInit() {
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

  private updateData() {
    this.chartData = [
      {
        data: this.localData.map(point => {
          const s = {
            x: new Date(point.dateTime), y: point.magnetron_1_I
          }
          return s;
        }), label: 'Magnetron 1 I'
      }
    ];
  }

}
