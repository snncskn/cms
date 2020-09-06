
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
export class VehicleAttributeValue {
    public uuid: string;
    public value: string;
}
export class Content {
   public content: VehicleAttributeValue[];
}

export class PaginationAttrValue {
    public code: string;
    public msgId: string;
    public text: string;
    public type: string;
    public data: Content;

}
