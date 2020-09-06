export class SupplierImageCreateForm {
    public supplierUuid: string;
    public imageInfos: ImageInfo[];

}

export class ImageInfo {
    public uuid: string;
    public selected: boolean;
    public downloadUrl: string;
}
