import { WorkshopImageCreateForm } from './image-create-form';
import { Image } from '../../general/image';

export class JobCardCreateUpdateForm {

    public jobCardUuid: string;
    public operatorUuid: string;
    public imageInfos: Image[];
}
