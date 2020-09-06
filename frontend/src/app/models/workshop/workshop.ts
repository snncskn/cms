import {Vehicle} from '../../modules/vehicle/vehicle.module';
import {User} from '../general/user';
import { Image } from '../general/image';

export class Workshop {

    public vehicle: Vehicle;
    public uuid: string;
    public fleetNumber: string;
    public lastUpdateDate: string;
    public siteName: string;
    public reportNumber: string;
    public jobCardStatus: string;
    public startDate: string;
    public endDate: string;
    public breakDownStartDate: string;
    public breakDownEndDate: string;
    public jobCardEndDate: string;
    public jobCardStartDate: string;
    public description: string;
    public currentMachineHours: string;
    public reportType: string;
    public operatorUser: User ;
    public supervisorUser: User;
    public mechanicUser: User;
    public foremanUser: User;
    public imageInfos: Image[];
}


