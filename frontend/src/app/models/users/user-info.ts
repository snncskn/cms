import { Image } from '../general/image';
import { SiteForm } from '../sites/site-form';
import { RoleForm } from '../roles/role-form';

export class UserInfo {
    public uuid: string;
    public firstName: string;
    public lastName: string;
    public email: string;
    public position: string;
    public contactNumber: string;
    public imageUuid: string;
    public password: string;
    public verifyPassword: string;
    public expireDate: string;
    public staffNumber: string;
    public imageInfos: Image[];
    public imageInfo: Image;
    public siteInfos: SiteForm[];
    public roleInfos: RoleForm[];

}

