export enum RuStoreProductType {
  NON_CONSUMABLE,
  CONSUMABLE,
  SUBSCRIPTION,
}

export enum RuStoreProductStatus {
  ACTIVE,
  INACTIVE,
}

export enum RuStorePurchaseState {
  CREATED,
  INVOICE_CREATED,
  CONFIRMED,
  PAID,
  CANCELLED,
  CONSUMED,
  CLOSED,
  TERMINATED,
}
