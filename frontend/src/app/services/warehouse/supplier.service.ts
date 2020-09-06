import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { catchError } from 'rxjs/operators';
import { Pageable, Pagination } from 'src/app/models/pagination-generic';
import { SupplierCreateUpdateForm } from '../../models/suppliers/supplier-create-update-form';
import { RestResponse } from '../../models/general/rest-response';
import { SupplierForm } from '../../models/suppliers/supplier-form';
import { SupplierContact } from 'src/app/models/suppliers/supplier-contact';
import { SupplierImageCreateForm } from 'src/app/models/suppliers/supplier-image';
import { AuthenticationService } from '../general/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class SupplierService {
  private readonly endpoint = 'suppliers';
  constructor(
    private readonly httpClient: HttpClient,
    private readonly authenticationService: AuthenticationService

  ) {

  }

  public create(supplier: SupplierCreateUpdateForm): Observable<RestResponse<SupplierCreateUpdateForm>> {
    if (supplier.uuid === null) {
      return this.save(supplier);
    } else {
      return this.update(supplier);
    }
  }
  public createSupplierContact(contact: SupplierContact): Observable<RestResponse<SupplierContact>> {
    if (contact.uuid === null) {
      return this.saveSupplierContact(contact);
    } else {
      return this.updateSupplierContact(contact);
    }
  }
  public save(supplierCreateUpdateForm: SupplierCreateUpdateForm): Observable<RestResponse<SupplierForm>> {

    return this.httpClient.post<RestResponse<SupplierForm>>
      (`${environment.url}/${this.endpoint}/`, supplierCreateUpdateForm);
  }
  public createImages(supplierImageCreateForm: SupplierImageCreateForm): Observable<RestResponse<SupplierForm>> {

    return this.httpClient.post<RestResponse<SupplierForm>>
      (`${environment.url}/${this.endpoint}/create-images`, supplierImageCreateForm);
  }
  public update(supplier: SupplierCreateUpdateForm): Observable<RestResponse<SupplierCreateUpdateForm>> {
    return this.httpClient.put<RestResponse<SupplierCreateUpdateForm>>
      (`${environment.url}/${this.endpoint}?uuid=${supplier.uuid}`, supplier);
  }
  public saveSupplierContact(contact: SupplierContact): Observable<RestResponse<SupplierContact>> {

    return this.httpClient.post<RestResponse<SupplierContact>>(`
                            ${environment.url}/${this.endpoint}/create-contact`, contact);
  }
  public updateSupplierContact(contact: SupplierContact): Observable<RestResponse<SupplierContact>> {
    return this.httpClient.put<RestResponse<SupplierContact>>
      (`${environment.url}/${this.endpoint}/update-contact?uuid=${contact.uuid}`, contact);
  }
  public delete(uuid: string): Observable<SupplierForm> {

    return this.httpClient.delete<SupplierForm>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
  }
  public deleteContact(uuid: string): Observable<SupplierForm> {

    return this.httpClient.delete<SupplierForm>(`
          ${environment.url}/${this.endpoint}/delete-supplier-contact?contactUuid=${uuid}`);
  }

  public find(uuid: string): Observable<RestResponse<SupplierForm>> {

    return this.httpClient.get<RestResponse<SupplierForm>>(`${environment.url}/${this.endpoint}/${uuid}`);
  }

  public get(path: string, params: HttpParams = new HttpParams()): Observable<any> {

    return this.httpClient.get(`${environment.url}${path}`, { params })
      .pipe(catchError(this.formatErrors));
  }

  public put(path: string, body: Object = {}): Observable<any> {

    return this.httpClient.put(
      `${environment.url}${path}`,
      JSON.stringify(body)
    ).pipe(catchError(this.formatErrors));
  }

  public post(path: string, body: Object = {}): Observable<any> {

    return this.httpClient.post(
      `${environment.url}${path}`,
      JSON.stringify(body)
    ).pipe(catchError(this.formatErrors));
  }

  public list(page: Pageable): Observable<Pagination<SupplierForm[]>> {
    return this.httpClient
      .get<Pagination<SupplierForm[]>>(
        `${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
  }
  public filter(page: Pageable, filter: string): Observable<Pagination<SupplierForm[]>> {
    return this.httpClient
      .get<Pagination<SupplierForm[]>>
      (`${environment.url}/${this.endpoint}/filter/${filter}?page=${page.page}&size=${page.size}`);
  }

  public postForFile(page: Pageable, filter: string): Observable<Blob> {
    const currentUser = this.authenticationService.currentUserValue;
    return this.httpClient.post(`${environment.url}/${this.endpoint}/xlsExcel`, filter, {
      headers: {
        Authorization: `Bearer ${currentUser.token}`,
        Site: ` ${currentUser.currentSite.uuid}`
      },
      responseType: 'blob'
    });
  }

  private readonly formatErrors: (error: any) => Observable<never> = (error: any) => {

    return throwError(error.error);
  }



}
