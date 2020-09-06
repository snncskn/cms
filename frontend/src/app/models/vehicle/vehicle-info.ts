import { Image } from '../general/image';
import { SiteForm } from '../sites/site-form';

export class VehicleInfo {
    public vehicleDetailTypeAttrInfos: [];
    public vehicleInfo: Vehicle;
    public vehicleTypeInfo: VehicleTypeInfo;
    public imageInfos: Image[];
}
export class VehicleTypeInfo {
    public uuid: string;
    public vehicleTypeAttributes: string;
    public vehicleTypeDesc: string;

}
export class Vehicle {

    public uuid: string;
    public fleetNo: string;
    public serialNo: string;
    public registrationNo: string;
    public vinNo: string;
    public currentMachineHours: string;
    public serviceIntervalHours: string;
    public lastServiceDay: string;
    public lastServiceHours: string;
    public vehicleTypeUuid: string;
    public vehicleType: VehicleType;
    public imageUuid: string;
    public siteInfo: SiteForm;
    public imageInfos: Image[];
    public remainingHours: string;
    public serviceWarning: string;

}

export class VehicleType {
    public uuid: string;
    public name: string;
    public vehicleTypeAttributes: VehicleTypeAttributes[];
}
export class VehicleTypeAttributes {
    public uuid: string;
    public selectedVehicleAttrUuid: string;
    public vehicleAttributeName: string;
    public vehicleAttributeValues: VehicleAttributeValues[];
}


export class VehicleAttributeValues {
    public uuid: string;
    public vehicleAttributeValue: string;
}

export class VehicleImage {
    public vehicleUuid: string;
    public imageInfos: Image[];
}
