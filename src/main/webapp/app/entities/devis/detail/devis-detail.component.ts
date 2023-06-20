import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDevis } from '../devis.model';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

@Component({
  selector: 'jhi-devis-detail',
  templateUrl: './devis-detail.component.html',
})
export class DevisDetailComponent implements OnInit {
  devis: IDevis | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ devis }) => {
      this.devis = devis;
    });
  }
  public openPDF(): void {
    const DATA: any = document.getElementById('htmlData');
    html2canvas(DATA).then(canvas => {
      const fileWidth = 208;
      const fileHeight = (canvas.height * fileWidth) / canvas.width;
      const FILEURI = canvas.toDataURL('image/png');
      const PDF = new jsPDF('p', 'mm', 'a4');
      const position = 0;
      PDF.addImage(FILEURI, 'PNG', 0, position, fileWidth, fileHeight);
      PDF.save('angular-demo.pdf');
    });
  }
  previousState(): void {
    window.history.back();
  }
}
