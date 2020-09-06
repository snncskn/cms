import { User } from '../../general/user';

export class JobCardUpdateForm {
    public jobCardUuid: string;
    public operatorUserUuid: string;
    public mechanicUserUuid: string;
    public foremanUserUuid: string;
    public riskAssessment: boolean;
    public wheelNuts: boolean;
    public oilLevel: boolean;
    public machineGrease: boolean;
    public lockOutProcedure: boolean;
}
