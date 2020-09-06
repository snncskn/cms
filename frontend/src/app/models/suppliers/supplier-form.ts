import {SupplierCreateUpdateForm} from './supplier-create-update-form';
import {SupplierContact} from './supplier-contact';
import { Image } from '../general/image';

export class SupplierForm {

    public uuid: string;
    public name: string;
    public address: string;
    public registerNumber: string;
    public taxNumber: string;
    public imageInfos: Image[];
    public contacts: SupplierContact[];
}
