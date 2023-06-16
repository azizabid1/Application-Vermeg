import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IStatusEmploye } from '../status-employe.model';

@Component({
  selector: 'jhi-status-employe-detail',
  templateUrl: './status-employe-detail.component.html',
})
export class StatusEmployeDetailComponent implements OnInit {
  statusEmploye: IStatusEmploye | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ statusEmploye }) => {
      this.statusEmploye = statusEmploye;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
