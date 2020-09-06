import { User } from '../../general/user';
import { Image } from '../../general/image';

export class BreakDown {
    public uuid: string;
    public breakDownUuid: string;
    public breakDownStartDate: string;
    public breakDownEndDate: string;
    public jobCardStartDate: string;
    public jobCardEndDate: string;
    public lastUpdateDate: string;
    public fleetNumber: string;
    public reportNumber: string;
    public jobCardStatus: string;
    public siteName: string;
    public vehicleUuid: string;
    public operatorUuid: string;
    public reportType: string;
    public description: string;
    public operatorUser: User;
    public supervisorUser: User;
    public imageInfos: Image[];
}
