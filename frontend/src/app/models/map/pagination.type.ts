
export class PaginationRequest {
    public itemsPerPage?: number;
    public page: number;
}


export class Sort {
    public unsorted: boolean;
    public sorted: boolean;
    public empty: boolean;

}
export class Pageable {
    public page: number;
    public size: number;
}
export class VehicleAttribute {
    public uuid: string;
    public name: string;
}
export class VehicleTypeAttribute {
    public uuid: string;
    public vehicleTypeAttributes: VehicleTypeAttributes[];

}

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
export class PaginationType {
    public code: string;
    public msgId: string;
    public text: string;
    public type: string;
    public data: VehicleTypeAttributes;
}
