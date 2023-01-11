import { RuStoreProductStatus, RuStoreProductType, RuStorePurchaseState } from './enums';

interface RuStorePurchase {
  purchaseId: string;
  productId: string;
  description: string;
  language: string;
  purchaseTime: string;
  orderId: string;
  amountLabel: string;
  amount: number;
  currency: string;
  quantity: number;
  purchaseState: RuStorePurchaseState;
  developerPayload: string;
}

interface RuStoreProduct {
  productId: string;
  productType: RuStoreProductType;
  productStatus: RuStoreProductStatus;
  priceLabel: string;
  price: number;
  currency: string;
  language: string;
  title: string;
  description: string;
  imageUrl: any;
  promoImageUrl: any;
  subscription: RuStoreProductSubscription;
}

interface RuStoreProductSubscription {
  freeTrialPeriod: RuStoreSubscriptionPeriod;
  gracePeriod: RuStoreSubscriptionPeriod;
  introductoryPrice: string;
  introductoryPriceAmount: string;
  introductoryPricePeriod: RuStoreSubscriptionPeriod;
  subscriptionPeriod: RuStoreSubscriptionPeriod;
}

interface RuStoreSubscriptionPeriod {
  days: number;
  months: number;
  years: number;
}
