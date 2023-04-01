export var RuStoreProductType;
(function (RuStoreProductType) {
    RuStoreProductType[RuStoreProductType["NON_CONSUMABLE"] = 0] = "NON_CONSUMABLE";
    RuStoreProductType[RuStoreProductType["CONSUMABLE"] = 1] = "CONSUMABLE";
    RuStoreProductType[RuStoreProductType["SUBSCRIPTION"] = 2] = "SUBSCRIPTION";
})(RuStoreProductType || (RuStoreProductType = {}));
export var RuStoreProductStatus;
(function (RuStoreProductStatus) {
    RuStoreProductStatus[RuStoreProductStatus["ACTIVE"] = 0] = "ACTIVE";
    RuStoreProductStatus[RuStoreProductStatus["INACTIVE"] = 1] = "INACTIVE";
})(RuStoreProductStatus || (RuStoreProductStatus = {}));
export var RuStorePurchaseState;
(function (RuStorePurchaseState) {
    RuStorePurchaseState[RuStorePurchaseState["CREATED"] = 0] = "CREATED";
    RuStorePurchaseState[RuStorePurchaseState["INVOICE_CREATED"] = 1] = "INVOICE_CREATED";
    RuStorePurchaseState[RuStorePurchaseState["CONFIRMED"] = 2] = "CONFIRMED";
    RuStorePurchaseState[RuStorePurchaseState["PAID"] = 3] = "PAID";
    RuStorePurchaseState[RuStorePurchaseState["CANCELLED"] = 4] = "CANCELLED";
    RuStorePurchaseState[RuStorePurchaseState["CONSUMED"] = 5] = "CONSUMED";
    RuStorePurchaseState[RuStorePurchaseState["CLOSED"] = 6] = "CLOSED";
    RuStorePurchaseState[RuStorePurchaseState["TERMINATED"] = 7] = "TERMINATED";
})(RuStorePurchaseState || (RuStorePurchaseState = {}));
