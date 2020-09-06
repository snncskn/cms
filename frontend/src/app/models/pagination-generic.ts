
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
export class Data<T> {
    public content: T;
    public empty: boolean;
    public first: boolean;
    public last: boolean;
    public number: number;
    public totalElements: number;
    public totalPages: number;
    public numberOfElements: number;
    public pageable:  Pageable;
    public size: number;
    public sort: Sort;
}
export class Pagination<T> {
    public code: string;
    public msgId: string;
    public text: string;
    public type: string;
    public data: Data<T>;
}

