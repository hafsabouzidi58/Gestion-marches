import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrestataireList } from './prestataire-list';

describe('PrestataireList', () => {
  let component: PrestataireList;
  let fixture: ComponentFixture<PrestataireList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrestataireList],
    }).compileComponents();

    fixture = TestBed.createComponent(PrestataireList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
