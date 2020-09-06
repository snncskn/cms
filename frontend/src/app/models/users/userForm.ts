import { Image } from '../general/image';
import { SiteForm } from '../sites/site-form';

export class UserForm {

    public uuid: string;
    public fullName: string;
    public firstName: string;
    public lastName: string;
    public email: string;
    public contactNumber: string;
    public imageUuid: string;
    public password: string;
    public verifyPassword: string;
    public expireDate: string;
    public staffNumber: string;
    public imageInfo: Image;
    public siteInfos: SiteForm[];
    public roleInfos: UserForm[];

}
