export class VehicleAttributeEntiy {
   public attribute: string;
   public uuid: string;
   public vehicleAttributeValues: [];
}


export class VehicleAttributeCreateUpdateForm {

    public code: string;
    public attribute: string;
    public vehicleAttributeValues: [];
    public msgId: string;
    public text:  string;
    public type: string;
    public vehicleAttribute: string;
    public uuid: string;
    public data: VehicleAttributeEntiy;
}
