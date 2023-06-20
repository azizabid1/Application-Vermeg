import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'Poste_user',
        data: { pageTitle: 'vermegApp.checkPoste.home.title' },
        loadChildren: () => import('./PosteUser/posteCheck.module').then(m => m.PosteCheckModule),
      },
      {
        path: 'departement',
        data: { pageTitle: 'vermegApp.departement.home.title' },
        loadChildren: () => import('./departement/departement.module').then(m => m.DepartementModule),
      },
      {
        path: 'devis',
        data: { pageTitle: 'vermegApp.devis.home.title' },
        loadChildren: () => import('./devis/devis.module').then(m => m.DevisModule),
      },
      {
        path: 'equipe',
        data: { pageTitle: 'vermegApp.equipe.home.title' },
        loadChildren: () => import('./equipe/equipe.module').then(m => m.EquipeModule),
      },
      {
        path: 'poste',
        data: { pageTitle: 'vermegApp.poste.home.title' },
        loadChildren: () => import('./poste/poste.module').then(m => m.PosteModule),
      },
      {
        path: 'projet',
        data: { pageTitle: 'vermegApp.projet.home.title' },
        loadChildren: () => import('./projet/projet.module').then(m => m.ProjetModule),
      },
      {
        path: 'status-employe',
        data: { pageTitle: 'vermegApp.statusEmploye.home.title' },
        loadChildren: () => import('./status-employe/status-employe.module').then(m => m.StatusEmployeModule),
      },
      {
        path: 'tache',
        data: { pageTitle: 'vermegApp.tache.home.title' },
        loadChildren: () => import('./tache/tache.module').then(m => m.TacheModule),
      },
      {
        path: 'vote',
        data: { pageTitle: 'vermegApp.vote.home.title' },
        loadChildren: () => import('./vote/vote.module').then(m => m.VoteModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
