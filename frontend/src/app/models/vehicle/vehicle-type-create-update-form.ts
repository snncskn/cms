import {VehicleTypeAttributeDto} from '../map/VehicleTypeAttributeDto';

export class VehicleTypeCreateUpdateForm {
    public uuid: string;
    public name: string;
    public vehicleTypeDesc: string;
    public vehicleTypeAttributes: VehicleTypeAttributeDto[];
    public msgId;
    public text;
    public type;
    public vehicleTypeAttributeCreateUpdateForms: VehicleTypeAttributeDto[];
}
