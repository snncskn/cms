import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Validators, FormGroup, FormBuilder } from '@angular/forms';

import * as Chartist from 'chartist';
import { Router } from '@angular/router';
import { AuthenticationService } from '../services/general/authentication.service';
import { User } from '../models/general/user';
import { Image } from '../models/general/image';
import { ImageService } from '../services/general/image.service';
import { ToastrManager } from 'ng6-toastr-notifications';

declare const $: any;

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
  public user: User;
  public profileForm: FormGroup;
  public image: Image;
  public fileData: File = null;
  public operationLock: boolean;

  constructor(private readonly router: Router,
    private readonly formBuilder: FormBuilder,
    private readonly imageService: ImageService,
    public toastr: ToastrManager,
    private readonly authenticationService: AuthenticationService) {

    this.image = new Image();
    this.image.downloadUrl = './assets/img/placeholder.jpg';
    this.user = this.authenticationService.currentUserValue;
    if (this.user.imageUuid) {
      this.imageService.find(this.user.imageUuid).subscribe(image => {
        this.image = image.data;
        this.user.profileImage = image.data.downloadUrl;
      });
    }
    this.profileForm = this.formBuilder.group({
      uuid: [null],
      contactNumber: [this.user.contactNumber],
      email: [this.user.email],
      firstName: [this.user.firstName],
      lastName: [this.user.lastName],
      imageUuid: [this.user.imageUuid]
    });
  }
  public onFileChanged = (event) => {
    this.fileData = <File>event.target.files[0];
    this.imageService.upload(this.fileData).subscribe(data => {
      this.profileForm.controls['imageUuid'].setValue(data.data.downloadUrl);
      this.user.imageUuid = data.data.uuid;
      this.imageService.find(this.user.imageUuid).subscribe(image => {
        this.image = image.data;
        this.user.profileImage = image.data.downloadUrl;
      });
      localStorage.setItem('currentUser', JSON.stringify(this.user));
      this.toastr.successToastr('Success', 'Image updated');

    });
  }
  public ngOnInit(): void {
  }

  public ngOnSubmit = () => {

  }
}
