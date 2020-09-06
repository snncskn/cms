
export class VehicleAttributeEntity {
   public uuid: string;
   public vehicleAttributeValue: string;
   public vehicleAttributeValueUuid: string;
}

export class VehicleAttributeValueCreateUpdateForm {

   public data: VehicleAttributeEntity;
   public desc: string;
   public vehicleAttributeUuid: string;
   public attributeValueUuid: string;
   public uuid: string;
   public vehicleAttributeValue: string;
}
