export class VehicleTypeInfo {
    public vehicleTypeDesc: string;
}

export class VehicleFilter {
    public siteFilterForm: SiteFilter;
    public vehicleTypeFilterForm: VehicleTypeFilter;
    public fleetNo: string;
    public serialNo: string;
    public registrationNo: string;
    public vinNo: string;
    public remainingHours: string;
    public currentMachineHours: string;
    public serviceIntervalHours: string;
    public lastServiceDay: string;
    public lastServiceHours: string;
    public unit: string;
    public isUsable: boolean;
    public page: number;
    public size: number;
    public usable: boolean;

}

export class VehicleTypeFilter {
    public name: string;
}

export class SiteFilter {
    public description: string;
}
