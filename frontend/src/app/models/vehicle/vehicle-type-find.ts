 export class VehicleTypeFind {
    public uuid: string;
    public vehicleTypeDesc: string;
    public vehicleTypeAttributes: VehicleTypeAttributes[];

}

export class VehicleTypeAttributes {
    public uuid: string;
    public vehicleAttributeName: string;
    public vehicleAttributeValues: VehicleAttributeValues[];
}

export class VehicleAttributeValues {
    public uuid: string;
    public vehicleAttributeValue: string;

}
