
import { User } from '../../general/user';
import { Pageable, Pagination } from 'src/app/models/pagination-generic';

export class BreakDownSearch {
    public startDate: string;
    public endDate: string;
    public vehicleUuid: string;
    public filter: string;
    public breakDownStartDate: string;
    public requestStartDate: string;
    public breakDownEndDate: string;
    public requestEndDate: string;
    public page: number;
    public size: number;
    public jobCardNumber: string;
    public fleetNumber: string;
    public requestUser: string;
    public reportNumber: string;
    public stockCode: string;
    public itemDescription: string;
    public status: string;
    public site: string;

}
