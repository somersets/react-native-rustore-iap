import { NativeModules, Platform } from 'react-native';

import type {
  RuStoreProductStatus,
  RuStoreProductType,
  RuStorePurchaseState,
} from './enums';

const LINKING_ERROR =
  `The package 'react-native-rustore-iap' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const RuStoreIap: RuStoreIapModule = NativeModules.RustoreIap
  ? NativeModules.RustoreIap
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

interface RuStoreIapModule {
  checkRuStorePurchasesAvailability: () => Promise<Boolean | String>;
  initializeRuStore: (
    consoleAppId: string,
    deepLinkScheme?: string,
    externalPaymentLoggerFactory?: boolean
  ) => void;
  purchaseRuStoreProduct: (
    product: RuStoreProduct,
    developerPayload?: string
  ) => Promise<PaymentResult | InvalidPurchaseResult>;
  getRuStoreProducts: (productIds: String[]) => Promise<RuStoreProduct[]>;
  getRuStorePurchases: () => Promise<RuStorePurchase[]>;
  confirmRuStorePurchase: (
    purchaseId: string,
    developerPayload?: string
  ) => Promise<ConfirmPurchaseResponse>;
  deleteRuStorePurchase: (
    purchaseId: string
  ) => Promise<DeletePurchaseResponse>;
}

export async function checkRuStoreAvailable(): Promise<Boolean | String> {
  return await RuStoreIap.checkRuStorePurchasesAvailability();
}

export function initializeRuStore(
  consoleAppId: string = '',
  deepLinkScheme: string = '',
  externalPaymentLoggerFactory: boolean = false
): void {
  RuStoreIap.initializeRuStore(
    consoleAppId,
    deepLinkScheme,
    externalPaymentLoggerFactory
  );
}

export async function confirmRuStorePurchase(
  purchaseId: string,
  developerPayload?: string
): Promise<ConfirmPurchaseResponse> {
  return await RuStoreIap.confirmRuStorePurchase(purchaseId, developerPayload);
}

export async function deleteRuStorePurchase(
  purchaseId: string
): Promise<DeletePurchaseResponse> {
  return await RuStoreIap.deleteRuStorePurchase(purchaseId);
}

export async function purchaseRuStoreProduct(
  product: RuStoreProduct,
  developerPayload?: string
): Promise<PaymentResult | InvalidPurchaseResult> {
  return await RuStoreIap.purchaseRuStoreProduct(product, developerPayload);
}

export async function getRuStoreProducts(
  productsIds: String[]
): Promise<RuStoreProduct[]> {
  return await RuStoreIap.getRuStoreProducts(productsIds);
}

export async function getRuStorePurchases(): Promise<RuStorePurchase[]> {
  return await RuStoreIap.getRuStorePurchases();
}

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
  subscriptionToken: string;
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

interface PaymentResult {
  purchaseId: string;
  productId: string;
  orderId: string;
  subscriptionToken: string;
  finishCode: string;
}

interface ResponseWithCode {
  code: number;
  errorDescription: string;
  errorMessage: string;
  errors: string[];
  meta: RequestMeta;
}

export interface ConfirmPurchaseResponse extends ResponseWithCode {}

export interface DeletePurchaseResponse extends ResponseWithCode {}

export interface RequestMeta {
  traceId: string;
}

export interface InvalidPurchaseResult {
  purchaseId: string;
  invoiceId: string;
  orderId: string;
  quantity: number;
  productId: string;
  errorCode: number;
}

export interface RuStoreSubscriptionPeriod {
  days: number;
  months: number;
  years: number;
}
