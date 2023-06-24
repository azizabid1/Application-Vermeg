import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { AuthServerProvider } from 'app/core/auth/auth-jwt.service';
import { IPoste, Poste } from 'app/entities/poste/poste.model';
import { PosteService } from 'app/entities/poste/service/poste.service';
import { Observable, combineLatest, finalize } from 'rxjs';

@Component({
  selector: 'jhi-postecheck',
  templateUrl: './postecheck.component.html',
})
export class PostecheckComponent implements OnInit {
  postes!: IPoste[];
  @Output() changeSelectedPoste = new EventEmitter<Poste>();
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  _poste!: Poste;
  idUser!: number | null;
  isSaving = false;
  @Input()
  public set poste(poste) {
    this._poste = poste;
  }
  public get poste(): Poste {
    return this._poste;
  }
  constructor(
    protected posteService: PosteService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal,
    protected authServerProvider: AuthServerProvider
  ) {}

  onChangeSelect(i: number): void {
    this.poste = this.postes[i];
    this.changeSelectedPoste.emit(this.poste);
  }

  addChoixPoste(): void {
    console.log(this.poste);
    if (this.poste && this.poste.users) {
      if (this.poste.users.length > 0) {
        for (let i = 0; i < this.poste.users.length; i++) {
          this.poste.users[i]?.id === this.authServerProvider.getId();
          console.log(i);
        }
      } else {
        this.poste.users[0]?.id === this.authServerProvider.getId();
        console.log(this.poste.users[0]?.id);
      }
    }

    console.log(this.poste);
    this.subscribeToSaveResponse(this.posteService.update(this.poste));
    window.history.back();
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.posteService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IPoste[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }

  ngOnInit(): void {
    this.handleNavigation();
  }

  previousState(): void {
    window.history.back();
  }

  trackId(_index: number, item: IPoste): number {
    return item.id!;
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPoste>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = +(page ?? 1);
      const sort = (params.get(SORT) ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === ASC;
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  protected onSuccess(data: IPoste[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/Poste_user'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.postes = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
