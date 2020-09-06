
export class VehicleTypeAttributes {
    public uuid: string;
    public vehicleUuid: string;
    public vehicleTypeUuid: string;
    public selectedVehicleAttrUuid: string;
    public vehicleAttributeListDetailCreateForm: VehicleAttributeListDetailCreateForm[];
    public vehicleTypeAttributes: VehicleAttributeListDetailCreateForm[];
}


export class VehicleAttributeListDetailCreateForm {
    public uuid: string;
    public selectedVehicleAttrUuid: string;
    public vehicleAttributeUuid: string;
    public vehicleAttributeValueUuid: string;
}
