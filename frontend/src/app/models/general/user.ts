import { SiteForm } from '../sites/site-form';
import { Image } from './image';
import { RoleInfo } from './role-info';

export class User {
    public id: number;
    public uuid: string;
    public username: string;
    public fullName: string;
    public firstName: string;
    public lastName: string;
    public contactNumber: string;
    public email: string;
    public imageUuid: string;
    public staffNumber: string;
    public profileImage: string;
    public token: string;
    public siteInfo: SiteForm;
    public imageInfo: Image;
    public siteInfos: SiteForm[];
    public roleInfos: RoleInfo[];
    public currentSite: SiteForm;
}
