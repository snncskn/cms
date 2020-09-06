import { Image } from '../general/image';

export class ItemInfo {

      public partDetailTypeAttrInfos: null;
      public partInfo: Item;
      public partTypeInfo: null;
}
export class Item {
    public uuid: string;
    public barcode: string;
    public imageUuid: string;
    public itemDescription: string;
    public itemTypeInfo: ItemTypeInfo;
    public minStockQuantity: string;
    public storePartNumber: string;
    public unit: string;
    public imageInfos: Image[];
}
export class ItemTypeInfo {
    public uuid: string;
    public vehicleTypeAttributes: VehicleTypeAttributes[];
    public vehicleTypeDesc: string;
    public name: string;
}
export class VehicleType {
    public uuid: string;
    public vehicleTypeDesc: string;
    public vehicleTypeAttributes: VehicleTypeAttributes[];
}
export class VehicleTypeAttributes {
    public uuid: string;
    public vehicleAttributeName: string;
    public selectedVehicleAttrUuid:  string;
    public vehicleAttributeValues: VehicleAttributeValues[];
}

export class VehicleAttributeValues {
    public uuid: string;
    public vehicleAttributeValue: string;

}
export class ItemImage {
    public itemUuid: string;
    public imageInfos: Image[];
}
export class ApproveQuantityItem {
    public orderItemUuid: string;
    public transferItemUuid: string;
    public approveQuantity: string;
}
