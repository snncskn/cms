
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

export class DataPag {
    public content: VehicleAttribute[];

}

export class PaginationAttr {
    public code: string;
    public msgId: string;
    public text: string;
    public type: string;
    public data: DataPag;

}
