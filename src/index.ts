import { NativeModules, Platform } from 'react-native';
import type { RuStoreProduct, RuStorePurchase } from './types';

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

export function checkRuStoreAvailable(
  cb: (error: any, isAvailable: boolean) => void
) {
  RustoreIap.checkRuStorePurchasesAvailability(cb);
}

export async function getRuStoreProducts(
  productsIds: String[]
): Promise<RuStoreProduct[]> {
  return await RustoreIap.getRuStoreProducts(productsIds);
}

export async function getRuStorePurchases(): Promise<RuStorePurchase[]> {
  return await RustoreIap.getRuStorePurchases();
}

// errors meaning
// Application not active or not found - Приложение с заданным applicationId не найдено в RuStore
