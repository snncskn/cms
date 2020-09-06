import {VehicleTypeAttributeDto} from './VehicleTypeAttributeDto';

export class VehicleTypeFind {
    public uuid: string;
    public vehicleTypeDesc: string;
    public data: VehicleTypeAttributeDto[];
    public msgId: string;
    public text: string;
    public type: string;
    public vehicleTypeAttributeCreateUpdateForms: VehicleTypeAttributeDto[];
}
