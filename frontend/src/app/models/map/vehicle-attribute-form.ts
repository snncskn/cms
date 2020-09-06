export class VehicleAttributeEntity {
    public uuid: string;
    public vehicleAttributeValueDesc: string;
    public vehicleAttributeValueUuid: string;
}

export class VehicleAttributeForm {
    public vehicleAttributeValue: string;
    public vehicleTypeUuid: string;
    public uuid: string;
    public attribute: string;
    public vehicleAttributeUuid: string;
    public vehicleAttributeValues: VehicleAttributeEntity[];
}
