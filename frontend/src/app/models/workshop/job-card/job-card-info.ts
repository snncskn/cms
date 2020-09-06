import { User } from '../../general/user';
import { VehicleInfo } from '../../vehicle/vehicle-info';

export class JobCardInfo {
    public uuid: string;
    public kmHour: string;
    public fleetNumber: string;
    public currentKmHour: string;
    public requestNumber: string;
    public vehicleTypeName: string;
    public jobCardStartDate: string;
    public jobCardEndDate: string;
    public operatorUser: User;
    public mechanicUser: User;
    public foremanUser: User;
    public supervisorUser: User;
    public jobCardStatus: string;
    public riskAssessment: boolean;
    public lockOutProcedure: boolean;
    public wheelNuts: boolean;
    public oilLevel: boolean;
    public machineGrease: boolean;
    public vehicleInfo: VehicleInfo;
    public description: string;

}
