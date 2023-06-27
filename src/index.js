import { NativeModules, Platform } from 'react-native';
const LINKING_ERROR = `The package 'react-native-rustore-iap' doesn't seem to be linked. Make sure: \n\n` +
    Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
    '- You rebuilt the app after installing the package\n' +
    '- You are not using Expo managed workflow\n';
const RuStoreIap = NativeModules.RustoreIap
    ? NativeModules.RustoreIap
    : new Proxy({}, {
        get() {
            throw new Error(LINKING_ERROR);
        },
    });
export async function checkRuStoreAvailable() {
    return await RuStoreIap.checkRuStorePurchasesAvailability();
}
export function initializeRuStore(consoleAppId = '', deepLinkScheme = '', externalPaymentLoggerFactory = false) {
    RuStoreIap.initializeRuStore(consoleAppId, deepLinkScheme, externalPaymentLoggerFactory);
}
export async function confirmRuStorePurchase(purchaseId, developerPayload) {
    return await RuStoreIap.confirmRuStorePurchase(purchaseId, developerPayload);
}
export async function deleteRuStorePurchase(purchaseId) {
    return await RuStoreIap.deleteRuStorePurchase(purchaseId);
}
export async function purchaseRuStoreProduct(product, developerPayload) {
    return await RuStoreIap.purchaseRuStoreProduct(product, developerPayload);
}
export async function getRuStoreProducts(productsIds) {
    return await RuStoreIap.getRuStoreProducts(productsIds);
}
export async function getRuStorePurchases() {
    return await RuStoreIap.getRuStorePurchases();
}
export async function getRuStorePurchase(purchaseId) {
    return await RuStoreIap.getRuStorePurchase(purchaseId);
}
