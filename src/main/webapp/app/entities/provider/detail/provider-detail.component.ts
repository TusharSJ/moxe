import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProvider } from '../provider.model';

@Component({
  selector: 'jhi-provider-detail',
  templateUrl: './provider-detail.component.html',
})
export class ProviderDetailComponent implements OnInit {
  provider: IProvider | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ provider }) => {
      this.provider = provider;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
