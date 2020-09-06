import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { RestResponse, RestResponseEntity, RestResponseUploadEntity } from 'src/app/models/general/rest-response';
import { Image } from 'src/app/models/general/image';
import { Observable } from 'rxjs';
import { WebcamImage } from 'ngx-webcam';

@Injectable({
  providedIn: 'root'
})
export class ImageService {

  private readonly endpoint = 'image';
  constructor(private readonly httpClient: HttpClient) { }
  public upload = (file: File) => {
    const formdata: FormData = new FormData();
    formdata.append('file', file);

    return this.httpClient.post<RestResponse<Image>>
      (`${environment.url}/${this.endpoint}`, formdata);
  }
  public webcam = (webcamImage: WebcamImage) => {
    const formdata: FormData = new FormData();
    const date = new Date().valueOf();
    const imageName = date + '.jpeg';
    const imageBlob = this.dataURItoBlob(webcamImage.imageAsBase64);
    const imageFile = new File([imageBlob], imageName, { type: 'image/jpeg' });
    formdata.append('file', imageFile);

    return this.httpClient.post<RestResponse<Image>>
      (`${environment.url}/${this.endpoint}`, formdata);
  }
  public find(uuid: string): Observable<RestResponse<Image>> {
      return this.httpClient.get<RestResponse<Image>>(`${environment.url}/${this.endpoint}/${uuid}`);
  }
  public delete(uuid: string): Observable<RestResponse<Image>> {
    return this.httpClient.delete<RestResponse<Image>>(`${environment.url}/${this.endpoint}/${uuid}`);
  }

  public dataURItoBlob(dataURI: any): Blob {
    const byteString = window.atob(dataURI);
    const arrayBuffer = new ArrayBuffer(byteString.length);
    const int8Array = new Uint8Array(arrayBuffer);
    for (let i = 0; i < byteString.length; i++) {
      int8Array[i] = byteString.charCodeAt(i);
    }
    const blob = new Blob([int8Array], { type: 'image/jpeg' });
    return blob;
 }

}
