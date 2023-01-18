import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-rustore-iap' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const RustoreIap = NativeModules.RustoreIap
  ? NativeModules.RustoreIap
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export async function checkRuStoreAvailable(): Promise<Boolean> {
  return await RustoreIap.checkRuStorePurchasesAvailability();
}

export async function getRuStoreProducts(
  productsIds: String[]
): Promise<RuStoreProduct[]> {
  return await RustoreIap.getRuStoreProducts(productsIds);
}

export async function getRuStorePurchases(): Promise<RuStorePurchase[]> {
  return await RustoreIap.getRuStorePurchases();
}

import type {
  RuStoreProductStatus,
  RuStoreProductType,
  RuStorePurchaseState,
} from './enums';

export interface RuStorePurchase {
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

export interface RuStoreProduct {
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

export interface RuStoreProductSubscription {
  freeTrialPeriod: RuStoreSubscriptionPeriod;
  gracePeriod: RuStoreSubscriptionPeriod;
  introductoryPrice: string;
  introductoryPriceAmount: string;
  introductoryPricePeriod: RuStoreSubscriptionPeriod;
  subscriptionPeriod: RuStoreSubscriptionPeriod;
}

export interface RuStoreSubscriptionPeriod {
  days: number;
  months: number;
  years: number;
}
